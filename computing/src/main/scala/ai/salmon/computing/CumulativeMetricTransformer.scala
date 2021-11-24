package ai.salmon.computing

import org.apache.spark.ml.Transformer
import org.apache.spark.ml.param.{ Param, ParamMap, StringArrayParam }
import org.apache.spark.ml.util.{ DefaultParamsWritable, Identifiable }
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{ DoubleType, StringType, StructField, StructType }
import org.apache.spark.sql.{ DataFrame, Dataset, Row }

import scala.collection.mutable
import scala.util.hashing.MurmurHash3

/**
 * Transformer to aggregate data by experimental unit with additional
 * functionality like bucketing and ratio metric computing
 * @param uid -  uid for transformer
 */
class CumulativeMetricTransformer(override val uid: String)
    extends Transformer
    with DefaultParamsWritable
    with BaseStatisticTransformerParameters {

  def this() = this(Identifiable.randomUID("cumulativeMetricTransformer"))

  val numeratorMetrics: StringArrayParam = new StringArrayParam(
    this,
    "numeratorMetrics",
    "Numerator metrics names"
  )

  val denominatorMetrics: StringArrayParam = new StringArrayParam(
    this,
    "denominatorMetrics",
    "Denominator metric names"
  )

  val ratioMetrics: StringArrayParam = new StringArrayParam(
    this,
    "ratioMetrics",
    "New ratio metric names"
  )

  val numBuckets: Param[Int] = new Param[Int](
    this,
    "numBuckets",
    "Change entity uid to synth buckets"
  )
  setDefault(numBuckets, -1)

  override def transform(dataset: Dataset[_]): DataFrame = {
    import dataset.sparkSession.implicits._

    assert(
      $(numeratorMetrics).length == $(denominatorMetrics).toSeq.length,
      "Numerator metrics must have same size with denominator metrics"
    )
    assert(
      $(numeratorMetrics).length == $(ratioMetrics).length,
      "ratioMetricNames metrics must have same size with denominator and numerator metrics"
    )

    val columns = constructColumnsToAggregate(withName = true, withAdditive = true)

    val additiveCumulativeMetrics = dataset
      .filter($(additiveColumn))
      .withColumn(
        $(entityIdColumn),
        if ($(numBuckets) > 0)
          uidToBucket($(numBuckets))(col($(entityIdColumn)), col($(experimentColumn)))
        else col($(entityIdColumn))
      )
      .groupBy(
        columns.head,
        columns.tail: _*
      )
      .agg(sum($(valueColumn)) as $(valueColumn))

    val nonAdditiveMetrics = dataset
      .filter(!col($(additiveColumn)))

    val ratioValues = ($(numeratorMetrics) ++ $(denominatorMetrics)).distinct

    val metrics = ratioValues match {
      case values if values.nonEmpty => {
        val ratioData = ($(numeratorMetrics), $(denominatorMetrics), $(ratioMetrics)).zipped
          .map(RatioMetricData)
        val ratioColumns = constructColumnsToAggregate(withName = false, withAdditive = false)
        val cumulativeRatioMetrics = additiveCumulativeMetrics
          .filter(col($(metricNameColumn)).isin(values: _*))
          .groupBy(ratioColumns.head, ratioColumns.tail: _*)
          .agg(
            ratioUdf(ratioData)(
              collect_list(struct($(metricNameColumn), $(valueColumn)))
            ) as "ratioMetrics"
          )
          .select(Seq(explode($"ratioMetrics") as "ratioMetric") ++ ratioColumns.map(col): _*)
          .withColumn($(metricNameColumn), $"ratioMetric.metricName")
          .withColumn($(valueColumn), $"ratioMetric.metricValue")
          .withColumn($(additiveColumn), lit(false))
          .drop("ratioMetric")

        additiveCumulativeMetrics.union(
          cumulativeRatioMetrics.select(additiveCumulativeMetrics.columns.map(col): _*)
        )
      }
      case _ => additiveCumulativeMetrics
    }

    metrics
      .union(
        nonAdditiveMetrics
          .select(additiveCumulativeMetrics.columns.map(col): _*)
      )
  }

  override def copy(extra: ParamMap): Transformer = defaultCopy(extra)

  override def transformSchema(schema: StructType): StructType = {
    StructType(
      constructColumnsToAggregate(withName = true, withAdditive = true)
        .map(value => StructField(value, StringType, nullable = false)) ++ Array(
        StructField($(valueColumn), DoubleType, nullable = true)
      )
    )
  }

  /** @group setParam */
  def setNumBuckets(value: Int): this.type =
    set(numBuckets, value)

  /** @group setParam */
  def setNumeratorNames(value: Array[String]): this.type =
    set(numeratorMetrics, value)

  /** @group setParam */
  def setDenominatorNames(value: Array[String]): this.type =
    set(denominatorMetrics, value)

  /** @group setParam */
  def setRatioNames(value: Array[String]): this.type =
    set(ratioMetrics, value)

  private def ratioUdf(pairs: Seq[RatioMetricData]): UserDefinedFunction = udf {
    metrics: mutable.WrappedArray[Row] =>
      val epsilon = 1e-10d

      val metricsMapping = metrics
        .map(metric => metric.getAs[String]("metricName") -> metric.getAs[Double]("metricValue"))
        .toMap

      pairs.map(pair => {
        val nominator = metricsMapping.get(pair.metricNominator) match {
          case Some(x) => x
          case None    => 0d
        }

        metricsMapping.get(pair.metricDenominator) match {
          case Some(x) =>
            if (x < epsilon) Metric(pair.newName, 0) else Metric(pair.newName, nominator / x)
          case None => Metric(pair.newName, 0)
        }
      })
  }

  private def uidToBucket(numBuckets: Int): UserDefinedFunction = udf {
    (entityUid: String, experimentUid: String) =>
      val hash = MurmurHash3.stringHash(entityUid ++ experimentUid)
      math.abs(math.max(Int.MinValue + 1, hash) % numBuckets).toString
  }

  private def constructColumnsToAggregate(withName: Boolean, withAdditive: Boolean): Seq[String] = {
    Seq($(variantColumn), $(entityIdColumn), $(experimentColumn), $(metricSourceColumn)) ++
      (if (withName) Seq($(metricNameColumn)) else Seq[String]()) ++
      (if (withAdditive) Seq($(additiveColumn)) else Seq[String]())
  }
}

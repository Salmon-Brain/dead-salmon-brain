package ai.salmonbrain.ruleofthumb

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

  val numeratorNames: StringArrayParam = new StringArrayParam(
    this,
    "numeratorNames",
    "numerator metrics names"
  )
  setDefault(numeratorNames, Array[String]())

  val denominatorNames: StringArrayParam = new StringArrayParam(
    this,
    "denominatorNames",
    "denominator metric names"
  )
  setDefault(denominatorNames, Array[String]())

  val ratioNames: StringArrayParam = new StringArrayParam(
    this,
    "ratioNames",
    "new ratio metric names"
  )
  setDefault(ratioNames, Array[String]())

  val numBuckets: Param[Int] = new Param[Int](
    this,
    "numBuckets",
    "change entity uid to synth buckets"
  )
  setDefault(numBuckets, -1)

  val nonAdditiveAggFunc: Param[String] = new Param[String](
    this,
    "nonAdditiveAggFunc",
    "non additive metrics aggregation"
  )
  setDefault(nonAdditiveAggFunc, "mean")

  override def transform(dataset: Dataset[_]): DataFrame = {
    import dataset.sparkSession.implicits._

    assert(
      $(numeratorNames).length == $(denominatorNames).toSeq.length,
      "Numerator metrics must have same size with denominator metrics"
    )
    assert(
      $(numeratorNames).length == $(ratioNames).length,
      "ratioMetricNames metrics must have same size with denominator and numerator metrics"
    )

    val columns = constructColumnsToAggregate(withName = true, withAdditive = true)
    val data = dataset
      .withColumn(
        $(entityIdColumn),
        if ($(numBuckets) > 0)
          uidToBucket($(numBuckets))(col($(entityIdColumn)), col($(experimentColumn)))
        else col($(entityIdColumn))
      )

    val additiveCumulativeMetrics = data
      .filter($(additiveColumn))
      .groupBy(
        columns.head,
        columns.tail: _*
      )
      .agg(sum($(valueColumn)) as $(valueColumn))

    val nonAdditiveMetrics = data
      .filter(!col($(additiveColumn)))
      .groupBy(
        columns.head,
        columns.tail: _*
      )
      .agg(expr(s"${$(nonAdditiveAggFunc)}(${$(valueColumn)})") as $(valueColumn))

    val ratioValues = ($(numeratorNames) ++ $(denominatorNames)).distinct

    val metrics = ratioValues match {
      case values if values.nonEmpty => {
        val ratioData = ($(numeratorNames), $(denominatorNames), $(ratioNames)).zipped
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
    set(numeratorNames, value)

  /** @group setParam */
  def setDenominatorNames(value: Array[String]): this.type =
    set(denominatorNames, value)

  /** @group setParam */
  def setRatioNames(value: Array[String]): this.type =
    set(ratioNames, value)

  /** @group setParam */
  def setNonAdditiveAggFunc(value: String): this.type =
    set(nonAdditiveAggFunc, value)

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
    Seq(
      $(variantColumn),
      $(entityIdColumn),
      $(experimentColumn),
      $(metricSourceColumn),
      $(entityCategoryNameColumn),
      $(entityCategoryValueColumn)
    ) ++
      (if (withName) Seq($(metricNameColumn)) else Seq[String]()) ++
      (if (withAdditive) Seq($(additiveColumn)) else Seq[String]())
  }
}

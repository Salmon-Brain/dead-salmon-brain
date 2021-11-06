package ai.salmon.computing

import org.apache.spark.ml.Transformer
import org.apache.spark.ml.param.{ Param, ParamMap }
import org.apache.spark.ml.util.{ DefaultParamsWritable, Identifiable }
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{ DoubleType, StringType, StructField, StructType }
import org.apache.spark.sql.{ DataFrame, Dataset, Row }

import scala.collection.mutable
import scala.util.hashing.MurmurHash3

class CumulativeMetricTransformer(override val uid: String)
    extends Transformer
    with DefaultParamsWritable
    with BaseStatisticTransformerParameters {

  def this() = this(Identifiable.randomUID("cumulativeMetricTransformer"))

  val ratioMetricData: Param[Seq[RatioMetricData]] = new Param[Seq[RatioMetricData]](
    this,
    "ratioMetricData",
    "Data structure to compute ratio metrics"
  )
  setDefault(ratioMetricData, Seq[RatioMetricData]())

  val numBuckets: Param[Int] = new Param[Int](
    this,
    "numBuckets",
    "Change entity uid to synth buckets"
  )
  setDefault(numBuckets, -1)

  override def transform(dataset: Dataset[_]): DataFrame = {
    import dataset.sparkSession.implicits._

    val columns = constructColumnsToAggregate(withName = true, withAdditive = true)

    val cumulativeMetrics = dataset
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

    val ratioValues =
      $(ratioMetricData).flatMap(r => Seq(r.metricNominator, r.metricDenominator)).distinct

    ratioValues match {
      case values if values.nonEmpty => {
        val ratioColumns = constructColumnsToAggregate(withName = false, withAdditive = false)
        val cumulativeRatioMetrics = cumulativeMetrics
          .filter(col($(metricNameColumn)).isin(values: _*))
          .groupBy(ratioColumns.head, ratioColumns.tail: _*)
          .agg(
            ratioUdf($(ratioMetricData))(
              collect_list(struct($(metricNameColumn), $(valueColumn)))
            ) as "ratioMetrics"
          )
          .select(Seq(explode($"ratioMetrics") as "ratioMetric") ++ ratioColumns.map(col): _*)
          .withColumn($(metricNameColumn), $"ratioMetric.metricName")
          .withColumn($(valueColumn), $"ratioMetric.metricValue")
          .withColumn($(additiveColumn), lit(false))
          .drop("ratioMetric")

        cumulativeMetrics.union(
          cumulativeRatioMetrics.select(cumulativeMetrics.columns.map(col): _*)
        )

      }
      case _ => cumulativeMetrics
    }

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
  def setRatioMetricsData(value: Seq[RatioMetricData]): this.type =
    set(ratioMetricData, value)

  def ratioUdf(pairs: Seq[RatioMetricData]): UserDefinedFunction = udf {
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

  def uidToBucket(numBuckets: Int): UserDefinedFunction = udf {
    (entityUid: String, experimentUid: String) =>
      val hash = MurmurHash3.stringHash(entityUid ++ experimentUid)
      math.abs(math.max(Int.MinValue + 1, hash) % numBuckets).toString
  }

  def constructColumnsToAggregate(withName: Boolean, withAdditive: Boolean): Seq[String] = {
    Seq($(variantColumn), $(entityIdColumn), $(experimentColumn), $(metricSourceColumn)) ++
      (if (withName) Seq($(metricNameColumn)) else Seq[String]()) ++
      (if (withAdditive) Seq($(additiveColumn)) else Seq[String]())
  }
}

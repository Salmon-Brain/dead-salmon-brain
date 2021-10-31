package ai.salmon.computing

import org.apache.spark.ml.util.Identifiable
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{ DataFrame, Dataset, Row }

class WelchStatisticsTransformer(override val uid: String) extends BaseStatisticTransformer {
  def this() = this(Identifiable.randomUID("welchStatisticsTransformer"))

  override def transform(dataset: Dataset[_]): DataFrame = {
    import dataset.sqlContext.implicits._
    dataset
      .groupBy(
        $(experimentColumn),
        $(metricNameColumn),
        $(additiveColumn),
        $(metricSourceColumn)
      )
      .pivot($(variantColumn))
      .agg(
        struct(
          avg(col($(valueColumn))) as "mean",
          variance(col($(valueColumn))) as "variance",
          count(col($(valueColumn))) as "length"
        )
      )
      .withColumn("statisticsData", doStatistic($(alpha))($"control", $"treatment"))
      .drop("control", "treatment")
  }

  def doStatistic(alpha: Double): UserDefinedFunction = udf {
    (
        control: Row,
        treatment: Row
    ) =>
      val statResult = WelchTTest.welchTTest(control, treatment, alpha)
      val controlSize = control.getAs[Long]("length")
      val treatmentSize = treatment.getAs[Long]("length")

      StatisticsReport(
        statResult,
        srm(controlSize.toInt, treatmentSize.toInt, $(srmAlpha)),
        controlSize,
        treatmentSize,
        "welch"
      )
  }
}

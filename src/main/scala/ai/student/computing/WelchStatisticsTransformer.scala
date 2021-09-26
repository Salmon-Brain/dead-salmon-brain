package ai.student.computing

import org.apache.commons.math3.stat.inference.TestUtils
import org.apache.spark.ml.Transformer
import org.apache.spark.ml.param.ParamMap
import org.apache.spark.ml.util.{ DefaultParamsWritable, Identifiable }
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{ DataFrame, Dataset, Row }

class WelchStatisticsTransformer(override val uid: String)
    extends Transformer
    with DefaultParamsWritable
    with BaseStatisticTransformerParameters
    with BasicStatInferenceParameters {
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

  override def copy(extra: ParamMap): Transformer = defaultCopy(extra)

  override def transformSchema(schema: StructType): StructType = schema

  def doStatistic(alpha: Double): UserDefinedFunction = udf {
    (
        control: Row,
        treatment: Row
    ) =>
      val statResult = WelchTTest.welchTTest(control, treatment, alpha)
      val controlSize = control.getAs[Long]("length")
      val treatmentSize = treatment.getAs[Long]("length")
      val uniform = (treatmentSize + controlSize).toDouble / 2
      val srm = TestUtils.chiSquareTest(
        Array(uniform, uniform),
        Array(controlSize, treatmentSize),
        $(srmAlpha)
      )

      StatisticsReport(
        statResult,
        srm,
        controlSize,
        treatmentSize,
        "welch"
      )
  }
}

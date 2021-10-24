package ai.salmon.computing

import org.apache.spark.ml.Transformer
import org.apache.spark.ml.param.ParamMap
import org.apache.spark.ml.util.{ DefaultParamsWritable, Identifiable }
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions.{ collect_list, udf }
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{ DataFrame, Dataset }

import scala.collection.mutable

class MannWhitneyStatisticsTransformer(override val uid: String)
    extends Transformer
    with DefaultParamsWritable
    with BaseStatisticTransformerParameters
    with BaseStatisticTransformer
    with BasicStatInferenceParameters {
  def this() = this(Identifiable.randomUID("mannWhitneyStatisticsTransformer"))

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
        collect_list($(valueColumn))
      )
      .withColumn("statisticsData", doStatistic($(alpha))($"control", $"treatment"))
      .drop("control", "treatment")
  }

  override def copy(extra: ParamMap): Transformer = defaultCopy(extra)

  override def transformSchema(schema: StructType): StructType = schema

  def doStatistic(alpha: Double): UserDefinedFunction = udf {
    (
        control: mutable.WrappedArray[Double],
        treatment: mutable.WrappedArray[Double]
    ) =>
      val statResult = MannWhitneyTest.mannWhitneyTest(control.toArray, treatment.toArray, alpha)
      val controlSize = control.length
      val treatmentSize = treatment.length
      StatisticsReport(
        statResult,
        srm(controlSize, treatmentSize, $(srmAlpha)),
        controlSize,
        treatmentSize,
        "mannWhitney"
      )
  }
}

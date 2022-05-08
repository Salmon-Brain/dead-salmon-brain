package ai.salmonbrain.ruleofthumb

import org.apache.spark.ml.Transformer
import org.apache.spark.ml.param.ParamMap
import org.apache.spark.ml.util.Identifiable
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions.{ col, collect_list, udf }
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{ DataFrame, Dataset }

import scala.collection.mutable

/**
 * Transformer to apply Mann–Whitney U test
 * @see https://en.wikipedia.org/wiki/Mann%E2%80%93Whitney_U_test
 * @param uid -  uid for transformer
 */
class MannWhitneyStatisticsTransformer(override val uid: String) extends BaseStatisticTransformer {
  def this() = this(Identifiable.randomUID("mannWhitneyStatisticsTransformer"))

  override def transform(dataset: Dataset[_]): DataFrame = {
    dataset
      .groupBy(
        $(experimentColumn),
        $(metricNameColumn),
        $(additiveColumn),
        $(metricSourceColumn),
        $(entityCategoryNameColumn),
        $(entityCategoryValueColumn)
      )
      .pivot($(variantColumn))
      .agg(
        collect_list($(valueColumn))
      )
      .withColumn(
        "statisticsData",
        doStatistic($(alpha), $(beta), $(minValidSampleSize))(
          col($(controlName)),
          col($(treatmentName))
        )
      )
      .drop("control", "treatment")
  }

  override def copy(extra: ParamMap): Transformer = defaultCopy(extra)

  override def transformSchema(schema: StructType): StructType = schema

  def doStatistic(alpha: Double, beta: Double, minValidSampleSize: Int): UserDefinedFunction = udf {
    (
        control: mutable.WrappedArray[Double],
        treatment: mutable.WrappedArray[Double]
    ) =>
      val controlSize = Option(control).getOrElse(mutable.WrappedArray.empty).length
      val treatmentSize = Option(treatment).getOrElse(mutable.WrappedArray.empty).length
      val isEnoughData = math.min(controlSize, treatmentSize) >= minValidSampleSize
      val (statResult, srmResult) =
        if (isEnoughData)
          (
            MannWhitneyTest.mannWhitneyTest(
              control.toArray,
              treatment.toArray,
              alpha,
              beta
            ),
            srm(controlSize, treatmentSize, $(srmAlpha))
          )
        else (getInvalidStatResult(CentralTendency.MEDIAN), false)

      StatisticsReport(
        statResult,
        alpha,
        beta,
        minValidSampleSize,
        srmResult,
        controlSize,
        treatmentSize,
        TestType.MANN_WHITNEY.toString,
        isEnoughData
      )
  }
}

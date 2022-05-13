package ai.salmonbrain.ruleofthumb

import ai.salmonbrain.ruleofthumb.CentralTendency.CentralTendency
import org.apache.commons.math3.stat.inference.TestUtils
import org.apache.spark.ml.Transformer
import org.apache.spark.ml.param.ParamMap
import org.apache.spark.ml.util.DefaultParamsWritable
import org.apache.spark.sql.Encoders
import org.apache.spark.sql.types.{ BooleanType, StringType, StructField, StructType }

trait BaseStatisticTransformer
    extends Transformer
    with DefaultParamsWritable
    with BaseStatisticTransformerParameters
    with BasicStatInferenceParameters {

  protected val outputSchema: StructType =
    StructType(
      Array(
        StructField("experimentUid", StringType, nullable = false),
        StructField("metricName", StringType, nullable = false),
        StructField("isAdditive", BooleanType, nullable = false),
        StructField("metricSource", StringType, nullable = false),
        StructField("categoryName", StringType, nullable = false),
        StructField("categoryValue", StringType, nullable = false),
        StructField(
          "statisticsData",
          Encoders.product[StatisticsReport].schema,
          nullable = false
        )
      )
    )

  override def copy(extra: ParamMap): Transformer = defaultCopy(extra)

  override def transformSchema(schema: StructType): StructType = outputSchema

  def srm(controlSize: Int, treatmentSize: Int, alpha: Double): Boolean = {
    val uniform = (treatmentSize + controlSize).toDouble / 2
    TestUtils.chiSquareTest(
      Array(uniform, uniform),
      Array(controlSize, treatmentSize),
      alpha
    )
  }

  protected def getInvalidStatResult(centralTendency: CentralTendency): StatResult = {
    StatResult(
      Double.NaN,
      Double.NaN,
      -1,
      Double.NaN,
      Double.NaN,
      Double.NaN,
      Double.NaN,
      Double.NaN,
      Double.NaN,
      centralTendency.toString,
      isZeroVariance = false
    )
  }
}

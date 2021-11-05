package ai.salmon.computing

import org.apache.commons.math3.distribution.NormalDistribution
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
        StructField("expUid", StringType, nullable = false),
        StructField("metricName", StringType, nullable = false),
        StructField("isAdditive", BooleanType, nullable = false),
        StructField("metricSource", StringType, nullable = false),
        StructField(
          "statisticsData",
          Encoders.product[StatisticsReport].schema,
          nullable = false
        )
      )
    )
  val normalDistribution = new NormalDistribution(0, 1)

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

  def minimumSampleSizeEstimation(
      alpha: Double,
      beta: Double,
      mean: Double,
      variance: Double,
      detectableEffect: Double
  ): Double = {
    assert(detectableEffect > 0 && detectableEffect < 1)
    val multiplier = 2 * math.ceil(
      math.pow(
        normalDistribution.inverseCumulativeProbability(1 - alpha / 2) + normalDistribution
          .inverseCumulativeProbability(1 - beta),
        2
      )
    )

    (multiplier * variance) / math.pow((mean * detectableEffect), 2)
  }
}

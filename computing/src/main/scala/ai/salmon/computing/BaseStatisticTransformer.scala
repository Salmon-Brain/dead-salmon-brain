package ai.salmon.computing

import org.apache.commons.math3.stat.inference.TestUtils
import org.apache.spark.ml.Transformer
import org.apache.spark.ml.param.ParamMap
import org.apache.spark.ml.util.DefaultParamsWritable
import org.apache.spark.sql.types.{ BooleanType, StringType, StructField, StructType }
import org.apache.spark.sql.{ Dataset, Encoders }

trait BaseStatisticTransformer
    extends Transformer
    with DefaultParamsWritable
    with BaseStatisticTransformerParameters
    with BasicStatInferenceParameters {

  protected val variants = Variants.values.map(_.toString.toLowerCase)
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

  protected def checkVariants(dataset: Dataset[_]): Unit = {
    val observedVariants = dataset
      .select($(variantColumn))
      .distinct()
      .collect()
      .map(row => row.getAs[String]($(variantColumn)))
      .toSet
    assert(variants == observedVariants, "Variants must be named control and treatment")
  }
}

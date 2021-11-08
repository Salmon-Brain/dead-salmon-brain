package ai.salmon.inputs

import org.apache.spark.ml.Transformer
import org.apache.spark.ml.param.{ Param, ParamMap }
import org.apache.spark.ml.util.Identifiable
import org.apache.spark.sql.functions.{ col, expr, lit }
import org.apache.spark.sql.types._
import org.apache.spark.sql.{ DataFrame, Dataset }

class AccessLogTransformer(override val uid: String) extends Transformer {

  private val resultSchema: StructType = StructType(
    Array(
      StructField("timestamp", LongType, true),
      StructField("variantId", StringType, true),
      StructField("entityUid", StringType, true),
      StructField("expUid", StringType, true),
      StructField("metricValue", DoubleType, true),
      StructField("metricName", StringType, true),
      StructField("isHistory", BooleanType, true),
      StructField("entityCategories", ArrayType(StringType, true), true),
      StructField("metricSource", StringType, true),
      StructField("isAdditive", BooleanType, true)
    )
  )

  def this() = this(Identifiable.randomUID("NginxLogTransformer"))

  val expUidExpression: Param[String] = new Param[String](
    this,
    "expUidExpression",
    "experiment id expression"
  )

  val variantIdExpression: Param[String] = new Param[String](
    this,
    "variantIdExpression",
    "variant id expression"
  )

  val metricValueExpression: Param[String] = new Param[String](
    this,
    "metricValueExpression",
    "metric value expression"
  )

  val metricNameExpression: Param[String] = new Param[String](
    this,
    "metricNameExpression",
    "metric name expression"
  )

  /** @group setParam */
  def setExpUidExpression(value: String): this.type =
    set(expUidExpression, value)

  /** @group setParam */
  def setVariantIdExpression(value: String): this.type =
    set(variantIdExpression, value)

  /** @group setParam */
  def setMetricValueExpression(value: String): this.type =
    set(metricValueExpression, value)

  /** @group setParam */
  def setMetricNameExpression(value: String): this.type =
    set(metricNameExpression, value)

  override def transform(dataset: Dataset[_]): DataFrame = {
    dataset
      .withColumn("expUid", expr($(expUidExpression)))
      .withColumn("variantId", expr($(variantIdExpression)))
      .withColumn("metricValue", expr($(metricValueExpression)).cast(DoubleType))
      .withColumn("metricName", expr($(metricNameExpression)))
      .withColumn("metricSource", lit("feedback"))
      .withColumn("isAdditive", lit(true))
      .filter(col("metricName") =!= "none")
      .select(
        "timestamp",
        "variantId",
        "entityUid",
        "expUid",
        "metricValue",
        "metricName",
        "metricSource",
        "isAdditive"
      )
  }

  override def copy(extra: ParamMap): Transformer = defaultCopy(extra)

  override def transformSchema(schema: StructType): StructType = resultSchema
}

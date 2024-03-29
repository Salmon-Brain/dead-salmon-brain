package ai.salmonbrain.inputs

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
      StructField("experimentUid", StringType, true),
      StructField("metricValue", DoubleType, true),
      StructField("metricName", StringType, true),
      StructField("isHistory", BooleanType, true),
      StructField("categoryName", StringType, true),
      StructField("categoryValue", StringType, true),
      StructField("metricSource", StringType, true),
      StructField("isAdditive", BooleanType, true)
    )
  )

  def this() = this(Identifiable.randomUID("NginxLogTransformer"))

  val experimentUidExpression: Param[String] = new Param[String](
    this,
    "experimentUidExpression",
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
  def setexperimentUidExpression(value: String): this.type =
    set(experimentUidExpression, value)

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
      .withColumn("experimentUid", expr($(experimentUidExpression)))
      .withColumn("variantId", expr($(variantIdExpression)))
      .withColumn("metricValue", expr($(metricValueExpression)).cast(DoubleType))
      .withColumn("metricName", expr($(metricNameExpression)))
      .withColumn("categoryName", lit("common"))
      .withColumn("categoryValue", lit("all"))
      .withColumn("metricSource", lit("feedback"))
      .withColumn("isAdditive", lit(true))
      .filter(col("metricName") =!= "none")
      .select(
        "timestamp",
        "variantId",
        "entityUid",
        "experimentUid",
        "metricValue",
        "metricName",
        "categoryName",
        "categoryValue",
        "metricSource",
        "isAdditive"
      )
  }

  override def copy(extra: ParamMap): Transformer = defaultCopy(extra)

  override def transformSchema(schema: StructType): StructType = resultSchema
}

package ai.salmonbrain.ruleofthumb

import org.apache.spark.ml.Transformer
import org.apache.spark.ml.param.{ Param, ParamMap }
import org.apache.spark.ml.util.Identifiable
import org.apache.spark.sql.functions.{ broadcast, callUDF, col, lit }
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{ DataFrame, Dataset }

/**
 * Transformer to remove outliers from data based on percentiles
 * @param uid -  uid for transformer
 */
class OutlierRemoveTransformer(override val uid: String)
    extends Transformer
    with BaseStatisticTransformerParameters {
  def this() = this(Identifiable.randomUID("outlierRemoveTransformer"))

  val lowerPercentile: Param[Double] = new Param[Double](
    this,
    "lowerPercentile",
    "lower percentile to clear outliers"
  )
  setDefault(lowerPercentile, 0.01)

  val upperPercentile: Param[Double] = new Param[Double](
    this,
    "upperPercentile",
    "upper percentile to clear outliers"
  )
  setDefault(upperPercentile, 0.99)

  /** @group setParam */
  def setLowerPercentile(value: Double): this.type =
    set(lowerPercentile, value)

  /** @group setParam */
  def setUpperPercentile(value: Double): this.type =
    set(upperPercentile, value)

  override def transform(dataset: Dataset[_]): DataFrame = {
    assert($(lowerPercentile) > 0 && $(lowerPercentile) < 1, "lowerPercentile must be in (0, 1)")
    assert($(upperPercentile) > 0 && $(upperPercentile) < 1, "upperPercentile must be in (0, 1)")
    assert(
      $(upperPercentile) > $(lowerPercentile),
      "upperPercentile must be greater than lowerPercentile"
    )

    import dataset.sparkSession.implicits._

    val columns = Seq(
      $(variantColumn),
      $(experimentColumn),
      $(metricSourceColumn),
      $(metricNameColumn)
    )
    val percentilesBound = dataset
      .groupBy(columns.head, columns: _*)
      .agg(
        callUDF("percentile_approx", col($(valueColumn)), lit($(lowerPercentile))) as "leftBound",
        callUDF("percentile_approx", col($(valueColumn)), lit($(upperPercentile))) as "rightBound"
      )

    dataset
      .join(broadcast(percentilesBound), columns)
      .filter(col($(valueColumn)) > $"leftBound" && col($(valueColumn)) < $"rightBound")
      .drop("leftBound", "rightBound")
  }

  override def copy(extra: ParamMap): Transformer = defaultCopy(extra)

  override def transformSchema(schema: StructType): StructType = schema

}

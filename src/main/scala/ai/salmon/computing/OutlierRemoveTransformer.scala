package ai.salmon.computing

import org.apache.spark.ml.Transformer
import org.apache.spark.ml.param.{ Param, ParamMap }
import org.apache.spark.ml.util.Identifiable
import org.apache.spark.sql.functions.{ broadcast, callUDF, col, lit }
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{ DataFrame, Dataset }

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
  setDefault(lowerPercentile, 0.99)

  /** @group setParam */
  def setLowerPercentile(value: Double): this.type =
    set(lowerPercentile, value)

  /** @group setParam */
  def setUpperPercentile(value: Double): this.type =
    set(upperPercentile, value)

  override def transform(dataset: Dataset[_]): DataFrame = {
    assert($(lowerPercentile) > 0 && $(lowerPercentile) < 1)
    assert($(upperPercentile) > 0 && $(upperPercentile) < 1)
    assert($(upperPercentile) > $(lowerPercentile))

    import dataset.sparkSession.implicits._

    val percentilesBound = dataset
      .groupBy(
        $(variantColumn),
        $(entityIdColumn),
        $(experimentColumn),
        $(metricSourceColumn),
        $(metricNameColumn)
      )
      .agg(
        callUDF("percentile_approx", col($(valueColumn)), lit($(lowerPercentile))) as "leftBound",
        callUDF("percentile_approx", col($(valueColumn)), lit($(upperPercentile))) as "rightBound"
      )

    dataset
      .join(broadcast(percentilesBound))
      .filter(col($(valueColumn)) > $"leftBound" && col($(valueColumn)) < $"rightBound")
      .drop("leftBound", "rightBound")
  }

  override def copy(extra: ParamMap): Transformer = defaultCopy(extra)

  override def transformSchema(schema: StructType): StructType = schema

}

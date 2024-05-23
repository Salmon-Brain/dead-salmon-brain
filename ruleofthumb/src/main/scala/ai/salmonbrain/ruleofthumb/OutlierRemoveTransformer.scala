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
    assert($(upperPercentile) > 0 && $(upperPercentile) < 1, "upperPercentile must be in (0, 1)")
    assert(
      $(upperPercentile) > $(lowerPercentile),
      "upperPercentile must be greater than lowerPercentile"
    )

    val isDisabledLower = $(lowerPercentile) <= 0

    import dataset.sparkSession.implicits._

    val aggFunc = Seq(
      callUDF("percentile_approx", col($(valueColumn)), lit($(upperPercentile))) as "rightBound"
    ) ++
      (if (isDisabledLower) Seq()
       else
         Seq(
           callUDF(
             "percentile_approx",
             col($(valueColumn)),
             lit($(lowerPercentile))
           ) as "leftBound"
         ))
    val filterFunc =
      if (isDisabledLower) col($(valueColumn)) < $"rightBound"
      else col($(valueColumn)) > $"leftBound" && col($(valueColumn)) < $"rightBound"

    val dropCols = if (isDisabledLower) Seq("rightBound") else Seq("rightBound", "leftBound")

    val columns = Seq(
      $(variantColumn),
      $(experimentColumn),
      $(metricSourceColumn),
      $(metricNameColumn),
      $(entityCategoryNameColumn),
      $(entityCategoryValueColumn)
    )
    val percentilesBound = dataset
      .groupBy(columns.head, columns: _*)
      .agg(
        aggFunc.head,
        aggFunc.tail: _*
      )

    dataset
      .join(broadcast(percentilesBound), columns)
      .filter(filterFunc)
      .drop(dropCols: _*)
  }

  override def copy(extra: ParamMap): Transformer = defaultCopy(extra)

  override def transformSchema(schema: StructType): StructType = schema

}

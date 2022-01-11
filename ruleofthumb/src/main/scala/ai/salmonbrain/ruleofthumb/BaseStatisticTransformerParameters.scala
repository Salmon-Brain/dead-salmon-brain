package ai.salmonbrain.ruleofthumb

import org.apache.spark.ml.param.{ Param, Params }

trait BaseStatisticTransformerParameters extends Params {

  val metricSourceColumn: Param[String] = new Param[String](
    this,
    "metricSourceColumn",
    "column with experiment metric data source"
  )
  setDefault(metricSourceColumn, "metricSource")

  val entityCategoriesColumn: Param[String] = new Param[String](
    this,
    "entityCategoriesColumn",
    "column with experiment entity_id categories"
  )
  setDefault(entityCategoriesColumn, "entityCategories")

  val entityIdColumn: Param[String] = new Param[String](
    this,
    "entityIdColumn",
    "column with entity id"
  )
  setDefault(entityIdColumn, "entityUid")

  val experimentColumn: Param[String] = new Param[String](
    this,
    "experimentColumn",
    "column with experiment id"
  )

  setDefault(experimentColumn, "experimentUid")

  val variantColumn: Param[String] = new Param[String](
    this,
    "variantColumn",
    "column to split control and treatment"
  )

  setDefault(variantColumn, "variantId")

  val valueColumn: Param[String] = new Param[String](
    this,
    "valueColumn",
    "column with metric value"
  )

  setDefault(valueColumn, "metricValue")

  val metricNameColumn: Param[String] = new Param[String](
    this,
    "metricNameColumn",
    "column with metric name"
  )

  setDefault(metricNameColumn, "metricName")

  val additiveColumn: Param[String] = new Param[String](
    this,
    "additiveColumn",
    "column with metric type additive or non_additive"
  )

  setDefault(additiveColumn, "isAdditive")

  val controlName: Param[String] = new Param[String](
    this,
    "controlName",
    "label for control groups"
  )

  setDefault(controlName, "control")

  val treatmentName: Param[String] = new Param[String](
    this,
    "treatmentName",
    "label for treatment groups"
  )

  setDefault(treatmentName, "treatment")

  /** @group setParam */
  def setDataProviderColumn(value: String): this.type =
    set(metricSourceColumn, value)

  /** @group setParam */
  def setEntityCategoryColumn(value: String): this.type =
    set(entityCategoriesColumn, value)

  /** @group setParam */
  def setEntityIdColumn(value: String): this.type =
    set(entityIdColumn, value)

  /** @group setParam */
  def setExperimentColumn(value: String): this.type =
    set(experimentColumn, value)

  /** @group setParam */
  def setVariantColumn(value: String): this.type = set(variantColumn, value)

  /** @group setParam */
  def setValueColumn(value: String): this.type = set(valueColumn, value)

  /** @group setParam */
  def setMetricNameColumn(value: String): this.type =
    set(metricNameColumn, value)

  /** @group setParam */
  def setAdditiveColumn(value: String): this.type =
    set(additiveColumn, value)

  /** @group setParam */
  def setControlName(value: String): this.type =
    set(controlName, value)

  /** @group setParam */
  def setTreatmentName(value: String): this.type =
    set(treatmentName, value)
}

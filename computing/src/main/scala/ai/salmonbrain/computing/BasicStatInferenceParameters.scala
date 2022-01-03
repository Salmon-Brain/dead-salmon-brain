package ai.salmonbrain.computing

import org.apache.spark.ml.param.{ Param, Params }

trait BasicStatInferenceParameters extends Params {
  val alpha: Param[Double] = new Param[Double](
    this,
    "alpha",
    "parameter for check Type 1 error"
  )
  setDefault(alpha, 0.05)

  val beta: Param[Double] = new Param[Double](
    this,
    "beta",
    "parameter for check Type 2 error"
  )
  setDefault(beta, 0.2)

  val srmAlpha: Param[Double] = new Param[Double](
    this,
    "srmAlpha",
    "parameter for check FPR for SRM"
  )
  setDefault(srmAlpha, 0.05)

  /** @group setParam */
  def setAlpha(value: Double): this.type =
    set(alpha, value)

  /** @group setParam */
  def setBeta(value: Double): this.type =
    set(beta, value)

  /** @group setParam */
  def setSrmAlpha(value: Double): this.type =
    set(srmAlpha, value)

}

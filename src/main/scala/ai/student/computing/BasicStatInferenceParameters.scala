package ai.student.computing

import org.apache.spark.ml.param.{ Param, Params }

trait BasicStatInferenceParameters extends Params {
  val alpha: Param[Double] = new Param[Double](
    this,
    "alpha",
    "parameter for check FPR"
  )
  setDefault(alpha, 0.05)

  val srmAlpha: Param[Double] = new Param[Double](
    this,
    "alpha",
    "parameter for check FPR for SRM"
  )
  setDefault(srmAlpha, 0.05)

  /** @group setParam */
  def setAlpha(value: Double): this.type =
    set(alpha, value)

  /** @group setParam */
  def setSrmAlpha(value: Double): this.type =
    set(srmAlpha, value)

}

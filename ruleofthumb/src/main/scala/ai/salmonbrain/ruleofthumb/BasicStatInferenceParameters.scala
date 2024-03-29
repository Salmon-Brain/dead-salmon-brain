package ai.salmonbrain.ruleofthumb

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

  val minValidSampleSize: Param[Int] = new Param[Int](
    this,
    "minValidSampleSize",
    "parameter for skip invalid groups"
  )
  setDefault(minValidSampleSize, 10)

  val useLinearApproximationForVariance: Param[Boolean] = new Param[Boolean](
    this,
    "useLinearApproximationForVariance",
    "parameter for control variance computing method for nonparametric tests"
  )
  setDefault(useLinearApproximationForVariance, false)

  /** @group setParam */
  def setUseLinearApproximationForVariance(value: Boolean): this.type =
    set(useLinearApproximationForVariance, value)

  /** @group setParam */
  def setAlpha(value: Double): this.type =
    set(alpha, value)

  /** @group setParam */
  def setBeta(value: Double): this.type =
    set(beta, value)

  /** @group setParam */
  def setSrmAlpha(value: Double): this.type =
    set(srmAlpha, value)

  /** @group setParam */
  def setMinValidSampleSize(value: Int): this.type =
    set(minValidSampleSize, value)

}

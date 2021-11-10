package ai.salmon.computing

import org.apache.commons.math3.distribution.NormalDistribution

trait BaseStatTest {
  val normalDistribution = new NormalDistribution(0, 1)
  val EPS = 1e-10

  /*
    van Belle G (2002) Statistical rules of thumb. Wiley, ISBN: 0471402273
   */
  def sampleSizeEstimation(
      alpha: Double,
      beta: Double,
      controlCentralTendency: Double,
      treatmentCentralTendency: Double,
      commonVariance: Double
  ): Long = {
    val nominator = 2 * math.ceil(
      square(
        normalDistribution.inverseCumulativeProbability(1 - alpha / 2) + normalDistribution
          .inverseCumulativeProbability(1 - beta)
      )
    )

    val denominator = square(treatmentCentralTendency - controlCentralTendency) / commonVariance
    if (denominator < EPS) 0 else (nominator / denominator).toLong
  }

  def square(x: Double): Double = {
    x * x
  }
}

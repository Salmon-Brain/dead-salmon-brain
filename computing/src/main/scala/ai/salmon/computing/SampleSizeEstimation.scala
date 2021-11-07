package ai.salmon.computing

import org.apache.commons.math3.distribution.NormalDistribution

trait SampleSizeEstimation {
  val normalDistribution = new NormalDistribution(0, 1)

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
    val sq = (x: Double) => x * x
    val nominator = 2 * math.ceil(
      math.pow(
        normalDistribution.inverseCumulativeProbability(1 - alpha / 2) + normalDistribution
          .inverseCumulativeProbability(1 - beta),
        2
      )
    )

    val denominator = sq(treatmentCentralTendency - controlCentralTendency) / commonVariance
    (nominator / denominator).toLong
  }
}

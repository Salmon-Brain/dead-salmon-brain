package ai.salmonbrain.computing

import org.apache.commons.math3.distribution.NormalDistribution

trait BaseStatTest {
  val normalDistribution = new NormalDistribution(0, 1)
  val EPS = 1e-10

  def sampleSizeEstimation: Long = ???
  def square(x: Double): Double = {
    x * x
  }
}

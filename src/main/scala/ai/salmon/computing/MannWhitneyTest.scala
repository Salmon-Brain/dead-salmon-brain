package ai.salmon.computing

import org.apache.commons.math3.distribution.{ BinomialDistribution, NormalDistribution }
import org.apache.commons.math3.stat.descriptive.rank.Median
import org.apache.commons.math3.stat.inference.MannWhitneyUTest

object MannWhitneyTest {
  val median = new Median()
  val normal = new NormalDistribution()

  def mannWhitneyTest(
      control: Array[Double],
      treatment: Array[Double],
      alpha: Double
  ): StatResult = {
    assert(alpha < 1)
    val mannWhitneyUTest = new MannWhitneyUTest()
    val uStatistic = mannWhitneyUTest.mannWhitneyU(control, treatment)
    val pValue = mannWhitneyUTest.mannWhitneyUTest(control, treatment)
    val controlMedian = median.evaluate(control)
    val treatmentMedian = median.evaluate(treatment)
    val effect = treatmentMedian - controlMedian
    val std = math.sqrt(medianVariance(treatment) + medianVariance(control))
    val ci = ConfidenceInterval(
      effect + normal.inverseCumulativeProbability(alpha / 2) * std,
      effect + normal.inverseCumulativeProbability(1 - alpha / 2) * std,
      controlMedian
    )

    StatResult(
      uStatistic,
      pValue,
      controlMedian,
      treatmentMedian,
      ci.percentageLeft,
      ci.percentageRight,
      CentralTendency.MEDIAN.toString
    )
  }

  /*
   * https://www.researchgate.net/publication/11148358_Statistical_inference_for_a_linear_function_of_medians_Confidence_intervals_hypothesis_testing_and_sample_size_requirements
   */
  def medianVariance(values: Array[Double]): Double = {
    val sorted = values.sorted
    val y1 = sorted(
      sorted.length - alpha(sorted.length)
    )

    val y2 = sorted(alpha(sorted.length) - 1)
    val zed = zeta(sorted.length)
    (y1 - y2) * (y1 - y2) / 4 / zed / zed
  }

  private def alpha(length: Int): Int = {
    math
      .round(
        (length + 1.0) / 2 - math.sqrt(length)
      )
      .toInt
  }

  private def aBinomial(length: Int): Double = {
    new BinomialDistribution(length, 0.5).cumulativeProbability(alpha(length) - 1) * 2
  }

  private def zeta(length: Int): Double = {
    normal.inverseCumulativeProbability(1 - aBinomial(length) / 2)
  }
}

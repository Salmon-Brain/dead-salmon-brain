package ai.salmonbrain.ruleofthumb

import org.apache.commons.math3.distribution.BinomialDistribution
import org.apache.commons.math3.stat.descriptive.moment.Variance
import org.apache.commons.math3.stat.descriptive.rank.Median
import org.apache.commons.math3.stat.inference.MannWhitneyUTest

object MannWhitneyTest extends BaseStatTest {
  val median = new Median()
  val variance = new Variance()

  def mannWhitneyTest(
      control: Array[Double],
      treatment: Array[Double],
      alpha: Double,
      beta: Double,
      useLinearApproximationForVariance: Boolean
  ): StatResult = {
    assert(alpha < 1 && beta < 1)
    val controlMedian = median.evaluate(control)
    val treatmentMedian = median.evaluate(treatment)

    val (treatmentMedianVariance, controlMedianVariance) =
      if (useLinearApproximationForVariance)
        (medianVariance(treatment), medianVariance(control))
      else (variance.evaluate(treatment), variance.evaluate(control))

    (treatmentMedianVariance, controlMedianVariance) match {
      case x if x._1 < EPS || x._2 < EPS =>
        StatResult(
          Double.NaN,
          Double.NaN,
          -1,
          controlMedian,
          treatmentMedian,
          controlMedianVariance,
          treatmentMedianVariance,
          Double.NaN,
          Double.NaN,
          CentralTendency.MEDIAN.toString,
          isZeroVariance = true
        )
      case _ =>
        val mannWhitneyUTest = new MannWhitneyUTest()
        val uStatistic = mannWhitneyUTest.mannWhitneyU(control, treatment)
        val pValue = mannWhitneyUTest.mannWhitneyUTest(control, treatment)

        val std = math.sqrt(treatmentMedianVariance + controlMedianVariance)
        val size = math.max(control.length, treatment.length)

        val ci = CI(
          controlMedian,
          controlMedianVariance,
          treatmentMedian,
          treatmentMedianVariance,
          std,
          normalDistribution.inverseCumulativeProbability(alpha / 2),
          normalDistribution.inverseCumulativeProbability(1 - alpha / 2),
          size
        )

        val sampleSize = sampleSizeEstimation(
          alpha,
          beta,
          treatmentMedian,
          controlMedian,
          treatment.length,
          control.length
        )

        StatResult(
          uStatistic,
          pValue,
          sampleSize,
          controlMedian,
          treatmentMedian,
          controlMedianVariance,
          treatmentMedianVariance,
          ci.lowerPercent,
          ci.upperPercent,
          CentralTendency.MEDIAN.toString,
          isZeroVariance = false
        )
    }
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
    square(y1 - y2) / (4 * square(zed))
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
    normalDistribution.inverseCumulativeProbability(1 - aBinomial(length) / 2)
  }

  /*
   * https://www.researchgate.net/publication/11148358_Statistical_inference_for_a_linear_function_of_medians_Confidence_intervals_hypothesis_testing_and_sample_size_requirements
   */
  def sampleSizeEstimation(
      alpha: Double,
      beta: Double,
      medianTreatment: Double,
      medianControl: Double,
      treatmentSize: Int,
      controlSize: Int
  ): Long = {
    val nominator = math.ceil(
      square(
        normalDistribution.inverseCumulativeProbability(1 - alpha / 2) + normalDistribution
          .inverseCumulativeProbability(1 - beta)
      )
    )

    val denominator = square(medianTreatment - medianControl) / (treatmentSize + controlSize)

    (nominator / denominator).toLong
  }
}

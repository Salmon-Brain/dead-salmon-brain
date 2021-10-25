package ai.salmon.computing

import org.apache.commons.math3.distribution.NormalDistribution
import org.apache.commons.math3.stat.descriptive.rank.Median
import org.apache.commons.math3.stat.inference.MannWhitneyUTest

object MannWhitneyTest extends BaseStat {
  def mannWhitneyTest(
      control: Array[Double],
      treatment: Array[Double],
      alpha: Double
  ): StatResult = {
    assert(alpha < 1)
    val mannWhitneyUTest = new MannWhitneyUTest()
    val uStatistic = mannWhitneyUTest.mannWhitneyU(control, treatment)
    val pValue = mannWhitneyUTest.mannWhitneyUTest(control, treatment)
    val (left, median, right) = hodgesLehmannEstimation(control, treatment, alpha)
    val ci = ConfidenceInterval(left, Option(median), right, new Median().evaluate(control))

    StatResult(
      uStatistic,
      None,
      pValue,
      mean(control),
      mean(treatment),
      ci.absoluteEffect,
      ci.delta,
      ci.percentageEffect,
      ci.percentageLeft,
      ci.percentageRight
    )
  }

  def hodgesLehmannEstimation(
      treatment: Array[Double],
      control: Array[Double],
      alpha: Double = 0.05
  ): (Double, Double, Double) = {
    val lengthTreatment = treatment.length
    val lengthControl = control.length
    val m = lengthTreatment * lengthControl

    val data =
      (for (x <- treatment; y <- control)
        yield (x, y))
        .map { case (left, right) => left - right }

    val median = new Median().evaluate(data)

    val z = new NormalDistribution(null, 0, 1).inverseCumulativeProbability(1.0 - (alpha / 2))
    val ca = Math
      .ceil(
        lengthTreatment.toDouble * lengthControl / 2.0 - z * Math.sqrt(
          lengthTreatment.toDouble * lengthControl * (lengthTreatment + lengthControl + 1) / 12.0
        )
      )
      .toInt
    (data(ca), median, data(m + 1 - ca))
  }
}

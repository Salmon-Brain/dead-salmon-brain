package ai.salmon.computing

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

    StatResult(
      uStatistic,
      null,
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
}

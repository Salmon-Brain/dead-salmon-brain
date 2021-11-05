package ai.salmon.computing

import org.apache.commons.math3.distribution.TDistribution
import org.apache.commons.math3.stat.descriptive.moment.{ Mean, Variance }
import org.apache.spark.sql.Row

object WelchTTest {
  val EPS = 1e-10
  lazy val mean = new Mean()
  lazy val variance = new Variance()

  def welchTTest(control: Array[Double], treatment: Array[Double], alpha: Double): StatResult = {
    val controlData =
      DescriptiveStat(mean.evaluate(control), variance.evaluate(control), control.length.toLong)
    val treatmentData =
      DescriptiveStat(
        mean.evaluate(treatment),
        variance.evaluate(treatment),
        treatment.length.toLong
      )
    welchTTest(controlData, treatmentData, alpha)
  }

  def welchTTest(
      controlData: DescriptiveStat,
      treatmentData: DescriptiveStat,
      alpha: Double
  ): StatResult = {
    assert(alpha < 1 && alpha > 0)

    val (controlVariance, treatmentVariance) = (controlData.variance, treatmentData.variance)
    val (controlSampleSize, treatmentSampleSize) = (controlData.length, treatmentData.length)
    val (controlMean, treatmentMean) =
      (controlData.mean, treatmentData.mean)

    (controlVariance, treatmentVariance) match {
      case x if x._1 < EPS || x._2 < EPS =>
        StatResult(
          Double.NaN,
          controlMean,
          treatmentMean,
          Double.NaN,
          Double.NaN,
          Double.NaN
        )
      case _ =>
        val qt = controlVariance / controlSampleSize + treatmentVariance / treatmentSampleSize
        val std = math.sqrt(qt)
        val t = (controlMean - treatmentMean) / std
        val df =
          square(qt) /
            (square(controlVariance) /
              (square(
                controlSampleSize
              ) * (controlSampleSize - 1)) + square(
                treatmentVariance
              ) / (square(
                treatmentSampleSize
              ) * (treatmentSampleSize - 1)))
        val tDistribution = new TDistribution(df)
        val p = 2.0 * tDistribution.cumulativeProbability(-math.abs(t))
        val ci = CI(
          controlMean,
          math.sqrt(controlVariance),
          treatmentMean,
          math.sqrt(treatmentVariance),
          std,
          tDistribution.inverseCumulativeProbability(alpha / 2),
          tDistribution.inverseCumulativeProbability(1 - alpha / 2)
        )

        StatResult(
          t,
          p,
          controlMean,
          treatmentMean,
          ci.lowerPercent,
          ci.upperPercent
        )
    }
  }

  def welchTTest(control: Row, treatment: Row, alpha: Double): StatResult = {
    val controlData = DescriptiveStat(
      control.getAs[Double]("mean"),
      control.getAs[Double]("variance"),
      control.getAs[Long]("length")
    )
    val treatmentData = DescriptiveStat(
      treatment.getAs[Double]("mean"),
      treatment.getAs[Double]("variance"),
      treatment.getAs[Long]("length")
    )
    welchTTest(controlData, treatmentData, alpha)
  }

  def square(x: Double): Double = {
    x * x
  }
}

case class DescriptiveStat(mean: Double, variance: Double, length: Long)

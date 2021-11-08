package ai.salmon.computing

import org.apache.commons.math3.distribution.TDistribution
import org.apache.commons.math3.stat.descriptive.moment.{ Mean, Variance }
import org.apache.spark.sql.Row

object WelchTTest extends BaseStatTest {
  lazy val mean = new Mean()
  lazy val variance = new Variance()

  def welchTTest(
      control: Array[Double],
      treatment: Array[Double],
      alpha: Double,
      beta: Double
  ): StatResult = {
    val controlData =
      DescriptiveStat(mean.evaluate(control), variance.evaluate(control), control.length.toLong)
    val treatmentData =
      DescriptiveStat(
        mean.evaluate(treatment),
        variance.evaluate(treatment),
        treatment.length.toLong
      )
    welchTTest(controlData, treatmentData, alpha, beta)
  }

  def welchTTest(
      controlData: DescriptiveStat,
      treatmentData: DescriptiveStat,
      alpha: Double,
      beta: Double
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
          Double.NaN,
          0L,
          controlMean,
          treatmentMean,
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
        val size = math.max(controlSampleSize, treatmentSampleSize)
        val ci = CI(
          controlMean,
          controlVariance,
          treatmentMean,
          treatmentVariance,
          std,
          tDistribution.inverseCumulativeProbability(alpha / 2),
          tDistribution.inverseCumulativeProbability(1 - alpha / 2),
          size
        )

        val sampleSize = sampleSizeEstimation(
          alpha,
          beta,
          controlMean,
          treatmentMean,
          (controlVariance + treatmentVariance) / 2
        )

        StatResult(
          t,
          p,
          sampleSize,
          controlMean,
          treatmentMean,
          ci.lowerPercent,
          ci.upperPercent
        )
    }
  }

  def welchTTest(
      control: Row,
      treatment: Row,
      alpha: Double,
      beta: Double
  ): StatResult = {
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
    welchTTest(controlData, treatmentData, alpha, beta)
  }
}

case class DescriptiveStat(mean: Double, variance: Double, length: Long)

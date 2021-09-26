package ai.student.computing

import org.apache.commons.math3.distribution.TDistribution
import org.apache.spark.sql.Row

object WelchTTest {
  val EPS = 1e-10

  def welchTTest(control: Array[Double], treatment: Array[Double], alpha: Double): StatResult = {
    val controlData = DescriptiveStat(mean(control), variance(control), control.length.toLong)
    val treatmentData =
      DescriptiveStat(mean(treatment), variance(treatment), treatment.length.toLong)
    welchTTest(controlData, treatmentData, alpha)
  }

  def welchTTest(
      controlData: DescriptiveStat,
      treatmentData: DescriptiveStat,
      alpha: Double
  ): StatResult = {
    assert(alpha < 1 && alpha > 0)

    val (vx, vy) = (controlData.variance, treatmentData.variance)
    val (nx, ny) = (controlData.length, treatmentData.length)
    val (controlMean, treatmentMean) =
      (controlData.mean, treatmentData.mean)

    (vx, vy) match {
      case x if x._1 < EPS || x._2 < EPS =>
        StatResult(
          Double.NaN,
          Double.NaN,
          Double.NaN,
          controlMean,
          treatmentMean,
          Double.NaN,
          Double.NaN,
          Double.NaN,
          Double.NaN,
          Double.NaN
        )
      case _ =>
        val qt = vx / nx + vy / ny
        val std = math.sqrt(qt)

        val t = (controlMean - treatmentMean) / std
        val df =
          square(qt) / (square(vx) / (square(nx) * (nx - 1)) + square(vy) / (square(ny) * (ny - 1)))
        val tDistribution = new TDistribution(df)
        val p = 2.0 * tDistribution.cumulativeProbability(-math.abs(t))
        val effect = treatmentMean - controlMean
        val ci = ConfidenceInterval(
          effect + tDistribution.inverseCumulativeProbability(alpha / 2) * std,
          effect + tDistribution.inverseCumulativeProbability(1 - alpha / 2) * std,
          controlMean
        )

        StatResult(
          t,
          df,
          p,
          controlMean,
          treatmentMean,
          ci.absoluteEffect,
          ci.delta,
          ci.percentageEffect,
          ci.percentageLeft,
          ci.percentageRight
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

  def square[T](x: T)(implicit num: Numeric[T]): T = {
    import num._
    x * x
  }

  def count[A](a: Seq[A])(implicit num: Fractional[A]): A =
    a.foldLeft(num.zero) { case (cnt, _) => num.plus(cnt, num.one) }

  def mean[A](a: Seq[A])(implicit num: Fractional[A]): A = num.div(a.sum, count(a))

  def variance[A](a: Seq[A])(implicit num: Fractional[A]): A =
    num.div(a.map(xs => square(num.minus(xs, mean(a)))).sum, num.minus(count(a), num.one))
}

case class DescriptiveStat(mean: Double, variance: Double, length: Long)

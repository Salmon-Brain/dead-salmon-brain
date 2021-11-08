package ai.salmon.computing

case class ExpData(
    timestamp: Long,
    variantId: String,
    entityUid: String,
    expUid: String,
    metricValue: Double,
    metricName: String,
    metricSource: String = "feedback",
    isAdditive: Boolean = true
)

case class StatisticsReport(
    statResult: StatResult,
    srm: Boolean,
    controlSize: Long,
    treatmentSize: Long,
    testType: String
)

case class CI(
    controlCentrality: Double,
    controlVariance: Double,
    treatmentCentrality: Double,
    treatmentVariance: Double,
    commonStd: Double,
    leftInterval: Double,
    rightInterval: Double,
    sampleSize: Double
) {

  def effect: Double = treatmentCentrality - controlCentrality
  def lower: Double = effect + commonStd * leftInterval
  def upper: Double = effect + commonStd * rightInterval
  def lowerPercent: Double = getPercent(leftInterval)
  def upperPercent: Double = getPercent(rightInterval)

  /*
  https://arxiv.org/pdf/1803.06336.pdf - delta method
  These formulas assume the covariance between the Treatment and Control mean is
  zero which will be true in a controlled experiment when the randomization is carried out properly
   */
  def getPercent(interval: Double): Double = {
    val sq = (x: Double) => x * x
    val pointEstimate = treatmentCentrality / controlCentrality - 1
    val uncertainty =
      (interval / (math.sqrt(sampleSize) * controlCentrality)) * math.sqrt(
        treatmentVariance + (sq(treatmentCentrality) * controlVariance) / sq(controlCentrality)
      )
    (pointEstimate + uncertainty) * 100
  }
}

case class StatResult(
    statistic: Double,
    pValue: Double,
    requiredSampleSizeByVariant: Long,
    controlCentralTendency: Double,
    treatmentCentralTendency: Double,
    percentageLeft: Double,
    percentageRight: Double,
    centralTendencyType: String = CentralTendency.MEAN.toString
)

case class Metric(metricName: String, metricValue: Double)
case class RatioMetricData(metricNominator: String, metricDenominator: String, newName: String)

object CentralTendency extends Enumeration {
  type CentralTendency = Value
  val MEAN, MEDIAN, MODE = Value
}

object TestType extends Enumeration {
  type CentralTendency = Value
  val WELCH, MANN_WHITNEY = Value
}

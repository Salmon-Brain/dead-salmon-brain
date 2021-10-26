package ai.salmon.computing

import ai.salmon.computing.CentralTendency.CentralTendency

case class ExpData(
    timestamp: Long,
    variantId: String,
    entityUid: String,
    expUid: String,
    metricValue: Double,
    metricName: String,
    isHistory: Boolean = false,
    entityCategories: Seq[String] = Seq[String](),
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

case class ConfidenceInterval(
    left: Double,
    right: Double,
    denominator: Double
) {
  def percentageLeft: Double = left / denominator * 100.0
  def percentageRight: Double = right / denominator * 100.0
}

case class StatResult(
    statistic: Double,
    pValue: Double,
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

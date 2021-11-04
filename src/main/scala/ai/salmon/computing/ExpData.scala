package ai.salmon.computing

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

case class CI(
    controlCentrality: Double,
    controlStd: Double,
    treatmentCentrality: Double,
    treatmentStd: Double,
    commonStd: Double,
    leftInterval: Double,
    rightInterval: Double
) {
  def effect: Double = treatmentCentrality - controlCentrality
  def lower: Double = effect + commonStd * leftInterval
  def upper: Double = effect + commonStd * rightInterval
  def effectPercent: Double = (effect / controlCentrality) * 100
  def controlCV: Double = controlStd / controlCentrality
  def treatmentCV: Double = treatmentStd / treatmentCentrality
  def lowerPercent: Double = getPercentEffect(effectPercent, controlCV, treatmentCV, leftInterval)
  def upperPercent: Double = getPercentEffect(effectPercent, controlCV, treatmentCV, rightInterval)

  /*
  https://ai.stanford.edu/~ronnyk/2009controlledExperimentsOnTheWebSurvey.pdf
   */
  def getPercentEffect(
      effectPercent: Double,
      cvControl: Double,
      cvTreatment: Double,
      interval: Double
  ): Double = {
    val sq = (x: Double) => x * x
    val percent = effectPercent + 1
    val nominator = 1 + interval * math.sqrt(
      sq(cvControl) + sq(cvTreatment) - sq(interval) * sq(cvControl) * sq(cvTreatment)
    )
    val denominator = 1 - math.abs(interval) * sq(cvControl)
    percent * (nominator / denominator) - 1
  }

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

object TestType extends Enumeration {
  type CentralTendency = Value
  val WELCH, MANN_WHITNEY = Value
}

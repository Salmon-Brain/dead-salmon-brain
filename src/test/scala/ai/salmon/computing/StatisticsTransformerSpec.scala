package ai.salmon.computing

import helpers.ExperimentDataGenerator.{
  experimentDataGenerator,
  generateDataForWelchTest,
  seqExpDataToDataFrame
}
import helpers.SparkHelper
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.first
import org.scalactic.{ Equality, TolerantNumerics }
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers

class StatisticsTransformerSpec extends AnyFlatSpec with SparkHelper with Matchers {
  import spark.implicits._
  val epsilon = 1e-4f
  val statWelch = new WelchStatisticsTransformer()
  val mannStat = new MannWhitneyStatisticsTransformer()
  val auto = new AutoStatisticsTransformer()

  val cum: CumulativeMetricTransformer = new CumulativeMetricTransformer()
    .setRatioMetricsData(Seq(RatioMetricData("clicks", "views", "ctr")))

  val cumWithBuckets: CumulativeMetricTransformer = new CumulativeMetricTransformer()
    .setRatioMetricsData(Seq(RatioMetricData("clicks", "views", "ctr")))
    .setNumBuckets(256)

  private val metricsWithUplift: DataFrame = cumWithBuckets.transform(
    seqExpDataToDataFrame(
      experimentDataGenerator(
        uplift = 0.2,
        controlSize = 10000,
        treatmentSize = 10000,
        beta = 1000
      )
    )
  )

  private val metricsWithoutUplift: DataFrame = cumWithBuckets.transform(
    seqExpDataToDataFrame(
      experimentDataGenerator(
        uplift = 0.0,
        controlSize = 10000,
        treatmentSize = 10000
      )
    )
  )

  private lazy val metricsToNonCltWithSkewness: DataFrame = cum.transform(
    seqExpDataToDataFrame(
      experimentDataGenerator(
        uplift = 0.0,
        controlSize = 3000,
        treatmentSize = 3000,
        treatmentSkew = 10,
        controlSkew = 10
      )
    )
  )

  implicit val doubleEq: Equality[Double] = TolerantNumerics.tolerantDoubleEquality(epsilon)

  "WelchStatisticsTransformer" should "be" in {

    val data = generateDataForWelchTest()

    val result = statWelch.transform(data)
    val pValue =
      result.select("statisticsData.statResult.pValue").collect().map(_.getDouble(0)).toSeq
    assert(0.036 === pValue.head)
  }

  "Srm" should "be" in {
    val data =
      seqExpDataToDataFrame(experimentDataGenerator(controlSize = 100, treatmentSize = 130))
    val metrics = new CumulativeMetricTransformer()
      .transform(data)
    val result = statWelch.transform(metrics)
    result.select("statisticsData.srm").collect().map(_.getBoolean(0)).toSeq.foreach {
      assert(_)
    }
  }

  "Welch detection uplift" should "be" in {
    val res = statWelch.transform(metricsWithUplift)
    val pValues = pValuesFromResult(res)
    assert(pValues("views") > 0.05)
    assert(pValues("clicks") < 0.05)
    assert(pValues("ctr") < 0.05)
  }

  "Welch detection equality" should "be" in {
    val pValues = pValuesFromResult(statWelch.transform(metricsWithoutUplift))
    assert(pValues("views") > 0.05)
    assert(pValues("clicks") > 0.05)
    assert(pValues("ctr") > 0.05)
  }

  "MannWhitney detection uplift" should "be" in {
    val pValues = pValuesFromResult(mannStat.transform(metricsWithUplift))
    assert(pValues("views") > 0.05)
    assert(pValues("clicks") < 0.05)
    assert(pValues("ctr") < 0.05)
  }

  "MannWhitney detection equality" should "be" in {
    val pValues = pValuesFromResult(mannStat.transform(metricsWithoutUplift))
    assert(pValues("views") > 0.05)
    assert(pValues("clicks") > 0.05)
    assert(pValues("ctr") > 0.05)
  }

  "Auto detection equality" should "be" in {
    val statResult = auto.transform(metricsToNonCltWithSkewness)
    val pValues = pValuesFromResult(statResult)
    assert(pValues("views") > 0.05)
    assert(pValues("clicks") > 0.05)
    assert(pValues("ctr") > 0.05)
    val testType = statResult.select($"statisticsData.testType").collect()(0).getString(0)
    assertResult(TestType.MANN_WHITNEY.toString)(testType)
  }

  def pValuesFromResult(result: DataFrame): Map[String, Double] = {
    result
      .groupBy($"metricName")
      .agg(first("statisticsData.statResult.pValue") as "pValue")
      .collect()
      .map(row => row.getString(0) -> row.getDouble(1))
      .toMap
  }
}

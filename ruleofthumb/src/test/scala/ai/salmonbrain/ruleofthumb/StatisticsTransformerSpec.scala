package ai.salmonbrain.ruleofthumb

import helpers.ExperimentDataGenerator.{experimentDataGenerator, generateDataForWelchTest, seqExpDataToDataFrame}
import helpers.SharedSparkSession
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.first
import org.scalactic.{Equality, TolerantNumerics}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers

class StatisticsTransformerSpec extends AnyFlatSpec with SharedSparkSession  with Matchers {
  import spark.implicits._
  val epsilon = 1e-4f
  val statWelch = new WelchStatisticsTransformer()
  val mannStat = new MannWhitneyStatisticsTransformer()
  val auto = new AutoStatisticsTransformer()
  val cum: CumulativeMetricTransformer = new CumulativeMetricTransformer()
    .setNumeratorNames(Array("clicks"))
    .setDenominatorNames(Array("views"))
    .setRatioNames(Array("ctr"))

  val cumWithBuckets: CumulativeMetricTransformer = new CumulativeMetricTransformer()
    .setNumeratorNames(Array("clicks"))
    .setDenominatorNames(Array("views"))
    .setRatioNames(Array("ctr"))
    .setNumBuckets(256)

  private lazy val metricsWithUplift: DataFrame = cumWithBuckets.transform(
    seqExpDataToDataFrame(
      experimentDataGenerator(
        uplift = 0.2,
        controlSize = 10000,
        treatmentSize = 10000,
        beta = 1000
      )
    )
  )

  private lazy val metricsWithoutUplift: DataFrame = cumWithBuckets.transform(
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
        controlSize = 300,
        treatmentSize = 300,
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

    val requiredSampleSize =
      result.select("statisticsData.statResult.requiredSampleSizeByVariant").collect()(0).getLong(0)
    assert(0.036 === pValue.head)
    assert(30 === requiredSampleSize)

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
    val res = mannStat.transform(metricsWithUplift)
    val pValues = pValuesFromResult(res)
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

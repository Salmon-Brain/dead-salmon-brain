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
    val pValues = pValuesFromResult(statWelch.transform(metricsWithUplift))
    statWelch.transform(metricsWithUplift).write.mode("overwrite").parquet("/tmp/welchUplift")
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
    mannStat.transform(metricsWithUplift).write.mode("overwrite").parquet("/tmp/mannUplift")

    assert(pValues("views") > 0.05)
    assert(pValues("clicks") > 0.05)
    assert(pValues("ctr") > 0.05)
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
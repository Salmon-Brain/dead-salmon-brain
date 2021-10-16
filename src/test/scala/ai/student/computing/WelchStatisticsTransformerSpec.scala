package ai.student.computing

import helpers.ExperimentDataGenerator.{
  experimentDataGenerator,
  generateDataForWelchTest,
  seqExpDataToDataFrame
}
import helpers.SparkHelper
import org.apache.spark.sql.functions.first
import org.scalactic.TolerantNumerics
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers

class WelchStatisticsTransformerSpec extends AnyFlatSpec with SparkHelper with Matchers {
  import spark.implicits._
  val epsilon = 1e-4f
  val stat = new WelchStatisticsTransformer()
  val cum: CumulativeMetricTransformer = new CumulativeMetricTransformer()
    .setRatioMetricsData(Seq(RatioMetricData("clicks", "views", "ctr")))
  val cumWithBuckets: CumulativeMetricTransformer = new CumulativeMetricTransformer()
    .setRatioMetricsData(Seq(RatioMetricData("clicks", "views", "ctr")))
    .setNumBuckets(256)

  implicit val doubleEq = TolerantNumerics.tolerantDoubleEquality(epsilon)

  "StatisticsTransformer" should "be" in {

    val data = generateDataForWelchTest()

    val result = stat.transform(data)
    val pValue =
      result.select("statisticsData.statResult.pValue").collect().map(_.getDouble(0)).toSeq
    assert(0.036 === pValue.head)
  }

  "Srm" should "be" in {
    val data =
      seqExpDataToDataFrame(experimentDataGenerator(controlSize = 100, treatmentSize = 130))
    val metrics = new CumulativeMetricTransformer()
      .transform(data)
    val result = stat.transform(metrics)
    result.select("statisticsData.srm").collect().map(_.getBoolean(0)).toSeq.foreach {
      assert(_)
    }
  }

  "Uplift" should "be" in {
    val data =
      seqExpDataToDataFrame(
        experimentDataGenerator(
          uplift = 0.2,
          controlSize = 10000,
          treatmentSize = 10000,
          beta = 1000
        )
      )
    val metrics = cumWithBuckets
      .transform(data)

    val result = stat.transform(metrics)

    val pValues = result
      .groupBy($"metricName")
      .agg(first("statisticsData.statResult.pValue") as "pValue")
      .collect()
      .map(row => row.getString(0) -> row.getDouble(1))
      .toMap

    assert(pValues("views") > 0.05)
    assert(pValues("clicks") < 0.05)
    assert(pValues("ctr") < 0.05)
  }

  "Not uplift" should "be" in {
    val data =
      seqExpDataToDataFrame(
        experimentDataGenerator(
          uplift = 0.0,
          controlSize = 10000,
          treatmentSize = 10000
        )
      )
    val metrics = cumWithBuckets
      .transform(data)

    val result = stat.transform(metrics)

    result.write.mode("overwrite").parquet("/tmp/noUplift")

    val pValues = result
      .groupBy($"metricName")
      .agg(first("statisticsData.statResult.pValue") as "pValue")
      .collect()
      .map(row => row.getString(0) -> row.getDouble(1))
      .toMap

    assert(pValues("views") > 0.05)
    assert(pValues("clicks") > 0.05)
    assert(pValues("ctr") > 0.05)
  }
}

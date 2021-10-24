package ai.salmon.computing

import helpers.ExperimentDataGenerator.{ experimentDataGenerator, seqExpDataToDataFrame }
import helpers.SparkHelper
import org.apache.spark.sql.functions.first
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers

class CumulativeMetricTransformerSpec extends AnyFlatSpec with SparkHelper with Matchers {
  "CumulativeMetricTransformer" should "be" in {
    val metrics = seqExpDataToDataFrame(experimentDataGenerator(withAggregation = false))

    val ratioMetrics = Seq(RatioMetricData("clicks", "views", "ctr"))

    val cumulativeDataByDate = new CumulativeMetricTransformer()
      .setRatioMetricsData(ratioMetrics)
      .setNumBuckets(256)
      .setIsUseDate(true)
      .transform(metrics)

    val newMetrics = cumulativeDataByDate
      .groupBy("expUid")
      .pivot("metricName")
      .agg(first("metricValue"))
      .columns
      .sorted

    assert(
      newMetrics
        sameElements Array("expUid", "clicks", "views", "ctr").sorted
    )

    assert(cumulativeDataByDate.columns.contains("date"))
    assert(
      cumulativeDataByDate.columns.sorted sameElements Array(
        "metricSource",
        "entityUid",
        "expUid",
        "isAdditive",
        "metricName",
        "metricValue",
        "variantId",
        "date"
      ).sorted
    )
  }
}
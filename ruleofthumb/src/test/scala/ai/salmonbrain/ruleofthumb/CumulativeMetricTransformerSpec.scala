package ai.salmonbrain.ruleofthumb

import helpers.ExperimentDataGenerator.{ experimentDataGenerator, seqExpDataToDataFrame }
import helpers.SparkHelper
import org.apache.spark.sql.functions.first
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers

class CumulativeMetricTransformerSpec extends AnyFlatSpec with SparkHelper with Matchers {
  "CumulativeMetricTransformer" should "be" in {
    val metrics = seqExpDataToDataFrame(experimentDataGenerator(withAggregation = false))

    val cumulativeData = new CumulativeMetricTransformer()
      .setNumeratorNames(Array("clicks"))
      .setDenominatorNames(Array("views"))
      .setRatioNames(Array("ctr"))
      .setNumBuckets(256)
      .transform(metrics)

    val newMetrics = cumulativeData
      .groupBy("experimentUid")
      .pivot("metricName")
      .agg(first("metricValue"))
      .columns
      .sorted

    assert(
      newMetrics
        sameElements Array("experimentUid", "clicks", "views", "ctr").sorted
    )

    assert(
      cumulativeData.columns.sorted sameElements Array(
        "metricSource",
        "entityUid",
        "experimentUid",
        "isAdditive",
        "metricName",
        "metricValue",
        "variantId"
      ).sorted
    )
  }
}

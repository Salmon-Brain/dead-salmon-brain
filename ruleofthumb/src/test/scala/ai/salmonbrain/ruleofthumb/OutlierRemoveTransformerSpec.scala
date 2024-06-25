package ai.salmonbrain.ruleofthumb

import helpers.ExperimentDataGenerator.{experimentDataGenerator, generateDataForWelchTest, seqExpDataToDataFrame}
import helpers.SharedSparkSession
import org.apache.spark.ml.Pipeline
import org.apache.spark.sql.SparkSession
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers

class OutlierRemoveTransformerSpec extends AnyFlatSpec with SharedSparkSession with Matchers {
  implicit val sparkSession: SparkSession = spark
  "OutlierRemoveTransformerSpec" should "be" in {
    val data = generateDataForWelchTest()
    val clearData = new OutlierRemoveTransformer().transform(data)
    assert(clearData.count() == 26)
  }

  "OutlierRemoveTransformerSpec with 0 lower percentile" should "be" in {
    val data = generateDataForWelchTest()
    val clearData = new OutlierRemoveTransformer().setLowerPercentile(0).transform(data)
    assert(clearData.count() == 28)
  }

  "OutlierRemoveTransformerSpec with excluded columns" should "be" in {
    val data =
      seqExpDataToDataFrame(
        experimentDataGenerator(
          uplift = 0,
          controlSkew = 0.1,
          treatmentSkew = 0.1,
          controlSize = 100,
          treatmentSize = 100,
          withAggregation = false
        )
      )
    val pipe = new Pipeline().setStages(
      Array(
        new CumulativeMetricTransformer(),
        new OutlierRemoveTransformer()
          .setLowerPercentile(0)
          .setUpperPercentile(0.99)
          .setExcludedMetrics(Array("clicks"))
      )
    )
    val clearData = pipe.fit(data).transform(data)
    assert(clearData.filter("metricName = 'clicks'").count() == 200)
    assert(clearData.filter("metricName = 'views'").count() < 200)
  }
}

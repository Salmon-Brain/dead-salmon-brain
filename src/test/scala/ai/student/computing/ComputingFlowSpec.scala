package ai.student.computing

import helpers.ExperimentDataGenerator.{ experimentDataGenerator, seqExpDataToDataFrame }
import helpers.SparkHelper
import org.apache.spark.ml.Pipeline
import org.apache.spark.sql.functions._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers

class ComputingFlowSpec extends AnyFlatSpec with SparkHelper with Matchers {
  "ComputingFlow" should "be" in {
    import spark.implicits._
    val metrics =
      seqExpDataToDataFrame(experimentDataGenerator(uplift = 0, withAggregation = false))
    val ratioMetrics = Seq(RatioMetricData("clicks", "views", "ctr"))

    val cumulativeDataByDateToVisualize = new CumulativeMetricTransformer()
      .setRatioMetricsData(ratioMetrics)
      .setNumBuckets(256)
      .setIsUseDate(true)
      .transform(metrics)
      .groupBy(
        "date",
        "expUid",
        "metricName",
        "metricSource",
        "variantId"
      )
      .agg(
        mean($"metricValue") as "meanMetricValue",
        callUDF("approx_percentile", $"metricValue", lit(0.5)) as "medianMetricValue"
      )
      .groupBy("expUid", "metricName", "metricSource")
      .pivot("variantId")
      .agg(collect_list(struct($"date", $"meanMetricValue", $"medianMetricValue")))

    val statPipe = new Pipeline().setStages(
      Array(
        new CumulativeMetricTransformer()
          .setRatioMetricsData(ratioMetrics),
        new WelchStatisticsTransformer()
      )
    )

    val statResult = statPipe.fit(metrics).transform(metrics)

    val result = statResult
      .join(
        cumulativeDataByDateToVisualize,
        Seq("expUid", "metricName", "metricSource")
      )

    result.select($"statisticsData.srm").collect().map(_.getAs[Boolean]("srm")).foreach { srm =>
      assert(!srm)
    }

    result
      .select($"statisticsData.statResult.pValue")
      .collect()
      .map(_.getAs[Double]("pValue"))
      .foreach { pValue =>
        assert(pValue > 0.05)
      }
  }
}

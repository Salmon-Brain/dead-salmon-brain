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
    val metrics = seqExpDataToDataFrame(experimentDataGenerator(withAggregation = false))
    val ratioMetrics = Seq(RatioMetricData("clicks", "views", "ctr"))

    val cumulativeDataByDateToVisualize = new CumulativeMetricTransformer()
      .setRatioMetricsData(ratioMetrics)
      .setIsUseDate(true)
      .transform(metrics)
      .groupBy(
        "date",
        "expUid",
        "metricName",
        "entityCategory",
        "categoryType",
        "dataProvider",
        "variantId"
      )
      .agg(
        mean($"metricValue") as "meanMetricValue",
        callUDF("approx_percentile", $"metricValue", lit(0.5)) as "medianMetricValue"
      )
      .groupBy("expUid", "metricName", "entityCategory", "categoryType", "dataProvider")
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
        Seq("expUid", "metricName", "entityCategory", "categoryType", "dataProvider")
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

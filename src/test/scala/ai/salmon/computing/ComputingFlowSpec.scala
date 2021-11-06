package ai.salmon.computing

import helpers.ExperimentDataGenerator.{ experimentDataGenerator, seqExpDataToDataFrame }
import helpers.SparkHelper
import org.apache.spark.ml.Pipeline
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers

class ComputingFlowSpec extends AnyFlatSpec with SparkHelper with Matchers {
  "ComputingFlow" should "be" in {
    import spark.implicits._
    val metrics =
      seqExpDataToDataFrame(
        experimentDataGenerator(
          uplift = 0,
          controlSize = 3000,
          treatmentSize = 3000,
          withAggregation = false
        )
      )
    val ratioMetrics = Seq(RatioMetricData("clicks", "views", "ctr"))

    val statPipe = new Pipeline().setStages(
      Array(
        new CumulativeMetricTransformer()
          .setRatioMetricsData(ratioMetrics),
        new WelchStatisticsTransformer()
      )
    )

    val statResult = statPipe.fit(metrics).transform(metrics)

    statResult
      .select($"statisticsData.srm")
      .collect()
      .map(_.getAs[Boolean]("srm"))
      .foreach { srm =>
        assert(!srm)
      }

    statResult
      .select($"statisticsData.statResult.pValue")
      .collect()
      .map(_.getAs[Double]("pValue"))
      .foreach { pValue =>
        assert(pValue > 0.05)
      }
  }
}

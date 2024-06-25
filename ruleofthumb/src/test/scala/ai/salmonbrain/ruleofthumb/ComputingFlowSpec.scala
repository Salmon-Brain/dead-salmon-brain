package ai.salmonbrain.ruleofthumb

import helpers.ExperimentDataGenerator.{experimentDataGenerator, seqExpDataToDataFrame}
import helpers.SharedSparkSession
import org.apache.spark.ml.Pipeline
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers

class ComputingFlowSpec extends AnyFlatSpec with SharedSparkSession with Matchers {
  "ComputingFlow" should "be" in {
    import spark.implicits._
    lazy val metrics =
      seqExpDataToDataFrame(
        experimentDataGenerator(
          uplift = 0,
          controlSkew = 0.1,
          treatmentSkew = 0.1,
          controlSize = 5000,
          treatmentSize = 5000,
          withAggregation = false
        )
      )

    val statPipe = new Pipeline().setStages(
      Array(
        new CumulativeMetricTransformer()
          .setNumBuckets(256),
        new OutlierRemoveTransformer(),
        new AutoStatisticsTransformer()
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

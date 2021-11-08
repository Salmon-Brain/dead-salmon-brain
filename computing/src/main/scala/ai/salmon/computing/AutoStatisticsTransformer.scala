package ai.salmon.computing

import org.apache.spark.ml.util.Identifiable
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{ DataFrame, Dataset, Row }

/**
 * Transformer to auto choose between Welch and Mann Whitney test based on data
 * @param uid - uid for transformer
 */
class AutoStatisticsTransformer(override val uid: String) extends BaseStatisticTransformer {
  def this() = this(Identifiable.randomUID("autoStatisticsTransformer"))

  override def transform(dataset: Dataset[_]): DataFrame = {
    import dataset.sqlContext.implicits._
    val cltMarker = dataset
      .groupBy(
        $(experimentColumn),
        $(metricNameColumn),
        $(additiveColumn),
        $(metricSourceColumn)
      )
      .pivot($(variantColumn))
      .agg(
        struct(
          count(col($(valueColumn))) as "length",
          skewness(col($(valueColumn))) as "skewness"
        )
      )
      .withColumn(
        "isUseClt",
        checkHeuristicsForCLT($"control.length", $"control.skewness")
          && checkHeuristicsForCLT($"treatment.length", $"treatment.skewness")
      )
      .drop("treatment", "control", "length")

    val data = dataset.join(
      broadcast(cltMarker),
      Seq($(experimentColumn), $(metricNameColumn), $(additiveColumn), $(metricSourceColumn))
    )

    val cltReport = {
      val cltData = data.filter($"isUseClt")
      if (!cltData.isEmpty)
        fillBaseParameters(new WelchStatisticsTransformer())
          .transform(cltData)
      else
        dataset.sparkSession.createDataFrame(
          dataset.sparkSession.sparkContext.emptyRDD[Row],
          outputSchema
        )
    }

    val nonCltReport = {
      val nonCltData = data.filter(!$"isUseClt")
      if (!nonCltData.isEmpty)
        fillBaseParameters(new MannWhitneyStatisticsTransformer())
          .transform(nonCltData)
      else
        dataset.sparkSession.createDataFrame(
          dataset.sparkSession.sparkContext.emptyRDD[Row],
          outputSchema
        )
    }

    cltReport.union(nonCltReport).toDF()
  }

  /*
    ACMRef: Ron Kohavi, Alex Deng, Roger Longbotham, and Ya Xu.  2014. Seven Rules of Thumb for Web Site Experimenters.
    In Proceedings of the 20th ACM SIGKDD international conference on Knowledge discovery and data mining (KDD '14), pp. 1857-1866.
    https://exp-platform.com/Documents/2014%20experimentersRulesOfThumb.pdf
   */
  def checkHeuristicsForCLT: UserDefinedFunction = udf { (sampleSize: Long, skewness: Double) =>
    if (math.abs(skewness) > 1) {
      sampleSize >= (355 * skewness * skewness).toLong
    } else {
      /*
        Montgomery, Douglas C. Applied Statistics and Probability
        for Engineers. 5th. s.l. : John Wiley & Sons, Inc, 2010. 978-
        0470053041.
       */
      sampleSize >= 30
    }
  }

  def fillBaseParameters(transformer: BaseStatisticTransformer): transformer.type = {
    transformer
      .setAdditiveColumn($(additiveColumn))
      .setAlpha($(alpha))
      .setDataProviderColumn($(metricSourceColumn))
      .setEntityIdColumn($(entityIdColumn))
      .setExperimentColumn($(experimentColumn))
      .setMetricNameColumn($(metricNameColumn))
      .setSrmAlpha($(srmAlpha))
      .setValueColumn($(valueColumn))
      .setVariantColumn($(variantColumn))
  }
}

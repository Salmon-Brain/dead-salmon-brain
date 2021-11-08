package ai.salmon.computing

import ai.salmon.inputs.{
  AccessLogTransformer,
  ClickhouseDataSource,
  CsvHelper,
  NginxRawLogTransformer
}
import org.apache.spark.ml.Pipeline
import org.apache.spark.sql.{ DataFrame, SparkSession }

object ReportBuilder {
  def buildReport(config: Config, spark: SparkSession): DataFrame = {
    val events = config.input match {
      case Input.Files      => readFiles(spark, config)
      case Input.Clickhouse => readClickhouse(spark, config)
    }

    val accessLogConfig = config.accessLog match {
      case Some(value) => value
      case None        => throw new IllegalArgumentException("Access log config required")
    }
    val accessLogTransformer = new AccessLogTransformer()
    accessLogTransformer.setExpUidExpression(accessLogConfig.expUid)
    accessLogTransformer.setMetricNameExpression(accessLogConfig.metricName)
    accessLogTransformer.setMetricValueExpression(accessLogConfig.metricValue)
    accessLogTransformer.setVariantIdExpression(accessLogConfig.variantId)

    val ratioMetrics = Seq(RatioMetricData("clicks", "views", "ctr"))

    val statPipeline = new Pipeline().setStages(
      Array(
        accessLogTransformer,
        new CumulativeMetricTransformer()
          .setRatioMetricsData(ratioMetrics),
        new OutlierRemoveTransformer(),
        new AutoStatisticsTransformer()
      )
    )

    statPipeline.fit(events).transform(events)
  }

  def readFiles(spark: SparkSession, config: Config): DataFrame = {
    assert(config.files.nonEmpty)
    val csv = CsvHelper.readCsv(spark, config.files)
    new NginxRawLogTransformer().transform(csv)
  }

  def readClickhouse(spark: SparkSession, config: Config): DataFrame = {
    assert(config.clickhouse != null)
    val source = ClickhouseDataSource
    source.readData(spark, config.clickhouse.get)
  }
}

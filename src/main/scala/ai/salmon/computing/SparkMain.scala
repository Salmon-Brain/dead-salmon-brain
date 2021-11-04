package ai.salmon.computing

import org.apache.spark.sql.{ SaveMode, SparkSession }

object SparkMain {
  def main(args: Array[String]): Unit = {

    assert(args.length > 0, "Config file required")

    val spark = SparkSession.builder.appName("Salmon Application").getOrCreate()
    val config = ConfigUtils.readConfig(args(0))
    val report = ReportBuilder.buildReport(config, spark)
    report
      .coalesce(1)
      .write
      .mode(SaveMode.Overwrite)
      .json(config.output)
  }
}

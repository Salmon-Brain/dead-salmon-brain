package ai.salmonbrain.ruleofthumb

import org.apache.spark.sql.{ SaveMode, SparkSession }

object Main {

  def main(args: Array[String]): Unit = {
    assert(args.length > 0, "Config file required")

    val config: Config = ConfigUtils.readConfig(args(0))

    val builder = SparkSession.builder.appName("Salmon Brain Application")
    if (args.length > 1) {
      // set master url if specified. usually is used to run
      // locally in standalone mode (without spark cluster)
      builder.master(args(1))
    }
    val spark = builder.getOrCreate()
    val report = ReportBuilder.buildReport(config, spark)
    report.write
      .partitionBy("experimentUid")
      .mode(SaveMode.Overwrite)
      .json(config.output)
  }

}

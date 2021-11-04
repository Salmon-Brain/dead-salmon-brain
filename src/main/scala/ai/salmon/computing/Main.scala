package ai.salmon.computing

import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}

import java.io.File

object Main {

  def main(args: Array[String]) {
    assert(args.length > 0, "Config file required")

    val config: Config = ConfigUtils.readConfig(args(0))
    val file: File = new File(config.output)
    assert(
      (file.exists && file.canWrite) || file.createNewFile,
      "Failed to open or create output file: " + config.output
    )

    val spark = SparkSession
      .builder()
      .appName("Spark-LOCAL")
      .config("spark.master", "local")
      .getOrCreate()

    val report: DataFrame = ReportBuilder.buildReport(config, spark)
    report
      .coalesce(1)
      .write
      .mode(SaveMode.Overwrite)
      .json(file.getAbsolutePath)
  }

}

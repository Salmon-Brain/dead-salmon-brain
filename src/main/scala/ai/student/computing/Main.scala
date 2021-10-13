package ai.student.computing

import ai.student.inputs.{
  AccessLogTransformer,
  ClickhouseDataSource,
  CsvHelper,
  NginxRawLogTransformer
}
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.frugalmechanic.optparse._
import org.apache.spark.ml.Pipeline
import org.apache.spark.sql.functions.col
import org.apache.spark.sql.{ DataFrame, SparkSession }

import java.io.{ File, FileInputStream, FileWriter }

object Main extends OptParse {
  val configFile = StrOpt(short = 'c')
  val output = StrOpt(short = 'o')

  def readFiles(spark: SparkSession, config: Config): DataFrame = {
    assert(config.files != null)
    val csv = CsvHelper.readCsv(spark, config.files)
    new NginxRawLogTransformer().transform(csv)
  }

  def readClickhouse(spark: SparkSession, config: Config): DataFrame = {
    assert(config.clickhouse != null)
    val source = ClickhouseDataSource
    source.readData(spark, config.clickhouse.get)
  }

  def main(args: Array[String]) {
    parse(args)

    val file: File = new File(output.get)

    assert((file.exists && file.canWrite) || file.createNewFile)

    val factory = new YAMLFactory()
    val mapper = new ObjectMapper(factory)
    mapper.registerModule(DefaultScalaModule)
    val config: Config =
      mapper.readValue(new FileInputStream(new File(configFile.get)), classOf[Config])

    val spark = SparkSession
      .builder()
      .appName("Spark-LOCAL")
      .config("spark.master", "local")
      .getOrCreate()

    val events = config.input match {
      case Input.Files      => readFiles(spark, config)
      case Input.Clickhouse => readClickhouse(spark, config)
    }

    val accessLogConfig = config.accessLog match {
      case Some(value) => value
      case None        => throw new IllegalArgumentException("Access log config required")
    }
    assert(config.accessLog != null)
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
        new WelchStatisticsTransformer()
      )
    )

    val report = statPipeline.fit(events).transform(events)

    renderReport(report, file)
  }

  def getHexColor(num: Double): String = {
    val c = getColor(num)
    "#%06x".format((c._1 << 16) + (c._2 << 8) + c._3)
  }

  def getColor(num: Double): (Integer, Integer, Integer) = {
    if (num < 0) {
      (getColorRate(-num), 0, 0)
    } else {
      (0, getColorRate(num), 0)
    }
  }
  def getColorRate(num: Double): Integer = {
    if (num > 100) {
      255
    } else {
      55 + (200 * num / 100).toInt
    }
  }

  def renderReport(ds: org.apache.spark.sql.DataFrame, file: File): Unit = {

    val data = ds.select(
      col("metricName"),
      col("statisticsData.controlSize"),
      col("statisticsData.treatmentSize"),
      col("statisticsData.statResult.controlMean"),
      col("statisticsData.statResult.treatmentMean"),
      col("statisticsData.statResult.absoluteEffect"),
      col("statisticsData.statResult.percentageEffect"),
      col("statisticsData.statResult.percentageLeft"),
      col("statisticsData.statResult.percentageRight"),
      col("statisticsData.statResult.pValue")
    )

    var strHeader = "<tr>"

    strHeader += "<th>Metric</th>"
    strHeader += "<th>Control size</th>"
    strHeader += "<th>Treatment size</th>"
    strHeader += "<th>Control mean</th>"
    strHeader += "<th>Treatment mean</th>"
    strHeader += "<th>Absolute effect</th>"
    strHeader += "<th>Effect percent</th>"
    strHeader += "<th>Left percent</th>"
    strHeader += "<th>Right percent</th>"
    strHeader += "<th>P value</th>"

    strHeader += "</tr>"

    var body = ""

    for (row <- data.collect()) {
      body += "<tr>"

      body += "<td>%s</td>".format(row.getAs[String]("metricName"))
      body += ("<td>%d</td>".format(row.getAs[Integer]("controlSize")))
      body += "<td>%d</td>".format(row.getAs[Integer]("treatmentSize"))
      body += "<td>%.2f</td>".format(row.getAs[Double]("controlMean"))
      body += "<td>%.2f</td>".format(row.getAs[Double]("treatmentMean"))
      body += "<td>%.2f</td>".format(row.getAs[Double]("absoluteEffect"))
      val percentageEffect = row.getAs[Double]("percentageEffect")
      val percentageLeft = row.getAs[Double]("percentageLeft")
      val percentageRight = row.getAs[Double]("percentageRight")
      val pValue = row.getAs[Double]("pValue")
      body += "<td><font color=\"%s\">%.2f</font></td>".format(
        getHexColor(percentageEffect),
        percentageEffect
      )
      body += "<td><font color=\"%s\">%.2f</font></td>".format(
        getHexColor(percentageLeft),
        percentageLeft
      )
      body += "<td><font color=\"%s\">%.2f</font></td>".format(
        getHexColor(percentageRight),
        percentageRight
      )
      body += "<td><font color=\"%s\">%.2f</font></td>".format(
        getHexColor((1 - pValue) * 100),
        pValue
      )
      body += "</tr>"
    }

    val html = "<html><body><table>" + strHeader + body + "</table></body><html>"

    val writer = new FileWriter(file)
    writer.write(html)
    writer.close()
  }
}

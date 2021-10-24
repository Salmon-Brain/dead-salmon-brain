package ai.salmon.inputs

import ai.salmon.computing.ClickhouseConfig
import org.apache.spark.sql.{ DataFrame, SparkSession }

object ClickhouseDataSource {
  def readData(spark: SparkSession, config: ClickhouseConfig): DataFrame = {
    val strCols = config.mapping.keys.mkString(",")

    var filter = ""
    if (isNotBlank(config.filter)) {
      filter += "where %s".format(config.filter)
    }

    val sql = """select %s from %s %s""".format(strCols, config.dbtable, filter)

    val dbDF = spark.read
      .format("jdbc")
      .option("driver", config.driver)
      .option("url", config.url)
      .option("user", config.user)
      .option("password", config.password)
      .option("dbtable", s"( $sql ) t")
      .load()

    val colsToKeep = config.mapping.values.toList
    dbDF
      .select(
        dbDF.columns.map(c => dbDF(c).alias(config.mapping.getOrElse(c, c))): _*
      )
      .select(colsToKeep.head, colsToKeep.tail: _*)
  }

  def isNotBlank(str: String): Boolean = {
    str != null && str.trim.nonEmpty
  }
}

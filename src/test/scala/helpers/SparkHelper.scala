package helpers

import org.apache.log4j.{ Level, Logger }
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

trait SparkHelper {
  Logger.getLogger("org").setLevel(Level.ERROR)
  Logger.getLogger("akka").setLevel(Level.ERROR)

  lazy val conf = SparkHelper._sparkConf
  lazy val spark = SparkHelper._spark
  lazy val sc = SparkHelper._sc
  lazy val sqlc = SparkHelper._sqlc
}

object SparkHelper {
  private lazy val _sparkConf = new SparkConf()
    .setMaster("local[1]")
    .setAppName(getClass.getName)

  private lazy val _spark = SparkSession.builder().config(_sparkConf).getOrCreate()
  private lazy val _sc = _spark.sparkContext
  private lazy val _sqlc = _spark.sqlContext
}

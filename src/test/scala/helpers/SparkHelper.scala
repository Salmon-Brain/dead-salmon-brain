package helpers

import org.apache.spark.sql.{SQLContext, SparkSession}
import org.apache.spark.{SparkConf, SparkContext}


trait SparkHelper {
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

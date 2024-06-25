package helpers

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

trait SharedSparkSession {

  private lazy val _sparkConf = new SparkConf()
    .setMaster("local[1]")
    .setAppName(getClass.getName)

  lazy implicit val spark: SparkSession = {
    SparkSession
      .builder()
      .master("local")
      .appName("spark session")
      .config("spark.sql.shuffle.partitions", "1")
      .config(_sparkConf)
      .getOrCreate()
  }

  lazy val conf = spark.conf
  lazy val sc = spark.sparkContext
  lazy val sqlc = spark.sqlContext
}

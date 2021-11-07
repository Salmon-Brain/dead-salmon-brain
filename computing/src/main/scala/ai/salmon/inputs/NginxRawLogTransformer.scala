package ai.salmon.inputs

import org.apache.spark.ml.Transformer
import org.apache.spark.ml.param.ParamMap
import org.apache.spark.ml.util.Identifiable
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions.{ col, udf, unix_timestamp }
import org.apache.spark.sql.types._
import org.apache.spark.sql.{ DataFrame, Dataset, SparkSession }

case class Request(verb: String, path: String, protocol: String)

class NginxRawLogTransformer(override val uid: String) extends Transformer {

  def this() = this(Identifiable.randomUID("NginxRawLogTransformer"))

  private val toRequestUDF: UserDefinedFunction = udf({ s: String =>
    val Array(verb, path, protocol) = s.split(" ")
    Request(verb, path, protocol)
  })

  private val formDateUDF: UserDefinedFunction =
    udf((date: String, tz: String) => date.substring(1) + tz.substring(0, tz.length - 1))

  private val resultSchema: StructType = StructType(
    Array(
      StructField("entityUid", StringType, true),
      StructField("timestamp", LongType, true),
      StructField("path", StringType, true)
    )
  )

  override def transform(dataset: Dataset[_]): DataFrame = {
    dataset
      .withColumn("request", toRequestUDF(col("request")))
      .withColumn(
        "timestamp",
        unix_timestamp(formDateUDF(col("date"), col("tz")), "dd/MMM/yyyy:HH:mm:ssZ")
          .cast(LongType)
      )
      .filter(col("status_code") === 200)
      .select("entityUid", "timestamp", "request.path")
  }

  override def copy(extra: ParamMap): Transformer = defaultCopy(extra)

  override def transformSchema(schema: StructType): StructType = resultSchema
}
object CsvHelper {
  def readCsv(spark: SparkSession, files: Seq[String]): DataFrame = {
    spark.read
      .option("delimiter", " ")
      .csv(files: _*)
      .withColumnRenamed("_c2", "entityUid")
      .withColumnRenamed("_c3", "date")
      .withColumnRenamed("_c4", "tz")
      .withColumnRenamed("_c5", "request")
      .withColumn("status_code", col("_c6").cast(IntegerType))
  }
}

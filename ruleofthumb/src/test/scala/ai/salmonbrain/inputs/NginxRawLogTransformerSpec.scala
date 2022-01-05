package ai.salmonbrain.inputs

import helpers.SparkHelper
import org.apache.spark.sql.DataFrame
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class NginxRawLogTransformerSpec extends AnyFlatSpec with SparkHelper with Matchers {

  private val logsResource: String = getClass.getResource("/nginx_sample_1.txt").getPath

  "NginxRawLogTransformer" should "be" in {
    import sqlc.implicits._
    val rawLogs: DataFrame = CsvHelper.readCsv(spark, Seq(logsResource))

    val transformer = new NginxRawLogTransformer()
    val logs = transformer.transform(rawLogs)

    val expected: DataFrame = sc
      .parallelize(
        Seq(
          ("user1", ts("2021-10-01 10:00:00+0000"), "/view/1"),
          ("user1", ts("2021-10-01 10:01:00+0000"), "/view/2"),
          ("user1", ts("2021-10-01 10:02:00+0000"), "/view/3"),
          ("user1", ts("2021-10-01 10:03:00+0000"), "/view/4"),
          ("user1", ts("2021-10-01 10:05:00+0000"), "/click/1"),
          ("user1", ts("2021-10-01 10:06:00+0000"), "/click/2"),
          ("user2", ts("2021-10-01 10:10:00+0000"), "/view/1"),
          ("user2", ts("2021-10-01 10:11:00+0000"), "/view/2"),
          ("user2", ts("2021-10-01 10:15:00+0000"), "/click/2")
        )
      )
      .toDF("entityUid", "timestamp", "path")

    logs.collect() should contain theSameElementsAs (expected.collect())
  }

  private def ts(str: String): Long = {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssZ")
    ZonedDateTime.parse(str, formatter).toEpochSecond
  }
}

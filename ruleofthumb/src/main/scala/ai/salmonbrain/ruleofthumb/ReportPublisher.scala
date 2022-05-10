package ai.salmonbrain.ruleofthumb

import org.apache.commons.io.IOUtils
import org.apache.http.HttpHeaders
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.current_timestamp
import org.slf4j.LoggerFactory

import java.nio.charset.StandardCharsets

object ReportPublisher {
  private val log = LoggerFactory.getLogger("ReportPublisher")

  private def sendReport(url: String, report: DataFrame): Unit = {
    val encodedReport = report
      // TODO: replace current_timestamp with real report period end?
      .withColumn("ts", current_timestamp())

    encodedReport.toJSON.foreachPartition((pIt: Iterator[String]) => {
      pIt.foreach(dto => {
        val client = HttpClientBuilder.create().build()
        val post = new HttpPost(url)
        post.addHeader(HttpHeaders.CONTENT_TYPE, "application/json")
        post.setEntity(new StringEntity(dto))

        val response = client.execute(post)
        val entity = response.getEntity
        val responseStr = IOUtils.toString(entity.getContent, StandardCharsets.UTF_8)
        if (response.getStatusLine.getStatusCode == 200) {
          log.debug(
            "Successfully posted with result [{}]",
            responseStr: Any
          )
        } else {
          log.error(
            "Failed to post with status = {} and result [{}]",
            response.getStatusLine.getStatusCode: Any,
            responseStr: Any
          )
        }
      })
    })
  }

  def publishReport(config: Config, report: DataFrame): Unit = {
    config.postReportUrl match {
      case Some(url) => sendReport(url, report)
      case None      => log.info("Property 'postReportUrl' not set, skip report posting")
    }
  }
}

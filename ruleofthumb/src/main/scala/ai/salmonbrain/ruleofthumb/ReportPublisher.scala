package ai.salmonbrain.ruleofthumb

import ai.salmonbrain.experiment.api.dto.ReportDto
import org.apache.commons.io.IOUtils
import org.apache.http.HttpHeaders
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.spark.sql.{ DataFrame, Encoders }
import org.codehaus.jackson.map.ObjectMapper
import org.slf4j.LoggerFactory

import java.nio.charset.StandardCharsets

object ReportPublisher {
  private val log = LoggerFactory.getLogger("ReportPublisher")
  private val objectMapper = new ObjectMapper();

  private def toJson(dto: ReportDto): String = {
    objectMapper.writeValueAsString(dto)
  }

  private def sendReport(url: String, report: DataFrame): Unit = {
    val reportEncoder = Encoders.bean(classOf[ReportDto])
    val encodedReport = report.as(reportEncoder)

    encodedReport.foreachPartition((pIt: Iterator[ReportDto]) => {
      pIt.foreach(dto => {
        val client = HttpClientBuilder.create().build()
        val post = new HttpPost(url)
        post.addHeader(HttpHeaders.CONTENT_TYPE, "application/json")
        post.setEntity(new StringEntity(toJson(dto)))

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

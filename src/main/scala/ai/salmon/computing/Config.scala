package ai.salmon.computing

import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.module.scala.JsonScalaEnumeration

case class Config(
    @JsonScalaEnumeration(classOf[InputTypeRef]) input: Input.Input = Input.Files,
    files: Seq[String] = Seq.empty,
    accessLog: Option[AccessLogConfig],
    clickhouse: Option[ClickhouseConfig]
) {}

object Input extends Enumeration {
  type Input = Value
  val Files, Clickhouse = Value
}
class InputTypeRef extends TypeReference[Input.type]

case class AccessLogConfig(
    metricName: String = "",
    metricValue: String = "",
    expUid: String = "",
    variantId: String = ""
)

case class ClickhouseConfig(
    driver: String = "com.github.housepower.jdbc.ClickHouseDriver",
    url: String = "jdbc:clickhouse://127.0.0.1:9000",
    user: String = "default",
    password: String = "",
    dbtable: String = "",
    filter: String = "",
    mapping: Map[String, String] = Map.empty
) {}

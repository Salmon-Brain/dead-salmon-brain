package ai.salmon.computing

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.scala.DefaultScalaModule

import java.io.File

object ConfigUtils {
  private val factory = new YAMLFactory()
  private val mapper = new ObjectMapper(factory)
  mapper.registerModule(DefaultScalaModule)

  def readConfig(path:String):Config = {
    mapper.readValue(new File(path), classOf[Config])
  }

}

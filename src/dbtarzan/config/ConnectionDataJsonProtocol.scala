package dbtarzan.config

import spray.json._

object ConnectionDataJsonProtocol extends DefaultJsonProtocol {
import DefaultJsonProtocol._
  implicit val connectionDataFormat = jsonFormat(ConnectionData, "jar", "name", "driver", "url", "schema", "user", "password", "instances")
}

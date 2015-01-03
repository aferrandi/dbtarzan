package dbtarzan.config

import spray.json._
import DefaultJsonProtocol._
import java.io.File

case class ConnectionData(
	jar : String, 
	name: String, 
	driver: String, 
	url: String,
	schema: Option[String],
	user: String, 
	password: String
	)

object ConnectionDataJsonProtocol extends DefaultJsonProtocol {
  implicit val connectionDataFormat = jsonFormat(ConnectionData, "jar", "name", "driver", "url", "schema", "user", "password")
}

object ConfigReader {

	import ConnectionDataJsonProtocol._


	def read(file : File) : List[ConnectionData] = {
		val text = scala.io.Source.fromFile(file, "utf-8").getLines.mkString
		read(text)
	}
	
	def read(text : String) : List[ConnectionData] = {
		val result = text.parseJson
 		result.convertTo[Seq[ConnectionData]].toList
	}
}
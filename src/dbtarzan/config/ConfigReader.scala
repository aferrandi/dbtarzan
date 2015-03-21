package dbtarzan.config

import spray.json._
import java.io.File
import dbtarzan.db.util.FileReadWrite

case class ConnectionData(
	/* the path of the jar file of the driver */
	jar : String, 
	/* name of the database, as shown in the GUI */
	name: String, 
	/* the class name of the JDBC driver */
	driver: String, 
	/* the JDBC url used to connect*/
	url: String,
	/* the schema containing the data, in multi-schema databases (Oracle) */
	schema: Option[String],
	/* the user id to login to the database */
	user: String, 
	/* the password to login to the database */
	password: String,
	/* the number of connections that the application will open against this database (1 if not defined) */
	instances: Option[Int]
	)

object ConnectionDataJsonProtocol extends DefaultJsonProtocol {
import DefaultJsonProtocol._
  implicit val connectionDataFormat = jsonFormat(ConnectionData, "jar", "name", "driver", "url", "schema", "user", "password", "instances")
}

object ConfigReader {
	import ConnectionDataJsonProtocol._

	def read(name : String) : List[ConnectionData] = {
		val text = FileReadWrite.readFile(name)
		parseText(text)
	}
	
	def parseText(text : String) : List[ConnectionData] = {
		val result = text.parseJson
 		result.convertTo[Seq[ConnectionData]].toList
	}
}
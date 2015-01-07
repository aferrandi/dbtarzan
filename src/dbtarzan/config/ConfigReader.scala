package dbtarzan.config

import spray.json._
import DefaultJsonProtocol._
import java.io.File

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
  implicit val connectionDataFormat = jsonFormat(ConnectionData, "jar", "name", "driver", "url", "schema", "user", "password", "instances")
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
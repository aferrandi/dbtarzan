package dbtarzan.config

import spray.json._
import java.io.File
import dbtarzan.db.util.FileReadWrite



/* writes the databases configuration file */
object ConfigWriter {
	import ConnectionDataJsonProtocol._

	def write(name : String, connections : List[ConnectionData]) : Unit  = {
		val text = toText(connections)
		FileReadWrite.writeFile(name, text)		
	}
	
	def toText(connections : List[ConnectionData]) : String = {
		val result = connections.toJson
 		result.prettyPrint
	}
}
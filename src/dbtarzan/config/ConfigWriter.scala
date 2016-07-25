package dbtarzan.config

import spray.json._
import java.io.File
import dbtarzan.db.util.FileReadWrite
import dbtarzan.types.ConfigPath


/* writes the databases configuration file */
object ConfigWriter {
	import ConnectionDataJsonProtocol._

	def write(path : ConfigPath, connections : List[ConnectionData]) : Unit  = {
		val text = toText(connections)
		FileReadWrite.writeFile(path.path, text)		
	}
	
	def toText(connections : List[ConnectionData]) : String = {
		val result = connections.toJson
 		result.prettyPrint
	}
}
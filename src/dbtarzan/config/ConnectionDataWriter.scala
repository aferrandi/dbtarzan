package dbtarzan.config

import spray.json._
import dbtarzan.db.util.FileReadWrite
import dbtarzan.types.ConfigPath


/* writes the databases configuration file */
object ConnectionDataWriter {
	import ConnectionDataJsonProtocol._

	def write(path : ConfigPath, connections : List[ConnectionData]) : Unit  = {
		val text = toText(connections)
		FileReadWrite.writeFile(path.path, text)		
	}
	
	def toText(connections : List[ConnectionData]) : String = {
		val result = connections.sortBy(_.name).toJson
 		result.prettyPrint
	}
}
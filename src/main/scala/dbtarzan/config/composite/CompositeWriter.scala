package dbtarzan.config.composite

import dbtarzan.db.Composite
import dbtarzan.db.util.FileReadWrite
import spray.json._

import java.nio.file.Path

/* writes the databases configuration file */
object CompositeWriter {
  import CompositeJsonProtocol._
	def write(path : Path, connections : List[Composite]) : Unit  = {
		val text = toText(connections)
		FileReadWrite.writeFile(path, text)		
	}
	
	def toText(connections : List[Composite]) : String = {
		val result = connections.sortBy(_.compositeId.compositeName).toJson
 		result.prettyPrint
	}
}
package dbtarzan.config.connections

import spray.json._
import java.io.FileNotFoundException
import dbtarzan.db.util.FileReadWrite
import java.nio.file.Path

/* reads the databases configuration file */
object ConnectionDataReader {
	import ConnectionDataJsonProtocol._

	def read(path: Path) : List[ConnectionData] = {
		try { 
			val text = FileReadWrite.readFile(path)
			parseText(text)
		} catch {
		  case _: FileNotFoundException => List.empty[ConnectionData]
		}
	}
	
	def parseText(text : String) : List[ConnectionData] = {
		val result = text.parseJson
 		result.convertTo[Seq[ConnectionData]].toList.sortBy(_.name)
	}
}
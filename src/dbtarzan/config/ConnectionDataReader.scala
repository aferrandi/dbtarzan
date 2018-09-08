package dbtarzan.config

import spray.json._
import java.io.FileNotFoundException
import dbtarzan.db.util.FileReadWrite
import dbtarzan.types.ConfigPath

/* reads the databases configuration file */
object ConnectionDataReader {
	import ConnectionDataJsonProtocol._

	def read(path: ConfigPath) : List[ConnectionData] = {
		try { 
			val text = FileReadWrite.readFile(path.path)
			parseText(text)
		} catch {
		  case e: FileNotFoundException => List.empty[ConnectionData]
		}
	}
	
	def parseText(text : String) : List[ConnectionData] = {
		val result = text.parseJson
 		result.convertTo[Seq[ConnectionData]].toList
	}
}
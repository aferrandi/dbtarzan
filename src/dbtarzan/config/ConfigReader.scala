package dbtarzan.config

import spray.json._
import java.io.{ File, FileNotFoundException }
import dbtarzan.db.util.FileReadWrite


/* reads the databases configuration file */
object ConfigReader {
	import ConnectionDataJsonProtocol._

	def read(name : String) : List[ConnectionData] = {
		try { 
			 val text = FileReadWrite.readFile(name)
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
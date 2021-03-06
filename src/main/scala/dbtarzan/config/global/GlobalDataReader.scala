package dbtarzan.config.global

import spray.json._
import java.io.FileNotFoundException
import dbtarzan.db.util.FileReadWrite
import dbtarzan.localization.Languages
import java.nio.file.Path

/* reads the databases configuration file */
object GlobalDataReader {
	import GlobalDataJsonProtocol._

	def read(path: Path) : GlobalData = {
		try { 
			val text = FileReadWrite.readFile(path)
			parseText(text)
		} catch {
		  case e: FileNotFoundException => GlobalData(Languages.default, None)
		}
	}

	def parseText(text : String) : GlobalData = {
		text.parseJson.convertTo[GlobalData]
	}
}
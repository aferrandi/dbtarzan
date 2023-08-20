package dbtarzan.config.global

import java.io.FileNotFoundException
import dbtarzan.db.util.FileReadWrite
import dbtarzan.localization.Languages
import grapple.json.Json

import java.nio.file.Path

/* reads the databases configuration file */
object GlobalDataReader {

	def read(path: Path) : GlobalData = {
		try
			val text = FileReadWrite.readFile(path)
			parseText(text)
		catch
		  case _: FileNotFoundException => GlobalData(Languages.default, None)
	}

	def parseText(text : String) : GlobalData =
		Json.parse(text).as[GlobalData]
}
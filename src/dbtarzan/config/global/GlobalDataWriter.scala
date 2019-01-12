package dbtarzan.config.global

import spray.json._
import dbtarzan.db.util.FileReadWrite
import java.nio.file.Path


/* writes the databases configuration file */
object GlobalDataWriter {
	import GlobalDataJsonProtocol._

	def write(path : Path, data : GlobalData) : Unit  = {
		val text = data.toJson.prettyPrint
		FileReadWrite.writeFile(path, text)		
	}
}
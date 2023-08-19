package dbtarzan.config.global

import dbtarzan.db.util.FileReadWrite
import java.nio.file.Path
import grapple.json.{*, given}

/* writes the databases configuration file */
object GlobalDataWriter {
	def write(path : Path, data : GlobalData) : Unit  = {
		val text = Json.toPrettyPrint(Json.toJson(data))
		FileReadWrite.writeFile(path, text)		
	}
}
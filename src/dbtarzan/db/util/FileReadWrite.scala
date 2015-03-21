package dbtarzan.db.util

import ResourceManagement._
import java.io.FileWriter
import scala.io.Source
import java.nio.file.{Paths, Files}

object FileReadWrite {
	def fileExist(name : String) = Files.exists(Paths.get(name))

	def writeFile(name : String, content : String) : Unit =
		using(new FileWriter(name)) { fw =>
			fw.write(content)
		}

	def readFile(name : String) : String = 
		Source.fromFile(name, "utf-8").getLines.mkString
}
package dbtarzan.db.util

import ResourceManagement._
import java.io.FileWriter
import scala.io.Source
import java.nio.file.{Files, Path}

/* simple functions to read, write and check the existance of a small (configuration) file */
object FileReadWrite {
	def fileExist(name : Path) = name.toFile().canRead()

	def writeFile(name : Path, content : String) : Unit = {
		println("Creating:"+name)
		Option(name.getParent()).foreach(Files.createDirectories(_)) 
		using(new FileWriter(name.toFile())) { fw =>
			fw.write(content)
		}
	}

	def readFile(name : Path) : String = {
		println("Reading:"+name.toFile().getPath())
		Source.fromFile(name.toFile(), "utf-8").getLines.mkString
	}
}
package dbtarzan.db.util

import ResourceManagement._
import java.io.{ FileWriter, File }
import scala.io.Source
import java.nio.file.{Paths, Files, Path}

/* simple functions to read, write and check the existance of a small (configuration) file */
object FileReadWrite {
	def fileExist(name : Path) = name.toFile().canRead()

	def writeFile(name : Path, content : String) : Unit = {
		println("Creating:"+name)
		Files.createDirectories(name.getParent())
		using(new FileWriter(name.toFile())) { fw =>
			fw.write(content)
		}
	}

	def readFile(name : Path) : String = {
		println("Reading:"+name.toFile().getPath())
		Source.fromFile(name.toFile(), "utf-8").getLines.mkString
	}
}
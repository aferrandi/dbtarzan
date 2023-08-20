package dbtarzan.config.connections

import java.io.FileNotFoundException
import dbtarzan.db.util.FileReadWrite
import java.nio.file.Path
import grapple.json.{ *, given }


/* reads the databases configuration file */
object ConnectionDataReader {
  def read(path: Path) : List[ConnectionData] = {
    try
      val text = FileReadWrite.readFile(path)
      parseText(text)
    catch
      case _: FileNotFoundException => List.empty[ConnectionData]
  }

  def parseText(text : String) : List[ConnectionData] = {
    val result = Json.parse(text).as[Seq[ConnectionData]]
    result.toList.sortBy(_.name)
  }
}
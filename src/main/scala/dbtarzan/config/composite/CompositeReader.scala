package dbtarzan.config.composite

import dbtarzan.db.Composite
import dbtarzan.db.util.FileReadWrite
import grapple.json.{ *, given }

import java.io.FileNotFoundException
import java.nio.file.Path

/* reads the databases configuration file */
object CompositeReader {
  def read(path: Path) : List[Composite] = {
    try
      val text = FileReadWrite.readFile(path)
      parseText(text)
    catch
      case _: FileNotFoundException => List.empty[Composite]

  }

  def parseText(text : String) : List[Composite] = {
    val result = Json.parse(text).as[Seq[Composite]]
    result.toList.sortBy(_.compositeId.compositeName)
  }
}
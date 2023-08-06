package dbtarzan.config.composite

import dbtarzan.db.Composite
import dbtarzan.db.util.FileReadWrite
import spray.json._

import java.io.FileNotFoundException
import java.nio.file.Path

/* reads the databases configuration file */
object CompositeReader {
  import CompositeJsonProtocol._
  def read(path: Path) : List[Composite] = {
    try {
      val text = FileReadWrite.readFile(path)
      parseText(text)
    } catch {
      case _: FileNotFoundException => List.empty[Composite]
    }
  }

  def parseText(text : String) : List[Composite] = {
    val result = text.parseJson
    result.convertTo[Seq[Composite]].toList.sortBy(_.compositeId.compositeName)
  }
}
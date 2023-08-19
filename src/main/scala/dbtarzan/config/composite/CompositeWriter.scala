package dbtarzan.config.composite

import dbtarzan.db.Composite
import dbtarzan.db.util.FileReadWrite
import grapple.json.{ *, given }

import java.nio.file.Path

/* writes the databases configuration file */
object CompositeWriter {
  def write(path : Path, connections : List[Composite]) : Unit  = {
    val text = toText(connections)
    FileReadWrite.writeFile(path, text)
  }

  def toText(connections : List[Composite]) : String = {
    val result = connections.sortBy(_.compositeId.compositeName)
    Json.toPrettyPrint(Json.toJson(result))
  }
}
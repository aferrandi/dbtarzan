package dbtarzan.config.connections

import dbtarzan.db.util.FileReadWrite
import java.nio.file.Path
import grapple.json.{ *, given }

/* writes the databases configuration file */
object ConnectionDataWriter {
  def write(path : Path, connections : List[ConnectionData]) : Unit  = {
    val text = toText(connections)
    FileReadWrite.writeFile(path, text)
  }

  def toText(connections : List[ConnectionData]) : String = {
    val result = connections.sortBy(_.name)
    Json.toPrettyPrint(Json.toJson(result))
  }
}
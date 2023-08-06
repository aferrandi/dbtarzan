package dbtarzan.config.connections

import spray.json._
import dbtarzan.db.util.FileReadWrite
import java.nio.file.Path

/* writes the databases configuration file */
object ConnectionDataWriter {
  import ConnectionDataJsonProtocol._

  def write(path : Path, connections : List[ConnectionData]) : Unit  = {
    val text = toText(connections)
    FileReadWrite.writeFile(path, text)
  }

  def toText(connections : List[ConnectionData]) : String = {
    val result = connections.sortBy(_.name).toJson
    result.prettyPrint
  }
}
package dbtarzan.db.foreignkeys

import dbtarzan.db._
import dbtarzan.db.util.FileReadWrite
import spray.json._

import java.nio.file.Path

class ForeignKeyDirectionFormat extends RootJsonFormat[ForeignKeyDirection] {
  def write(direction: ForeignKeyDirection): JsValue = JsString(DBEnumsText.foreignKeyDirectionToText(direction))

  def read(json: JsValue): ForeignKeyDirection = json match {
    case JsString("STRAIGHT") => ForeignKeyDirection.STRAIGHT
    case JsString("TURNED") => ForeignKeyDirection.TURNED
    case _ => throw new DeserializationException("ForeignKeyDirection string expected")
  }
}

case class FieldsOnTableOneDb(table : String, fields : List[String])
case class ForeignKeyOneDb(name: String, from : FieldsOnTableOneDb, to: FieldsOnTableOneDb, direction : ForeignKeyDirection)
case class ForeignKeysOneDb(keys : List[ForeignKeyOneDb])
case class ForeignKeysForTableOneDb(table : String, keys : ForeignKeysOneDb)
case class ForeignKeysForTableListOneDb(keys : List[ForeignKeysForTableOneDb])

object ForeignKeysForTableJsonProtocolOneDb {
  import DefaultJsonProtocol._

  implicit val foreignKeyDirectionFormat: ForeignKeyDirectionFormat = new ForeignKeyDirectionFormat()
  implicit val fieldsOnTableFormatOneDb: RootJsonFormat[FieldsOnTableOneDb] = jsonFormat(FieldsOnTableOneDb.apply, "table", "fields" )
  implicit val foreignKeyFormatOneDb: RootJsonFormat[ForeignKeyOneDb] = jsonFormat(ForeignKeyOneDb.apply, "name", "from", "to", "direction")
  implicit val foreignKeysFormatOneDb: RootJsonFormat[ForeignKeysOneDb] = jsonFormat(ForeignKeysOneDb.apply, "keys")
  implicit val foreignKeysForTableFormatOneDb: RootJsonFormat[ForeignKeysForTableOneDb] = jsonFormat(ForeignKeysForTableOneDb.apply, "name", "keys")
  implicit val foreignKeysForTableListFormatOneDb: RootJsonFormat[ForeignKeysForTableListOneDb] = jsonFormat(ForeignKeysForTableListOneDb.apply, "keys")
}

class ForeignKeysFile(dirPath: Path, filename: String, databaseId: DatabaseId, simpleDatabaseId: SimpleDatabaseId) {
	import DefaultJsonProtocol._
	import ForeignKeysForTableJsonProtocolOneDb._

  val fileName : Path = dirPath.resolve(filename+".fgk")

	def writeAsFile(list : List[ForeignKeysForTable]) : Unit = {
    val keys = mapFromForeignKeys(list)
    FileReadWrite.writeFile(fileName, keys.toJson.prettyPrint)
  }

	def readFromFile() : List[ForeignKeysForTable] = {
		val text = FileReadWrite.readFile(fileName)
    val keys = text.parseJson.convertTo[ForeignKeysForTableListOneDb].keys
    mapToForeignKeys(keys)
  }

  private def mapToForeignKeys(keys: List[ForeignKeysForTableOneDb]): List[ForeignKeysForTable] = {
    keys.map(k => {
      ForeignKeysForTable(TableId(databaseId, simpleDatabaseId, k.table),
        ForeignKeys(k.keys.keys.map(l => ForeignKey(l.name,
          FieldsOnTable(TableId(databaseId, simpleDatabaseId, l.from.table), l.from.fields),
          FieldsOnTable(TableId(databaseId, simpleDatabaseId, l.to.table), l.to.fields),
          l.direction)))
      )
    })
  }

  private def mapFromForeignKeys(keys: List[ForeignKeysForTable]): List[ForeignKeysForTableOneDb] = {
    keys.map(k => ForeignKeysForTableOneDb(k.tableId.tableName,
      ForeignKeysOneDb(k.keys.keys.map(l => ForeignKeyOneDb(l.name,
        FieldsOnTableOneDb(l.from.table.tableName, l.from.fields),
        FieldsOnTableOneDb(l.to.table.tableName, l.to.fields),
        l.direction)))
    ))
  }

  def fileExist() : Boolean = FileReadWrite.fileExist(fileName)
}


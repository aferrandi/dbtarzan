package dbtarzan.db.foreignkeys

import dbtarzan.db.util.FileReadWrite
import dbtarzan.db.{AdditionalForeignKey, DatabaseId, FieldsOnTable, ForeignKey, ForeignKeys, ForeignKeysForTable, ForeignKeysForTableList, TableId}
import spray.json.DefaultJsonProtocol._
import spray.json._

import java.nio.file.Path
object TableJsonProtocol extends  DefaultJsonProtocol {
  implicit val databaseIdFormat: RootJsonFormat[DatabaseId] = jsonFormat(DatabaseId, "databaseName")
  implicit val tableIdFormat: RootJsonFormat[TableId] = jsonFormat(TableId, "databaseId", "tableName")
}

object ForeignKeysForTableJsonProtocol {
  import DefaultJsonProtocol._
  import TableJsonProtocol.tableIdFormat
  implicit val foreignKeyDirectionFormat: ForeignKeyDirectionFormat = new ForeignKeyDirectionFormat()
  implicit val fieldsOnTableFormat: RootJsonFormat[FieldsOnTable] = jsonFormat(FieldsOnTable, "table", "fields" )
  implicit val foreignKeyFormat: RootJsonFormat[ForeignKey] = jsonFormat(ForeignKey, "name", "from", "to", "direction")
  implicit val foreignKeysFormat: RootJsonFormat[ForeignKeys] = jsonFormat(ForeignKeys, "keys")
  implicit val foreignKeysForTableFormat: RootJsonFormat[ForeignKeysForTable] = jsonFormat(ForeignKeysForTable, "name", "keys")
  implicit val foreignKeysForTableListFormat: RootJsonFormat[ForeignKeysForTableList] = jsonFormat(ForeignKeysForTableList, "keys")
}
object AdditionalForeignKeysJsonProtocol {
  import ForeignKeysForTableJsonProtocol.fieldsOnTableFormat
  implicit val foreignKeyFormat: RootJsonFormat[AdditionalForeignKey] = jsonFormat(AdditionalForeignKey, "name", "from", "to")
}


case class AdditionalForeignKeyVer1(name: String, from : FieldsOnTableOneDb, to: FieldsOnTableOneDb)

object AdditionalForeignKeysVer1JsonProtocol {
  import ForeignKeysForTableJsonProtocolOneDb.fieldsOnTableFormatOneDb
  implicit val foreignKeyFormatVer1: RootJsonFormat[AdditionalForeignKeyVer1] = jsonFormat(AdditionalForeignKeyVer1, "name", "from", "to")
}


/* to write and read the additional foreign keys from a file. */
class AdditionalForeignKeysFile(dirPath: Path, databaseName : String) {
  import AdditionalForeignKeysJsonProtocol._
  import AdditionalForeignKeysVer1JsonProtocol._

  val fileName : Path = dirPath.resolve(databaseName+".fak")

	def writeAsFile(list : List[AdditionalForeignKey]) : Unit =
		FileReadWrite.writeFile(fileName, list.toJson.prettyPrint)
	
	def readFromFile(databaseId: DatabaseId) : List[AdditionalForeignKey] = {
		val text = FileReadWrite.readFile(fileName)
    try {
      text.parseJson.convertTo[List[AdditionalForeignKey]]
    } catch {
      case _: Throwable => {
        val keys = readVer1(databaseId, text)
        writeAsFile(keys)
        keys
      }
    }
	}

  private def readVer1(databaseId: DatabaseId, text: String): List[AdditionalForeignKey] = {
    text.parseJson.convertTo[List[AdditionalForeignKeyVer1]]
      .map(k => AdditionalForeignKey(k.name,
        FieldsOnTable(TableId(databaseId, k.from.table), k.from.fields),
        FieldsOnTable(TableId(databaseId, k.to.table), k.to.fields)
      ))
  }

  def fileExist() : Boolean = FileReadWrite.fileExist(fileName)
}


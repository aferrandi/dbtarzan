package dbtarzan.db.foreignkeys

import spray.json._
import dbtarzan.db.util.FileReadWrite
import dbtarzan.db.{ ForeignKeyDirection, DBEnumsText, FieldsOnTable, ForeignKey, ForeignKeys, ForeignKeysForTable, ForeignKeysForTableList }
import java.nio.file.Path

class ForeignKeyDirectionFormat extends RootJsonFormat[ForeignKeyDirection] {
  def write(direction: ForeignKeyDirection): JsValue = JsString(DBEnumsText.foreignKeyDirectionToText(direction))

  def read(json: JsValue): ForeignKeyDirection = json match {
    case JsString("STRAIGHT") => ForeignKeyDirection.STRAIGHT
    case JsString("TURNED") => ForeignKeyDirection.TURNED
    case _ => throw new DeserializationException("ForeignKeyDirection string expected")
  }
}

object ForeignKeysForTableJsonProtocol extends DefaultJsonProtocol {
  implicit val foreignKeyDirectionFormat: ForeignKeyDirectionFormat = new ForeignKeyDirectionFormat()
  implicit val fieldsOnTableFormat: RootJsonFormat[FieldsOnTable] = jsonFormat(FieldsOnTable, "table", "fields" )
  implicit val foreignKeyFormat: RootJsonFormat[ForeignKey] = jsonFormat(ForeignKey, "name", "from", "to", "direction")
  implicit val foreignKeysFormat: RootJsonFormat[ForeignKeys] = jsonFormat(ForeignKeys, "keys")
  implicit val foreignKeysForTableFormat: RootJsonFormat[ForeignKeysForTable] = jsonFormat(ForeignKeysForTable, "name", "keys")
  implicit val foreignKeysForTableListFormat: RootJsonFormat[ForeignKeysForTableList] = jsonFormat(ForeignKeysForTableList, "keys")
}


class ForeignKeysFile(dirPath: Path, databaseName : String) {
	import ForeignKeysForTableJsonProtocol._

  val fileName : Path = dirPath.resolve(databaseName+".fgk")

	def writeAsFile(list : ForeignKeysForTableList) : Unit =
		FileReadWrite.writeFile(fileName, list.toJson.prettyPrint)
	
	def readFromFile() : ForeignKeysForTableList = {
		val text = FileReadWrite.readFile(fileName)
		text.parseJson.convertTo[ForeignKeysForTableList]
	}

	def fileExist() : Boolean = FileReadWrite.fileExist(fileName)
}


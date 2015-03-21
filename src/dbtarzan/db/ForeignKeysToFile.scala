package dbtarzan.db

import spray.json._
import dbtarzan.db.util.FileReadWrite


object ForeignKeysForTableJsonProtocol extends DefaultJsonProtocol {
  import DefaultJsonProtocol._
  implicit val fieldsOnTableFormat = jsonFormat(FieldsOnTable, "table", "fields" )	
  implicit val foreignKeyFormat = jsonFormat(ForeignKey, "name", "from", "to" )
  implicit val foreignKeysFormat = jsonFormat(ForeignKeys, "keys")
  implicit val foreignKeysForTableFormat = jsonFormat(ForeignKeysForTable, "name", "keys")
  implicit val foreignKeysForTableListFormat = jsonFormat(ForeignKeysForTableList, "keys")
}

object ForeignKeysToFile {
	import ForeignKeysForTableJsonProtocol._
	private def fileName(databaseName : String) : String = databaseName+".fgk"

	def toFile(databaseName : String, list : ForeignKeysForTableList) : Unit =  
		FileReadWrite.writeFile(fileName(databaseName), list.toJson.prettyPrint)
	
	def fromFile(databaseName : String) : ForeignKeysForTableList = {
		val text = FileReadWrite.readFile(fileName(databaseName))
		text.parseJson.convertTo[ForeignKeysForTableList]
	}

}
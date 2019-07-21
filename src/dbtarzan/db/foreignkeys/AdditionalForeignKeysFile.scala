package dbtarzan.db.foreignkeys

import spray.json._
import dbtarzan.db.util.FileReadWrite
import dbtarzan.db.{ FieldsOnTable, AdditionalForeignKey }
import java.nio.file.{Path, Paths}

object AdditionalForeignKeysJsonProtocol extends DefaultJsonProtocol {
  implicit val fieldsOnTableFormat = jsonFormat(FieldsOnTable, "table", "fields" )	
  implicit val foreignKeyFormat = jsonFormat(AdditionalForeignKey, "name", "from", "to")
}


/* to write and read the additional foreign keys from a file. */
class AdditionalForeignKeysFile(databaseName : String) {
	import AdditionalForeignKeysJsonProtocol._

  val fileName : Path = Paths.get(databaseName+".fak")

	def toFile(list : List[AdditionalForeignKey]) : Unit =  
		FileReadWrite.writeFile(fileName, list.toJson.prettyPrint)
	
	def fromFile() : List[AdditionalForeignKey] = {
		val text = FileReadWrite.readFile(fileName)
		text.parseJson.convertTo[List[AdditionalForeignKey]]
	}

	def fileExist() : Boolean = FileReadWrite.fileExist(fileName)
}


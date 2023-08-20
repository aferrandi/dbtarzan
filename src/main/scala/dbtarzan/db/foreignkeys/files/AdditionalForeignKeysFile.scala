package dbtarzan.db.foreignkeys.files

import dbtarzan.db.*
import dbtarzan.db.foreignkeys.files.FieldsOnTableOneDb
import dbtarzan.db.util.FileReadWrite
import dbtarzan.localization.Language
import grapple.json.{*, given}

import java.nio.file.Path

/* to write and read the additional foreign keys from a file. */
class AdditionalForeignKeysFile(dirPath: Path, databaseName : String) {

  val fileName : Path = dirPath.resolve(databaseName+".fak")

  def writeAsFile(list : List[AdditionalForeignKey]) : Unit =
    FileReadWrite.writeFile(fileName, Json.toPrettyPrint(Json.toJson(list)))

  def readFromFile(databaseId: DatabaseId) : List[AdditionalForeignKey] = {
    val text = FileReadWrite.readFile(fileName)
    try
      Json.parse(text).as[List[AdditionalForeignKey]]
    catch
      case _: Throwable => {
        val keys = readVer1(databaseId, text)
        writeAsFile(keys)
        keys
    }
  }

  private def readVer1(databaseId: DatabaseId, text: String): List[AdditionalForeignKey] = {
    databaseId.origin match {
      case Left(simpleDatabaseId: SimpleDatabaseId) =>
        Json.parse(text).as[List[AdditionalForeignKeyVer1]]
          .map(k => AdditionalForeignKey(k.name,
            FieldsOnTable(TableId(databaseId, simpleDatabaseId, k.from.table), k.from.fields),
            FieldsOnTable(TableId(databaseId, simpleDatabaseId, k.to.table), k.to.fields)
          ))
      case _ => throw new NoSuchElementException("The database can only be simple, not a composite")
    }
  }

  def fileExist() : Boolean = FileReadWrite.fileExist(fileName)
}


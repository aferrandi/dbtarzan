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
    FileReadWrite.writeFile(fileName, AdditionalForeignKeysWriter.toText(list))

  def readFromFile(databaseId: DatabaseId) : List[AdditionalForeignKey] = {
    val text = FileReadWrite.readFile(fileName)
    try
      AdditionalForeignKeysReader.parsetText(text)
    catch
      case _: Throwable => {
        val keys = AdditionalForeignKeysReader.readVer1(databaseId, text)
        writeAsFile(keys)
        keys
    }
  }

  def fileExist() : Boolean = FileReadWrite.fileExist(fileName)
}


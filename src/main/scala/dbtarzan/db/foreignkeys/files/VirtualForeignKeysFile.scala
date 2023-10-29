package dbtarzan.db.foreignkeys.files

import dbtarzan.db.*
import dbtarzan.db.foreignkeys.files.FieldsOnTableOneDb
import dbtarzan.db.util.FileReadWrite
import dbtarzan.localization.Language
import grapple.json.{*, given}

import java.nio.file.Path

/* to write and read the virtual foreign keys from a file. */
class VirtualForeignKeysFile(dirPath: Path, databaseName : String) {

  val fileName : Path = dirPath.resolve(databaseName+".fak")

  def writeAsFile(list : List[VirtualalForeignKey]) : Unit =
    FileReadWrite.writeFile(fileName, VirtualForeignKeysWriter.toText(list))

  def readFromFile(databaseId: DatabaseId) : List[VirtualalForeignKey] = {
    val text = FileReadWrite.readFile(fileName)
    try
      VirtualForeignKeysReader.parseText(text)
    catch
      case _: Throwable => {
        val keys = VirtualForeignKeysReader.readVer1(databaseId, text)
        writeAsFile(keys)
        keys
    }
  }

  def fileExist() : Boolean = FileReadWrite.fileExist(fileName)
}


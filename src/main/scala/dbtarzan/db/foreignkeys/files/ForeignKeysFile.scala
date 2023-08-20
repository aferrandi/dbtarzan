package dbtarzan.db.foreignkeys.files

import dbtarzan.db.*
import dbtarzan.db.foreignkeys.*
import dbtarzan.db.util.FileReadWrite
import grapple.json.{*, given}

import java.nio.file.Path

class ForeignKeysFile(dirPath: Path, filename: String) {
  val fileName : Path = dirPath.resolve(filename+".fgk")

  def writeAsFile(list : List[ForeignKeysForTable]) : Unit = {
    val text = ForeignKeysWriter.toText(list)
    FileReadWrite.writeFile(fileName, text)
  }

  def readFromFile(databaseId: DatabaseId, simpleDatabaseId: SimpleDatabaseId) : List[ForeignKeysForTable] = {
    val text = FileReadWrite.readFile(fileName)
    val reader = ForeignKeysReader(databaseId, simpleDatabaseId)
    reader.parseText(text)
  }

  def fileExist() : Boolean = FileReadWrite.fileExist(fileName)
}


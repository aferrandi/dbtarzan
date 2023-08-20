package dbtarzan.db.foreignkeys.files

import dbtarzan.db.*
import dbtarzan.db.foreignkeys.*
import dbtarzan.db.util.FileReadWrite
import grapple.json.{*, given}

import java.nio.file.Path

class ForeignKeysFile(dirPath: Path, filename: String, databaseId: DatabaseId, simpleDatabaseId: SimpleDatabaseId) {
  val fileName : Path = dirPath.resolve(filename+".fgk")

  def writeAsFile(list : List[ForeignKeysForTable]) : Unit = {
    val keys = mapFromForeignKeys(list)
    FileReadWrite.writeFile(fileName, Json.toPrettyPrint(Json.toJson(keys)))
  }

  def readFromFile() : List[ForeignKeysForTable] = {
    val text = FileReadWrite.readFile(fileName)
    val keys = Json.parse(text).as[ForeignKeysForTableListOneDb].keys
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


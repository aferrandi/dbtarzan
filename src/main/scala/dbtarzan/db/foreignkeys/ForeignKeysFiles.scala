package dbtarzan.db.foreignkeys

import dbtarzan.db.*
import dbtarzan.db.util.FileReadWrite
import grapple.json.{ *, given }

import java.nio.file.Path

case class DeserializationException(msg: String, cause: Throwable = null) extends RuntimeException(msg, cause)

given JsonInput[ForeignKeyDirection] with
  def read(json: JsonValue): ForeignKeyDirection = json.as[JsonString].value match {
    case "STRAIGHT" => ForeignKeyDirection.STRAIGHT
    case "TURNED" => ForeignKeyDirection.TURNED
    case _ => throw DeserializationException("ForeignKeyDirection string expected")
  }

given JsonOutput[ForeignKeyDirection] with
  def write(u: ForeignKeyDirection): JsonValue = JsonString(u.toString)


case class FieldsOnTableOneDb(table : String, fields : List[String])
case class ForeignKeyOneDb(name: String, from : FieldsOnTableOneDb, to: FieldsOnTableOneDb, direction : ForeignKeyDirection)
case class ForeignKeysOneDb(keys : List[ForeignKeyOneDb])
case class ForeignKeysForTableOneDb(table : String, keys : ForeignKeysOneDb)
case class ForeignKeysForTableListOneDb(keys : List[ForeignKeysForTableOneDb])

given JsonInput[FieldsOnTableOneDb] with
  def read(json: JsonValue): FieldsOnTableOneDb = FieldsOnTableOneDb(json("table"), json("fields").as[List[String]])

given JsonOutput[FieldsOnTableOneDb] with
  def write(u: FieldsOnTableOneDb): JsonObject = Json.obj("table" -> u.table, "fields" -> u.fields)

given JsonInput[ForeignKeyOneDb] with
  def read(json: JsonValue): ForeignKeyOneDb = ForeignKeyOneDb(
    json("name"),
    json("from").as[FieldsOnTableOneDb],
    json("to").as[FieldsOnTableOneDb],
    json("direction").as[ForeignKeyDirection]
  )

given JsonOutput[ForeignKeyOneDb] with
  def write(u: ForeignKeyOneDb): JsonObject = Json.obj("name" -> u.name, "from" -> u.from, "to" -> u.to, "direction" -> u.direction)

given JsonInput[ForeignKeysOneDb] with
  def read(json: JsonValue): ForeignKeysOneDb = ForeignKeysOneDb(json("keys").as[List[ForeignKeyOneDb]])

given JsonOutput[ForeignKeysOneDb] with
  def write(u: ForeignKeysOneDb): JsonObject = Json.obj("keys" -> u.keys)

given JsonInput[ForeignKeysForTableOneDb] with
  def read(json: JsonValue): ForeignKeysForTableOneDb = ForeignKeysForTableOneDb(json("table"), json("keys").as[ForeignKeysOneDb])

given JsonOutput[ForeignKeysForTableOneDb] with
  def write(u: ForeignKeysForTableOneDb): JsonObject = Json.obj("table" -> u.table, "keys" -> u.keys)

given JsonInput[ForeignKeysForTableListOneDb] with
  def read(json: JsonValue): ForeignKeysForTableListOneDb = ForeignKeysForTableListOneDb(json("keys").as[List[ForeignKeysForTableOneDb]])

given JsonOutput[ForeignKeysForTableListOneDb] with
  def write(u: ForeignKeysForTableListOneDb): JsonObject = Json.obj("keys" -> u.keys)


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


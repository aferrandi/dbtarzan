package dbtarzan.db.foreignkeys

import dbtarzan.db.util.FileReadWrite
import dbtarzan.db.{AdditionalForeignKey, CompositeId, DatabaseId, FieldsOnTable, ForeignKey, ForeignKeyDirection, ForeignKeys, ForeignKeysForTable, ForeignKeysForTableList, SimpleDatabaseId, TableId}
import dbtarzan.localization.Language
import grapple.json.{*, given}

import java.nio.file.Path

given JsonInput[SimpleDatabaseId] with
  def read(json: JsonValue) = SimpleDatabaseId(json("databaseName"))

given JsonOutput[SimpleDatabaseId] with
  def write(u: SimpleDatabaseId) = Json.obj("databaseName" -> u.databaseName)

given JsonInput[CompositeId] with
  def read(json: JsonValue) = CompositeId(json("compositeName"))

given JsonOutput[CompositeId] with
  def write(u: CompositeId) = Json.obj("compositeName" -> u.compositeName)

given JsonInput[DatabaseId] with
  def read(json: JsonValue) = DatabaseId(json("origin"))

given JsonOutput[DatabaseId] with
  def write(u: DatabaseId) = Json.obj("origin" -> u.origin)

given JsonInput[TableId] with
  def read(json: JsonValue) = TableId(json("databaseId"), json("simpleDatabaseId"), json("tableName"))

given JsonOutput[TableId] with
  def write(u: TableId) = Json.obj("databaseId" -> u.databaseId, "simpleDatabaseId" -> u.simpleDatabaseId, "tableName" -> u.tableName)

given JsonInput[FieldsOnTable] with
  def read(json: JsonValue) = FieldsOnTable(json("table"), json("fields").as[List[String]])

given JsonOutput[FieldsOnTable] with
  def write(u: FieldsOnTable) = Json.obj("table" -> u.table, "fields" -> u.fields)

given JsonInput[ForeignKey] with
  def read(json: JsonValue) = ForeignKey(
    json("name"),
    json("from").as[FieldsOnTable],
    json("to").as[FieldsOnTable],
    json("direction").as[ForeignKeyDirection]
  )

given JsonOutput[ForeignKey] with
  def write(u: ForeignKey) = Json.obj("name" -> u.name, "from" -> u.from, "to" -> u.to, "direction" -> u.direction)

given JsonInput[ForeignKeys] with
  def read(json: JsonValue) = ForeignKeys(json("keys").as[List[ForeignKey]])

given JsonOutput[ForeignKeys] with
  def write(u: ForeignKeys) = Json.obj("keys" -> u.keys)

given JsonInput[ForeignKeysForTable] with
  def read(json: JsonValue) = ForeignKeysForTable(json("tableId"), json("keys").as[ForeignKeys])

given JsonOutput[ForeignKeysForTable] with
  def write(u: ForeignKeysForTable) = Json.obj("tableId" -> u.tableId, "keys" -> u.keys)

given JsonInput[AdditionalForeignKey] with
  def read(json: JsonValue) = AdditionalForeignKey(
    json("name"),
    json("from").as[FieldsOnTable],
    json("to").as[FieldsOnTable]
  )

given JsonOutput[AdditionalForeignKey] with
  def write(u: AdditionalForeignKey) = Json.obj("name" -> u.name, "from" -> u.from, "to" -> u.to)



case class AdditionalForeignKeyVer1(name: String, from : FieldsOnTableOneDb, to: FieldsOnTableOneDb)

given JsonInput[AdditionalForeignKeyVer1] with
  def read(json: JsonValue) = AdditionalForeignKeyVer1(
    json("name"),
    json("from").as[FieldsOnTableOneDb],
    json("to").as[FieldsOnTableOneDb]
  )

given JsonOutput[AdditionalForeignKeyVer1] with
  def write(u: AdditionalForeignKeyVer1) = Json.obj("name" -> u.name, "from" -> u.from, "to" -> u.to)


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


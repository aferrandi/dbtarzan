package dbtarzan.db.foreignkeys.files

import dbtarzan.db.{AdditionalForeignKey, CompositeId, DatabaseId, FieldsOnTable, ForeignKey, ForeignKeyDirection, ForeignKeys, ForeignKeysForTable, SimpleDatabaseId, TableId}
import grapple.json.{*, given}

given JsonInput[SimpleDatabaseId] with
  def read(json: JsonValue): SimpleDatabaseId = SimpleDatabaseId(json("databaseName"))

given JsonOutput[SimpleDatabaseId] with
  def write(u: SimpleDatabaseId): JsonObject = Json.obj("databaseName" -> u.databaseName)

given JsonInput[CompositeId] with
  def read(json: JsonValue): CompositeId = CompositeId(json("compositeName"))

given JsonOutput[CompositeId] with
  def write(u: CompositeId): JsonObject = Json.obj("compositeName" -> u.compositeName)

given JsonInput[DatabaseId] with
  def read(json: JsonValue): DatabaseId = DatabaseId(json("origin"))

given JsonOutput[DatabaseId] with
  def write(u: DatabaseId): JsonObject = Json.obj("origin" -> u.origin)

given JsonInput[TableId] with
  def read(json: JsonValue): TableId = TableId(json("databaseId"), json("simpleDatabaseId"), json("tableName"))

given JsonOutput[TableId] with
  def write(u: TableId): JsonObject = Json.obj("databaseId" -> u.databaseId, "simpleDatabaseId" -> u.simpleDatabaseId, "tableName" -> u.tableName)

given JsonInput[FieldsOnTable] with
  def read(json: JsonValue): FieldsOnTable = FieldsOnTable(json("table"), json("fields").as[List[String]])

given JsonOutput[FieldsOnTable] with
  def write(u: FieldsOnTable): JsonObject = Json.obj("table" -> u.table, "fields" -> u.fields)

given JsonInput[ForeignKey] with
  def read(json: JsonValue): ForeignKey = ForeignKey(
    json("name"),
    json("from").as[FieldsOnTable],
    json("to").as[FieldsOnTable],
    json("direction").as[ForeignKeyDirection]
  )

given JsonOutput[ForeignKey] with
  def write(u: ForeignKey): JsonObject = Json.obj("name" -> u.name, "from" -> u.from, "to" -> u.to, "direction" -> u.direction)

given JsonInput[ForeignKeys] with
  def read(json: JsonValue): ForeignKeys = ForeignKeys(json("keys").as[List[ForeignKey]])

given JsonOutput[ForeignKeys] with
  def write(u: ForeignKeys): JsonObject = Json.obj("keys" -> u.keys)

given JsonInput[ForeignKeysForTable] with
  def read(json: JsonValue): ForeignKeysForTable = ForeignKeysForTable(json("tableId"), json("keys").as[ForeignKeys])

given JsonOutput[ForeignKeysForTable] with
  def write(u: ForeignKeysForTable): JsonObject = Json.obj("tableId" -> u.tableId, "keys" -> u.keys)

given JsonInput[AdditionalForeignKey] with
  def read(json: JsonValue): AdditionalForeignKey = AdditionalForeignKey(
    json("name"),
    json("from").as[FieldsOnTable],
    json("to").as[FieldsOnTable]
  )

given JsonOutput[AdditionalForeignKey] with
  def write(u: AdditionalForeignKey): JsonObject = Json.obj("name" -> u.name, "from" -> u.from, "to" -> u.to)


given JsonInput[AdditionalForeignKeyVer1] with
  def read(json: JsonValue): AdditionalForeignKeyVer1 = AdditionalForeignKeyVer1(
    json("name"),
    json("from").as[FieldsOnTableOneDb],
    json("to").as[FieldsOnTableOneDb]
  )

given JsonOutput[AdditionalForeignKeyVer1] with
  def write(u: AdditionalForeignKeyVer1): JsonObject = Json.obj("name" -> u.name, "from" -> u.from, "to" -> u.to)


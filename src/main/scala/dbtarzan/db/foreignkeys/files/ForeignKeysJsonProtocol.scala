package dbtarzan.db.foreignkeys.files

import dbtarzan.db.ForeignKeyDirection
import dbtarzan.db.foreignkeys.*
import grapple.json.{*, given}
case class DeserializationException(msg: String, cause: Throwable = null) extends RuntimeException(msg, cause)

given JsonInput[ForeignKeyDirection] with
  def read(json: JsonValue): ForeignKeyDirection = json.as[JsonString].value match {
    case "STRAIGHT" => ForeignKeyDirection.STRAIGHT
    case "TURNED" => ForeignKeyDirection.TURNED
    case _ => throw DeserializationException("ForeignKeyDirection string expected")
  }

given JsonOutput[ForeignKeyDirection] with
  def write(u: ForeignKeyDirection): JsonValue = JsonString(u.toString)


given JsonInput[FieldsOnTableOneDb] with
  def read(json: JsonValue): FieldsOnTableOneDb = FieldsOnTableOneDb(
    json("table"),
    json("fields").as[List[String]]
  )

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
  def write(u: ForeignKeyOneDb): JsonObject = Json.obj(
    "name" -> u.name,
    "from" -> u.from,
    "to" -> u.to,
    "direction" -> u.direction
  )

given JsonInput[ForeignKeysOneDb] with
  def read(json: JsonValue): ForeignKeysOneDb = ForeignKeysOneDb(json("keys").as[List[ForeignKeyOneDb]])

given JsonOutput[ForeignKeysOneDb] with
  def write(u: ForeignKeysOneDb): JsonObject = Json.obj("keys" -> u.keys)

given JsonInput[ForeignKeysForTableOneDb] with
  def read(json: JsonValue): ForeignKeysForTableOneDb = ForeignKeysForTableOneDb(
    json.get("table") match {
      case Some(table) => table
      case _ => json("name") // backward compatibility
    },
    json("keys").as[ForeignKeysOneDb]
  )

given JsonOutput[ForeignKeysForTableOneDb] with
  def write(u: ForeignKeysForTableOneDb): JsonObject = Json.obj("table" -> u.table, "keys" -> u.keys)

given JsonInput[ForeignKeysForTableListOneDb] with
  def read(json: JsonValue): ForeignKeysForTableListOneDb = ForeignKeysForTableListOneDb(
    json("keys").as[List[ForeignKeysForTableOneDb]]
  )

given JsonOutput[ForeignKeysForTableListOneDb] with
  def write(u: ForeignKeysForTableListOneDb): JsonObject = Json.obj("keys" -> u.keys)

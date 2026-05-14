package dbtarzan.config.connections

import dbtarzan.db.{CompositeId, IdentifierDelimiters, MaxFieldSize, SchemaName}
import grapple.json.{JsonInput, JsonOutput, *, given}
import dbtarzan.config.password.{*, given}
import dbtarzan.config.connections.ConnectionData
import grapple.json

given JsonInput[IdentifierDelimiters] with
  def read(json: JsonValue): IdentifierDelimiters = {
    val jsonObj = json.as[JsonObject]
    IdentifierDelimiters(jsonObj("start").as[String].charAt(0), jsonObj("end").as[String].charAt(0))
  }

given JsonOutput[IdentifierDelimiters] with
  def write(u: IdentifierDelimiters): JsonObject = Json.obj("start" -> u.start.toString, "end" -> u.end.toString)

given JsonInput[MaxFieldSize] with
  def read(json: JsonValue): MaxFieldSize = {
    val jsonObj = json.as[JsonObject]
    MaxFieldSize(jsonObj("value").as[Int], jsonObj.readOption("leftSQLFunction"))
  }

given JsonOutput[MaxFieldSize] with
  def write(u: MaxFieldSize): JsonObject = Json.obj("value" -> u.value, "leftSQLFunction" -> u.leftSQLFunction)

given JsonInput[SchemaName] with
  def read(json: JsonValue): SchemaName = SchemaName(json.as[String])

given JsonOutput[SchemaName] with
  def write(u: SchemaName): JsonValue = JsonString(u.schema)

given JsonInput[ConnectionData] = {
  def readMaxFieldSizeOrInt(value: JsonValue): MaxFieldSize =
    eitherJsonInput[MaxFieldSize,Int].read(value).fold(identity, MaxFieldSize(_, None))
  json =>
    val jsonObj = json.as[JsonObject]
    ConnectionData(
      jsonObj.getString("jar"),
      jsonObj.getString("name"),
      jsonObj.getString("driver"),
      jsonObj.getString("url"),
      jsonObj.readOption[SchemaName]("schema"),
      jsonObj.getString("user"),
      jsonObj.readOption[Password]("password"),
      jsonObj.readOption[Int]("instances"),
      jsonObj.readOption[IdentifierDelimiters]("identifierDelimiters"),
      jsonObj.readOption[Int]("maxRows"),
      jsonObj.readOption[Int]("queryTimeoutInSeconds"),
      jsonObj.get("maxFieldSize").filter(JsonNull.!=).map(v => readMaxFieldSizeOrInt(v)),
      jsonObj.readOption[Int]("maxInClauseCount"),
      jsonObj.readOption[String]("catalog")
    )
}


given JsonOutput[ConnectionData] with
  def write(u: ConnectionData): JsonObject = Json.obj(
    "jar" -> u.jar,
    "name" -> u.name,
    "driver" -> u.driver,
    "url" -> u.url,
    "schema" -> u.schema,
    "user" -> u.user,
    "password" -> u.password,
    "instances" -> u.instances,
    "identifierDelimiters" -> u.identifierDelimiters,
    "maxRows" -> u.maxRows,
    "queryTimeoutInSeconds" -> u.queryTimeoutInSeconds,
    "maxFieldSize" -> u.maxFieldSize,
    "maxInClauseCount" -> u.maxInClauseCount,
    "catalog" -> u.catalog
  )


package dbtarzan.config.connections

import dbtarzan.db.{CompositeId, IdentifierDelimiters, SchemaName}
import grapple.json.{*, given}
import dbtarzan.config.password.{*, given}
import dbtarzan.config.connections.ConnectionData

given JsonInput[IdentifierDelimiters] with
  def read(json: JsonValue): IdentifierDelimiters = IdentifierDelimiters(json("start").as[String].charAt(0), json("end").as[String].charAt(0))

given JsonOutput[IdentifierDelimiters] with
  def write(u: IdentifierDelimiters): JsonObject = Json.obj("start" -> u.start.toString, "end" -> u.end.toString)

given JsonInput[SchemaName] with
  def read(json: JsonValue): SchemaName = SchemaName(json.as[String])

given JsonOutput[SchemaName] with
  def write(u: SchemaName): JsonValue = JsonString(u.schema)

given JsonInput[ConnectionData] =
  json =>
    ConnectionData(
      json.getString("jar"),
      json.getString("name"),
      json.getString("driver"),
      json.getString("url"),
      json.readOption[SchemaName]("schema"),
      json.getString("user"),
      json.readOption[Password]("password"),
      json.readOption[Int]("instances"),
      json.readOption[IdentifierDelimiters]("identifierDelimiters"),
      json.readOption[Int]("maxRows"),
      json.readOption[Int]("queryTimeoutInSeconds"),
      json.readOption[Int]("maxFieldSize"),
      json.readOption[String]("catalog")
    )


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
    "catalog" -> u.catalog
  )


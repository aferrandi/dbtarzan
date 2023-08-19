package dbtarzan.config.connections

import dbtarzan.db.{CompositeId, IdentifierDelimiters, SchemaName}
import grapple.json.{*, given}
import dbtarzan.config.password.*
import dbtarzan.config.connections.ConnectionData

given JsonInput[IdentifierDelimiters] with
  def read(json: JsonValue) = IdentifierDelimiters(json("start").as[String].charAt(0), json("end").as[String].charAt(0))

given JsonOutput[IdentifierDelimiters] with
  def write(u: IdentifierDelimiters) = Json.obj("start" -> u.start.toString, "end" -> u.end.toString)

given JsonInput[SchemaName] with
  def read(json: JsonValue) = SchemaName(json.as[String])

given JsonOutput[SchemaName] with
  def write(u: SchemaName) = JsonString(u.schema)

given JsonInput[ConnectionData] with
  def read(json: JsonValue) = ConnectionData(
    json("jar"),
    json("name"),
    json("driver"),
    json("url"),
    json.map[SchemaName]("schema"),
    json("user"),
    PasswordJsonInput.read(json("password")),
    json.map[Boolean]("passwordEncrypted"),
    json.map[Int]("instances"),
    json.map[IdentifierDelimiters]("identifierDelimiters"),
    json.map[Int]("maxRows"),
    json.map[Int]("queryTimeoutInSeconds"),
    json.map[Int]("maxFieldSize"),
    json.map[String]("catalog")
  )

given JsonOutput[ConnectionData] with
  def write(u: ConnectionData) = Json.obj(
    "jar" -> u.jar,
    "name" -> u.name,
    "driver" -> u.driver,
    "url" -> u.url,
    "schema" -> u.schema,
    "user" -> u.user,
    "password" -> PasswordJsonOutput.write(u.password),
    "passwordEncrypted" -> u.passwordEncrypted,
    "instances" -> u.instances,
    "identifierDelimiters" -> u.identifierDelimiters,
    "maxRows" -> u.maxRows,
    "queryTimeoutInSeconds" -> u.queryTimeoutInSeconds,
    "maxFieldSize" -> u.maxFieldSize,
    "catalog" -> u.catalog
  )


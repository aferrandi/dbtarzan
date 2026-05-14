package dbtarzan.config.composite

import dbtarzan.db.{Composite, CompositeId, SimpleDatabaseId}
import scala.language.implicitConversions

import grapple.json.{ *, given }

given JsonInput[CompositeId] with
  def read(json: JsonValue): CompositeId = CompositeId(json.as[JsonObject]("compositeName"))

given JsonOutput[CompositeId] with
  def write(u: CompositeId): JsonObject = Json.obj("compositeName" -> u.compositeName)

given JsonInput[SimpleDatabaseId] with
  def read(json: JsonValue): SimpleDatabaseId = SimpleDatabaseId(json.as[JsonObject]("databaseName"))

given JsonOutput[SimpleDatabaseId] with
  def write(u: SimpleDatabaseId): JsonObject = Json.obj("databaseName" -> u.databaseName)

given JsonInput[Composite] with
  def read(json: JsonValue): Composite = {
    val jsonObj = json.as[JsonObject]
    Composite(
      jsonObj("compositeId"),
      jsonObj("databaseIds").as[List[SimpleDatabaseId]],
      jsonObj.readOption[Boolean]("showAlsoIndividualDatabases").getOrElse(true)
    )
  }

given JsonOutput[Composite] with
  def write(u: Composite): JsonObject = Json.obj(
    "compositeId" -> u.compositeId,
    "databaseIds" -> u.databaseIds,
    "showAlsoIndividualDatabases" -> u.showAlsoIndividualDatabases
  )


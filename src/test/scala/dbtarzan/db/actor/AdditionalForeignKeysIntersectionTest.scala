package dbtarzan.db.actor

import dbtarzan.db._
import dbtarzan.testutil.TestDatabaseIds
import org.scalatest.flatspec.AnyFlatSpec

class AdditionalForeignKeysIntersectionTest extends AnyFlatSpec {
  def albumTableId = TestDatabaseIds.simpleTableId("Album")
  def artistTableId = TestDatabaseIds.simpleTableId("Artist")
  def customerTableId = TestDatabaseIds.simpleTableId("Customer")
  def foreignKeysByTable = Map(
    artistTableId -> ForeignKeys(List(
          ForeignKey("key1",  FieldsOnTable(albumTableId, List("ArtistId")), FieldsOnTable(artistTableId, List("ArtistId")), ForeignKeyDirection.STRAIGHT)
          ))
      )

  "the intersection with an empty additional key list" should "give an empty list" in {
    val intersection = AdditionalForeignKeysIntersection.intersection(foreignKeysByTable, List.empty)
    assert(intersection.isEmpty)
  }

  "the intersection with a non matching additional key list" should "give an empty list" in {
    val additionalKeys = List(
      AdditionalForeignKey("add1", FieldsOnTable(albumTableId, List("CustomerId")), FieldsOnTable(customerTableId, List("CustomerId")))
      )
    val intersection = AdditionalForeignKeysIntersection.intersection(foreignKeysByTable, additionalKeys)
    assert(intersection.isEmpty)
  }

  "the intersection with a matching additional key list from=from a d to=to" should "give the matcihing key" in {
    val additionalKeys = List(
      AdditionalForeignKey("add1", FieldsOnTable(albumTableId, List("ArtistId")), FieldsOnTable(artistTableId, List("ArtistId")))
      )
    val intersection = AdditionalForeignKeysIntersection.intersection(foreignKeysByTable, additionalKeys)
    assert(intersection === List("add1"))
  }

  "the intersection with a matching additional key list from=to a d to=from" should "give the matcihing key" in {
    val additionalKeys = List(
      AdditionalForeignKey("add1", FieldsOnTable(artistTableId, List("ArtistId")), FieldsOnTable(albumTableId, List("ArtistId")))
      )
    val intersection = AdditionalForeignKeysIntersection.intersection(foreignKeysByTable, additionalKeys)
    assert(intersection === List("add1"))
  }
}
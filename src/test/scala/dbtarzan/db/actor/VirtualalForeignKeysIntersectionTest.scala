package dbtarzan.db.actor

import dbtarzan.db._
import dbtarzan.testutil.TestDatabaseIds
import org.scalatest.flatspec.AnyFlatSpec

class VirtualalForeignKeysIntersectionTest extends AnyFlatSpec {
  def albumTableId: TableId = TestDatabaseIds.simpleTableId("Album")
  def artistTableId: TableId = TestDatabaseIds.simpleTableId("Artist")
  def customerTableId: TableId = TestDatabaseIds.simpleTableId("Customer")
  def foreignKeysByTable: Map[TableId,ForeignKeys] = Map(
    artistTableId -> ForeignKeys(List(
          ForeignKey("key1",  FieldsOnTable(albumTableId, List("ArtistId")), FieldsOnTable(artistTableId, List("ArtistId")), ForeignKeyDirection.STRAIGHT)
          ))
      )

  "the intersection with an empty virtual key list" should "give an empty list" in {
    val intersection = VirtualForeignKeysIntersection.intersection(foreignKeysByTable, List.empty)
    assert(intersection.isEmpty)
  }

  "the intersection with a non matching virtual key list" should "give an empty list" in {
    val virtualKeys = List(
      VirtualalForeignKey("add1", FieldsOnTable(albumTableId, List("CustomerId")), FieldsOnTable(customerTableId, List("CustomerId")))
      )
    val intersection = VirtualForeignKeysIntersection.intersection(foreignKeysByTable, virtualKeys)
    assert(intersection.isEmpty)
  }

  "the intersection with a matching virtual key list from=from a d to=to" should "give the matcihing key" in {
    val virtualKeys = List(
      VirtualalForeignKey("add1", FieldsOnTable(albumTableId, List("ArtistId")), FieldsOnTable(artistTableId, List("ArtistId")))
      )
    val intersection = VirtualForeignKeysIntersection.intersection(foreignKeysByTable, virtualKeys)
    assert(intersection === List("add1"))
  }

  "the intersection with a matching virtual key list from=to a d to=from" should "give the matcihing key" in {
    val virtualKeys = List(
      VirtualalForeignKey("add1", FieldsOnTable(artistTableId, List("ArtistId")), FieldsOnTable(albumTableId, List("ArtistId")))
      )
    val intersection = VirtualForeignKeysIntersection.intersection(foreignKeysByTable, virtualKeys)
    assert(intersection === List("add1"))
  }
}
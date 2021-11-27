package dbtarzan.db.actor

import dbtarzan.db._
import org.scalatest.flatspec.AnyFlatSpec

class AdditionalForeignKeysIntersectionTest extends AnyFlatSpec {
  def foreignKeysByTable = Map(
      "Artist" -> ForeignKeys(List(
          ForeignKey("key1",  FieldsOnTable("Album", List("ArtistId")), FieldsOnTable("Artist", List("ArtistId")), ForeignKeyDirection.STRAIGHT)
          ))
      )

  "the intersection with an empty additional key list" should "give an empty list" in {
    val intersection = AdditionalForeignKeysIntersection.intersection(foreignKeysByTable, List.empty)
    assert(intersection.isEmpty)
  }

  "the intersection with a non matching additional key list" should "give an empty list" in {
    val additionalKeys = List(
      AdditionalForeignKey("add1", FieldsOnTable("Album", List("CustomerId")), FieldsOnTable("Customer", List("CustomerId")))
      )
    val intersection = AdditionalForeignKeysIntersection.intersection(foreignKeysByTable, additionalKeys)
    assert(intersection.isEmpty)
  }

  "the intersection with a matching additional key list from=from a d to=to" should "give the matcihing key" in {
    val additionalKeys = List(
      AdditionalForeignKey("add1", FieldsOnTable("Album", List("ArtistId")), FieldsOnTable("Artist", List("ArtistId")))
      )
    val intersection = AdditionalForeignKeysIntersection.intersection(foreignKeysByTable, additionalKeys)
    assert(intersection === List("add1"))
  }

  "the intersection with a matching additional key list from=to a d to=from" should "give the matcihing key" in {
    val additionalKeys = List(
      AdditionalForeignKey("add1", FieldsOnTable("Artist", List("ArtistId")), FieldsOnTable("Album", List("ArtistId")))
      )
    val intersection = AdditionalForeignKeysIntersection.intersection(foreignKeysByTable, additionalKeys)
    assert(intersection === List("add1"))
  }
}
package dbtarzan.db.actor

import dbtarzan.db.*
import org.scalatest.flatspec.AnyFlatSpec

class ForeignKeysByPatternTest extends AnyFlatSpec {

  "filterForeignKeysByPattern" should "give the key matching the pattern" in {
    assert(List("customerEmployee") === keysAgainstPattern("Cust"))
  }

  "filterForeignKeysByPattern" should "give another key matching the pattern" in {
    assert(List("FLIGHTS") === keysAgainstPattern("seg"))
  }

  "filterForeignKeysByPattern" should "both keys matching the pattern" in {
    assert(List("customerEmployee", "FLIGHTS") === keysAgainstPattern("id"))
  }

  "filterForeignKeysByPattern" should "no key because nothing matching the pattern" in {
    assert(keysAgainstPattern("avaiiab").isEmpty)
  }

  "filterForeignKeysByPattern" should "give the key matching the pattern at the end of the key field" in {
    assert(List("FLIGHTS") === keysAgainstPattern("BER"))
  }


  private def keysAgainstPattern(pattern: String): List[String] = {
    val simpleId = SimpleDatabaseId("db")
    val dbid = DatabaseId(Left(simpleId))
    val key1 = ForeignKey("customerEmployee",
      FieldsOnTable(TableId(dbid, simpleId, "Customer"), List("employeeid")),
      FieldsOnTable(TableId(dbid, simpleId, "Employee"), List("id")),
      ForeignKeyDirection.STRAIGHT
    )
    val key2 = ForeignKey("FLIGHTS",
      FieldsOnTable(TableId(dbid, simpleId, "FLIGHTAVAILABILIY"), List("FLIGHT_ID", "SEG")),
      FieldsOnTable(TableId(dbid, simpleId, "FLIGHTS"), List("FLIGHT_ID", "SEGMENT_NUMBER")),
      ForeignKeyDirection.STRAIGHT
    )
    ForeignKeysByPattern.filterForeignKeysByPattern(List(key1, key2), pattern).map(key => key.name)
  }
}

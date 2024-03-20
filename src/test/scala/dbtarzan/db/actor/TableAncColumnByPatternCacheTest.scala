package dbtarzan.db.actor

import dbtarzan.db.basicmetadata.ColumnWithTable
import org.scalatest.flatspec.AnyFlatSpec

class TableAncColumnByPatternCacheTest extends AnyFlatSpec {
  "extractTablesMatchingPattern" should "give a sorted list of the table names with the columns containing the patteren" in {
    val tableNames = buildCache().extractTablesMatchingPattern("NAME")
    assert(List("Artist", "Employee", "PlayList") === tableNames.names)
  }

  "tablesByPattern" should "give a sorted list of the only table name having a column with that name" in {
    val tableNames = buildCache().extractTablesMatchingPattern("firstNAME")
    assert(List("Employee") === tableNames.names)
  }

  "tablesByPattern" should "give a sorted list of the tables containing the pattern" in {
    val tableNames = buildCache().extractTablesMatchingPattern("ist")
    assert(List("Artist", "PlayList") === tableNames.names)
  }

  "tablesByPattern" should "give an empty sorted list when the pattern is not found" in {
    val tableNames = buildCache().extractTablesMatchingPattern("dog")
    assert(tableNames.names.isEmpty)
  }

  "tablesByPattern" should "give an empty sorted list when the cache is empty" in {
    val tableNames = new TableAncColumnByPatternCache(List.empty).extractTablesMatchingPattern("dog")
    assert(tableNames.names.isEmpty)
  }


  private def buildCache() = {
    val metadataLoader = new TableAncColumnByPatternCache(
      List(
        ColumnWithTable("FirstName", "Employee"),
        ColumnWithTable("LastName", "Employee"),
        ColumnWithTable("Quantity", "InvoiceLine"),
        ColumnWithTable("UnitPrice", "InvoiceLine"),
        ColumnWithTable("Name", "PlayList"),
        ColumnWithTable("Name", "Artist")
      )
    )
    metadataLoader
  }
}

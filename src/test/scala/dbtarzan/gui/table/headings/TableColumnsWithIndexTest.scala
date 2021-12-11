package dbtarzan.gui.table.headings

import org.scalatest.flatspec.AnyFlatSpec

class TableColumnsWithIndexTest extends AnyFlatSpec {
  "columns indexes" should "be in the order names was given" in {
    val columnsIWithIndex = new TableColumnsWithIndex(List("TerritoryID", "EmployeeID"))
    assert(columnsIWithIndex.indexOf("territoryid") === 0)
    assert(columnsIWithIndex.indexOf("employeeid") === 1)
  }

  "names" should "found with keys" in {
    val columnsIWithIndex = new TableColumnsWithIndex(List("TerritoryID", "EmployeeID"))
    assert(columnsIWithIndex.nameOf("territoryid") === "TerritoryID")
    assert(columnsIWithIndex.nameOf("employeeid") === "EmployeeID")
  }

  "keys" should "be lowercase of names" in {
    val columnsIWithIndex = new TableColumnsWithIndex(List("TerritoryID", "EmployeeID"))
    assert(columnsIWithIndex.keys() === List("territoryid", "employeeid"))
  }
}

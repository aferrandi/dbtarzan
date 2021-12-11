package dbtarzan.gui.table.headings

import org.scalatest.flatspec.AnyFlatSpec

import scala.collection.immutable.BitSet

class TableColumnsAttributesTest extends AnyFlatSpec {
  "columns attributes" should "show the correct values after updated" in {
    val columnsAttributes = new TableColumnsAttributes(List("supplierid", "companyname", "contactname", "contacttitle", "address", "city", "region", "postalcode", "country", "phone", "fax", "homepage"))
    val primaryKeys = columnsAttributes.addKeys(List("supplierid"), TableColumnsStates.PRIMARYKEY_STATE)
    assert(primaryKeys === List(HeadingText("supplierid", BitSet(1))))
    val foreignKeys = columnsAttributes.addKeys(List("supplierid", "city"), TableColumnsStates.FOREIGNKEY_STATE)
    assert(foreignKeys === List(HeadingText("supplierid", BitSet(1, 2)), HeadingText("city", BitSet(2))))
  }
}

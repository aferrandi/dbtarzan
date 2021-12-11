package dbtarzan.gui.tabletabs

import dbtarzan.db.{DBTableStructure, Fields, Filter, QueryAttributes, TableDescription}
import org.scalatest.flatspec.AnyFlatSpec

class TableStructureTextTest extends AnyFlatSpec {
  "simple structure" should "result in a text with only the name" in {
    val structure = DBTableStructure.build(TableDescription("structureName", None, None), Fields(List.empty), QueryAttributes.none())
	  assert(TableStructureText.buildTabText(structure) === "structureName")
  }

  "structure with origin" should "result in a text with an arrow" in {
    val structure = DBTableStructure.build(TableDescription("structureName", Some("originName"), None), Fields(List.empty), QueryAttributes.none())
    assert(TableStructureText.buildTabText(structure) === "structureName<originName")
  }

  "structure with filter" should "result in a text with a star" in {
    val structure = DBTableStructure(TableDescription("structureName", None, None), Fields(List.empty), None, Some(Filter("where field > 10")), None, QueryAttributes.none())
    assert(TableStructureText.buildTabText(structure) === "structureName *")
  }
}
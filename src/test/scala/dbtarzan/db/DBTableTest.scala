package dbtarzan.db

import org.scalatest.flatspec.AnyFlatSpec

class DBTableTest extends AnyFlatSpec {
  "tablenames" should "give a sorted list of the table names" in {
    val structure = DBTableStructure(
      DBTableStructureBuilder.buildDescription(),
      Fields(DBTableStructureBuilder.buildColumns()),
      Some(DBTableStructureBuilder.buildForeignKeyCriteria()),
      None,
      None,
      QueryAttributes.none()
    )
    val dbTable = new DBTable(structure)
    assert(dbTable.tableDescription.name === "customer")
    assert(dbTable.fields.size === 2)
    assert(dbTable.hasOrderBy === false)
    val structureWithOrderBy = dbTable.withOrderByFields(DBTableStructureBuilder.buildOrderByFields())
    assert(structureWithOrderBy.genericFilter.isEmpty)
    val dbTableWithOrderBy = DBTable(structureWithOrderBy)
    assert(dbTableWithOrderBy.hasOrderBy === true)
    assert(dbTableWithOrderBy.orderBys.get.fields.size === 2)
    val structureWitFilter = dbTableWithOrderBy.withAdditionalFilter(Filter("name = 'Max'"))
    assert(structureWitFilter.genericFilter.isDefined)
  }
}

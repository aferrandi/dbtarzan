package dbtarzan.db

import dbtarzan.db.foreignkeys.{FKRow, ForeignKeyCriteria}
import dbtarzan.db.sql.SqlBuilder
import dbtarzan.testutil.TestDatabaseIds
import org.scalatest.flatspec.AnyFlatSpec

class SqlBuilderTest extends AnyFlatSpec {
  "a simple table" should "give a simple query" in {
    val structure = DBTableStructure(
        DBTableStructureBuilder.buildDescription(),
        sampleFields(),
        None,
        None,
        None,
        QueryAttributes.none()
      )
    val sql = SqlBuilder.buildQuerySql(structure, None)
    assert("SELECT id, name, age FROM customer" === sql.sql)
  }

  "a simple table with delimiters" should "give a query with delimiters" in {
    val structure = DBTableStructure(
        DBTableStructureBuilder.buildDescription(),
        sampleFields(),
        None,
        None,
        None,
        DBTableStructureBuilder.buildAttributes()
        )
    val sql = SqlBuilder.buildQuerySql(structure, None)
    assert("SELECT id, name, age FROM [TST].[customer]" === sql.sql)
  }

  "a table with foreign criteria" should "give a query with a where clause" in {
    val structure = DBTableStructure(
        DBTableStructureBuilder.buildDescription(),
        sampleFields(),
        Some(DBTableStructureBuilder.buildForeignKeyCriteria()),
        None,
        None,
        QueryAttributes.none()
      )
    val sql = SqlBuilder.buildQuerySql(structure, None)
    assert("SELECT id, name, age FROM customer WHERE (\n(name='John' AND age=23))" === sql.sql)
  }

  "a table with additional filter" should "give a query with a where clause" in {
    val structure = DBTableStructure(
        DBTableStructureBuilder.buildDescription(),
        sampleFields(),
        None,
        Some(Filter("name = 'john'")),
        None,
        QueryAttributes.none()
      )
    val sql = SqlBuilder.buildQuerySql(structure, None)
    assert("SELECT id, name, age FROM customer WHERE (\nname = 'john')" === sql.sql)
  }

  "a table with additional order by columns" should "give a query with an order by clause" in {
    val structure = DBTableStructure(
        DBTableStructureBuilder.buildDescription(),
        sampleFields(),
        None,
        None,
        Some(DBTableStructureBuilder.buildOrderByFields()),
        QueryAttributes.none()
    )
    val sql = SqlBuilder.buildQuerySql(structure, None)
    assert("SELECT id, name, age FROM customer ORDER BY name ASC, age DESC" === sql.sql)
  }

  "a table with empty order by columns list" should "give a simple query" in {
    val structure = DBTableStructure(
        DBTableStructureBuilder.buildDescription(),
        sampleFields(),
        None,
        None,
        Some(OrderByFields(List.empty[OrderByField])),
        QueryAttributes.none()
      )
    val sql = SqlBuilder.buildQuerySql(structure, None)
    assert("SELECT id, name, age FROM customer" === sql.sql)
  }

  private def sampleFields(): Fields =
    Fields(List(Field("id", FieldType.INT, "INT"), Field("name", FieldType.STRING, "VARCHAR"), Field("age", FieldType.INT, "INT")))

  "a row structure with no attributes" should "give a simple query" in {
    val structure = DBRowStructure(
      DBTableStructureBuilder.tableName,
      Fields(DBTableStructureBuilder.buildColumns()),
      DBTableStructureBuilder.buildFields("John", "23"),
      QueryAttributes.none()
    )
    val sql = SqlBuilder.buildSingleRowSql(structure)
    assert("SELECT * FROM customer WHERE (\nname='John') AND (\nage=23)" === sql.sql)
  }

  "a row structure with attributes" should "give a simple query" in {
    val structure = DBRowStructure(
      DBTableStructureBuilder.tableName,
      Fields(DBTableStructureBuilder.buildColumns()),
      DBTableStructureBuilder.buildFields("John", "23"),
      DBTableStructureBuilder.buildAttributes()
    )
    val sql = SqlBuilder.buildSingleRowSql(structure)
    assert("SELECT * FROM [TST].[customer] WHERE (\n[name]='John') AND (\n[age]=23)" === sql.sql)
  }
}
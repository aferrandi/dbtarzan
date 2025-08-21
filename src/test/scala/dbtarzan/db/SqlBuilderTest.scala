package dbtarzan.db

import dbtarzan.db.sql.SqlBuilder
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


    "a simple table with a left sql function" should "give a query that uses the function for the strings" in {
      val structure = DBTableStructure(
        DBTableStructureBuilder.buildDescription(),
        sampleFields(),
        None,
        None,
        None,
        QueryAttributes.none()
      )
      val sql = SqlBuilder.buildQuerySql(structure, Some(MaxFieldSize(200, Some("LEFT($column, $max)"))))
      assert("SELECT id, LEFT(name, 200), age FROM customer" === sql.sql)
    }

  "a simple table with a substr sql function" should "give a query that uses the function for the strings" in {
    val structure = DBTableStructure(
      DBTableStructureBuilder.buildDescription(),
      sampleFields(),
      None,
      None,
      None,
      QueryAttributes.none()
    )
    val sql = SqlBuilder.buildQuerySql(structure, Some(MaxFieldSize(200, Some("SUBSTR($column, 1, $max)"))))
    assert("SELECT id, SUBSTR(name, 1, 200), age FROM customer" === sql.sql)
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
    Fields(List(Field("id", FieldType.INT, "INT", None), Field("name", FieldType.STRING, "VARCHAR(255)", Some(255)), Field("age", FieldType.INT, "INT", None)))

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
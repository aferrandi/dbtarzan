package dbtarzan.db

import dbtarzan.db.foreignkeys.{FKRow, ForeignKeyCriteria}
import dbtarzan.testutil.TestDatabaseIds
import org.scalatest.flatspec.AnyFlatSpec

class SqlBuilderTest extends AnyFlatSpec {
  "a simple table" should "give a simple query" in {
    val structure = DBTableStructure(
        buildDescription(),
        noFields(),
        None,
        None,
        None,
        QueryAttributes.none()
      )
    val sql = SqlBuilder.buildSql(structure)
  	assert("SELECT * FROM customer" === sql.sql)
  }

  "a simple table with delimiters" should "give a query with delimiters" in {
    val structure = DBTableStructure(
        buildDescription(),
        noFields(),
        None,
        None,
        None,
        buildAttributes()
        )
    val sql = SqlBuilder.buildSql(structure)
    assert("SELECT * FROM [TST].[customer]" === sql.sql)
  }

  "a table with foreign criteria" should "give a query with a where clause" in {
    val structure = DBTableStructure(
        buildDescription(),
        noFields(),
        Some(buildForeignKeyCriteria()),
        None,
        None,
        QueryAttributes.none()
      )
    val sql = SqlBuilder.buildSql(structure)
    assert("SELECT * FROM customer WHERE (\n(name='John' AND age=23))" === sql.sql)
  }

  "a table with additional filter" should "give a query with a where clause" in {
    val structure = DBTableStructure(
        buildDescription(),
        noFields(),
        None,
        Some(Filter("name = 'john'")),
        None,
        QueryAttributes.none()
      )
    val sql = SqlBuilder.buildSql(structure)
    assert("SELECT * FROM customer WHERE (\nname = 'john')" === sql.sql)
  }

  "a table with additional order by columns" should "give a query with an order by clause" in {
    val structure = DBTableStructure(
        buildDescription(),
        noFields(),
        None,
        None,
        Some(OrderByFields(List(
          OrderByField(buildNameColumn(), OrderByDirection.ASC),
          OrderByField(buildAgeColumn(), OrderByDirection.DESC)
          ))),
        QueryAttributes.none()
    )
    val sql = SqlBuilder.buildSql(structure)
    assert("SELECT * FROM customer ORDER BY name ASC, age DESC" === sql.sql)
  }

  "a table with empty order by columns list" should "give a simple query" in {
    val structure = DBTableStructure(
        buildDescription(),
        noFields(),
        None,
        None,
        Some(OrderByFields(List.empty[OrderByField])),
        QueryAttributes.none()
      )
    val sql = SqlBuilder.buildSql(structure)
    assert("SELECT * FROM customer" === sql.sql)
  }

  "a row structure with no attributes" should "give a simple query" in {
    val structure = DBRowStructure(
      tableName,
      Fields(buildColumns()),
      buildFields("John", "23"),
      QueryAttributes.none()
    )
    val sql = SqlBuilder.buildSql(structure)
    assert("SELECT * FROM customer WHERE (\nname='John') AND (\nage=23)" === sql.sql)
  }

  "a row structure with attributes" should "give a simple query" in {
    val structure = DBRowStructure(
      tableName,
      Fields(buildColumns()),
      buildFields("John", "23"),
      buildAttributes()
    )
    val sql = SqlBuilder.buildSql(structure)
    assert("SELECT * FROM [TST].[customer] WHERE (\n[name]='John') AND (\n[age]=23)" === sql.sql)
  }


  private def buildForeignKeyCriteria() = {
    val rows = List(buildRow("John", "23"))
    ForeignKeyCriteria(rows, buildColumns())
  }

  private def buildNameColumn() = Field("name",  FieldType.STRING, "")
  private def buildAgeColumn() = Field("age",  FieldType.INT, "")

  private def buildColumns() = 
    List(
      buildNameColumn(),
      buildAgeColumn()
      )

  private def buildDescription() =
    TableDescription(tableName, None, None)

  private def tableName = {
    "customer"
  }

  private def noFields() = Fields(List())

  private def buildRow(name : String, age: String) = FKRow(buildFields(name, age))

  private def buildAttributes() = {
    QueryAttributes(Some(IdentifierDelimitersValues.squareBrackets), DBDefinition(Some(SchemaId(TestDatabaseIds.databaseId, TestDatabaseIds.simpleDatabaseId, SchemaName("TST"))), None), None)
  }

  private def buildFields(name: String, age: String) = {
    List(
      FieldWithValue("name", name),
      FieldWithValue("age", age)
    )
  }
}
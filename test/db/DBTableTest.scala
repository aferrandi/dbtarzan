package dbtarzan.db

import org.scalatest.FlatSpec

class DBTableTest extends FlatSpec {
  "a simple table" should "give a simple query" in {
    val table = DBTable.build(
        buildDescription(),
        Fields(buildColumns()),
        None,
        None,
        None,
        noneApplier()
      )
    val sql = table.buildSql()
  	assert("SELECT * FROM customer" === sql)
  }

  "a simple table with delimiters" should "give a query with delimiters" in {
      val applier = QueryAttributesApplier.from(QueryAttributes(Some(IdentifierDelimiters('[', ']')), Some("TST")))
    val table = DBTable.build(
        buildDescription(),
        Fields(buildColumns()),
        None,
        None,
        None,
        applier
      )
    val sql = table.buildSql()
    assert("SELECT * FROM [TST].[customer]" === sql)
  }

  "a table with foreign criteria" should "give a query with a where clause" in {
    val table = DBTable.build(
        buildDescription(),
        Fields(buildColumns()),
        Some(buildForeignKeyCriteria()),
        None,
        None,
        noneApplier()
      )
    val sql = table.buildSql()
    assert("SELECT * FROM customer WHERE (\n(name='John' AND age=23))" === sql)
  }

  "a table with additional filter" should "give a query with a where clause" in {
    val table = DBTable.build(
        buildDescription(),
        Fields(buildColumns()),
        None,
        Some(Filter("name = 'john'")),
        None,
        noneApplier()
      )
    val sql = table.buildSql()
    assert("SELECT * FROM customer WHERE (\nname = 'john')" === sql)
  }

  "a table with additional order by columns" should "give a query with an order by clause" in {
    val table = DBTable.build(
        buildDescription(),
        Fields(buildColumns()),
        None,
        None,
        Some(OrderByFields(List(
          OrderByField(buildNameColumn(), OrderByDirection.ASC),
          OrderByField(buildAgeColumn(), OrderByDirection.DESC)
          ))),
        noneApplier()
      )
    val sql = table.buildSql()
    assert("SELECT * FROM customer ORDER BY name ASC, age DESC" === sql)
  }

  "a table with empty order by columns list" should "give a simple query" in {
    val table = DBTable.build(
        buildDescription(),
        Fields(buildColumns()),
        None,
        None,
        Some(OrderByFields(List.empty[OrderByField])),
        noneApplier()
      )
    val sql = table.buildSql()
    assert("SELECT * FROM customer" === sql)
  }


  private def buildForeignKeyCriteria() = {
    val rows = List(buildRow("John", "23"))
    ForeignKeyCriteria(rows, buildColumns())
  }


  private def buildNameColumn() = Field("name",  FieldType.STRING)
  private def buildAgeColumn() = Field("age",  FieldType.INT)

  private def buildColumns() = 
    List(
      buildNameColumn(),
      buildAgeColumn()
      )

  private def buildDescription() =
    TableDescription("customer", None, None)

  private def buildRow(name : String, age: String) = FKRow(
    List(
      FieldWithValue("name", name),
      FieldWithValue("age", age)
    ))

  private def noneApplier() = QueryAttributesApplier.from(QueryAttributes(None, None))
}
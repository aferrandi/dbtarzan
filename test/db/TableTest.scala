package dbtarzan.db

import org.scalatest.FlatSpec

class TableTest extends FlatSpec {
  "a simple table" should "give a simple query" in {
    val table = Table.build(
        buildDescription(),
        Fields(buildColumns()),
        None,
        None,
        noneApplier()
      )
    val sql = table.buildSql()
  	assert("SELECT * FROM customer" === sql)
  }


  "a simple table with delimiters" should "give a query with delimiters" in {
      val applier = QueryAttributesApplier.from(QueryAttributes(Some(IdentifierDelimiters('[', ']')), Some("TST")))
    val table = Table.build(
        buildDescription(),
        Fields(buildColumns()),
        None,
        None,
        applier
      )
    val sql = table.buildSql()
    assert("SELECT * FROM TST.[customer]" === sql)
  }


  "a table with foreign criteria" should "give a query with a where clause" in {
    val table = Table.build(
        buildDescription(),
        Fields(buildColumns()),
        Some(buildForeignKeyCriteria()),
        None,
        noneApplier()
      )
    val sql = table.buildSql()
    assert("SELECT * FROM customer WHERE (\n(name='John' AND age=23))" === sql)
  }


  "a table with additional filter" should "give a query with a where clause" in {
    val table = Table.build(
        buildDescription(),
        Fields(buildColumns()),
        None,
        Some(Filter("name = 'john'")),
        noneApplier()
      )
    val sql = table.buildSql()
    assert("SELECT * FROM customer WHERE (\nname = 'john')" === sql)
  }

  private def buildForeignKeyCriteria() = {
    val rows = List(buildRow("John", "23"))
    ForeignKeyCriteria(rows, buildColumns())
  }


  private def buildColumns() = 
    List(
      Field("name",  FieldType.STRING),
      Field("age",  FieldType.INT)
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
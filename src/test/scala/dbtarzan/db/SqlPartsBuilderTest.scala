package dbtarzan.db

import dbtarzan.db.sql.SqlPartsBuilder
import org.scalatest.flatspec.AnyFlatSpec

class SqlPartsBuilderTest extends AnyFlatSpec {

  "an order by with rwo fields" should "give an order by clause" in {
    val sql = SqlPartsBuilder.buildOrderBy(
      OrderByFields(List(
        OrderByField(Field("name", FieldType.STRING, "the name of the person", None), OrderByDirection.DESC),
        OrderByField(Field("age", FieldType.INT, "the age of the person", None), OrderByDirection.ASC)
      ))
    )
    assert("ORDER BY name DESC, age ASC" === sql)
  }

  "an order by with one field" should "give an order by clause" in {
    val sql = SqlPartsBuilder.buildOrderBy(
      OrderByFields(List(
        OrderByField(Field("name", FieldType.STRING, "the name of the person", None), OrderByDirection.DESC),
      ))
    )
    assert("ORDER BY name DESC" === sql)
  }

  "an order by with zeo field" should "gives an empry string" in {
    val sql = SqlPartsBuilder.buildOrderBy(
      OrderByFields(List.empty)
    )
    assert("" === sql)
  }

  "an order by field" should "give an order by clause with one element" in {
    val sql = SqlPartsBuilder.buildOrderByOne(OrderByField(Field("name", FieldType.STRING, "the name of the person", None), OrderByDirection.DESC))
    assert("name DESC" === sql)
  }

  "a filter list of two filters" should "give a where clause with parens" in {
    val sql = SqlPartsBuilder.buildFilters(List("name = 'John'", "age = 27"))
    assert("WHERE (\nname = 'John') AND (\nage = 27)" === sql)
  }

  "a filter list of one filter" should "give a where clause with parens" in {
    val sql = SqlPartsBuilder.buildFilters(List("name = 'John'"))
      assert("WHERE (\nname = 'John')" === sql)
  }

  "a filter list of zero filter" should "give an empty string" in {
    val sql = SqlPartsBuilder.buildFilters(List.empty)
      assert("" === sql)
  }
}
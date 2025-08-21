package dbtarzan.db

import dbtarzan.db.sql.SqlFieldBuilder
import dbtarzan.testutil.TestDatabaseIds
import org.scalatest.flatspec.AnyFlatSpec

class SqlFieldBuilderTest extends AnyFlatSpec {
  "a text field without attributes" should "give a simple [name = 'value']" in {
    val builder = new SqlFieldBuilder(
      fields(),
      QueryAttributes(None, DBDefinition(None,None), None, None)
    )
    val text = builder.buildFieldText(FieldWithValue("name", "John"))
    assert("name='John'" === text)
  }

  "a null text field without attributes" should "give  [name IS NULL]" in {
    val builder = new SqlFieldBuilder(
      fields(),
      QueryAttributes(None, DBDefinition(None, None), None, None)
    )
    val text = builder.buildFieldText(FieldWithValue("name", null))
    assert("name IS NULL" === text)
  }

  "a numeric field without attributes" should "give a simple [name = value]" in {
    val builder = new SqlFieldBuilder(
      fields(),
      QueryAttributes(None, DBDefinition(None,None), None, None)
    )
    val text = builder.buildFieldText(FieldWithValue("age", "23"))
    assert("age=23" === text)
  }

  "a numeric field with attributes" should "give a simple [name = value]" in {
    val builder = new SqlFieldBuilder(
      fields(),
      QueryAttributes(Some(IdentifierDelimitersValues.doubleQuotes), DBDefinition(Some(SchemaId(TestDatabaseIds.databaseId, TestDatabaseIds.simpleDatabaseId, SchemaName("person"))),None), None, None)
    )
    val text = builder.buildFieldText(FieldWithValue("age", "23"))
    assert("\"age\"=23" === text)
  }

  "a text field not found in the table fields" should "give a simple [name = 'value']" in {
    val builder = new SqlFieldBuilder(
      fields(),
      QueryAttributes(None, DBDefinition(None, None), None, None)
    )
    val thrown = intercept[Exception] {
      builder.buildFieldText(FieldWithValue("email", "john@john.com"))
    }
    assert(thrown.getMessage === "field email not found in column types Set(NAME, AGE)")
  }

  private def fields() = {
    List(
      Field("name", FieldType.STRING, "the name of the person", None),
      Field("age", FieldType.INT, "the age of the person", None)
    )
  }
}
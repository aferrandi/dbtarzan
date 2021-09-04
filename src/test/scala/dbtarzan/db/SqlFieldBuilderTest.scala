package dbtarzan.db

import org.scalatest.FlatSpec

class SqlFieldBuilderTest extends FlatSpec {
  "a text field without attributes" should "give a simple [name = 'value']" in {
    val builder = new SqlFieldBuilder(
      fields(),
      QueryAttributes(None, DBDefinition(None,None), None)
    )
    val text = builder.buildFieldText(FieldWithValue("name", "John"))
    assert("name='John'" === text)
  }

  "a numeric field without attributes" should "give a simple [name = value]" in {
    val builder = new SqlFieldBuilder(
      fields(),
      QueryAttributes(None, DBDefinition(None,None), None)
    )
    val text = builder.buildFieldText(FieldWithValue("age", "23"))
    assert("age=23" === text)
  }

  "a numeric field with attributes" should "give a simple [name = value]" in {
    val builder = new SqlFieldBuilder(
      fields(),
      QueryAttributes(Some(IdentifierDelimitersValues.doubleQuotes), DBDefinition(Some(Schema("person")),None), None)
    )
    val text = builder.buildFieldText(FieldWithValue("age", "23"))
    assert("\"age\"=23" === text)
  }

  private def fields() = {
    List(
      Field("name", FieldType.STRING, "the name of the person"),
      Field("age", FieldType.INT, "the age of the person")
    )
  }
}
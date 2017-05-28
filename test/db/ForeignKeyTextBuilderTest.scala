package dbtarzan.db

import org.scalatest.FlatSpec

class ForeignKeyTextBuilderTest extends FlatSpec {
  "building foreign key query with delimiters" should "give a query with delimiters" in {
  	val rows = List(buildRow("John", "23"))
	val criteria = ForeignKeyCriteria(rows, buildColumns())
	val identifiersDelimiters = DelimitersApplier.from(Some(IdentifierDelimiters('[', ']')))
  	val text = ForeignKeyTextBuilder.buildClause(criteria, identifiersDelimiters)
  	assert("([name]='John' AND [age]=23)" === text)
  }

  "building foreign key query without delimiters" should "give a query without delimiters" in {
  	val rows = List(buildRow("John", "23"))
	val criteria = ForeignKeyCriteria(rows, buildColumns())
  	val text = ForeignKeyTextBuilder.buildClause(criteria, DelimitersApplier.from(None))
  	assert("(name='John' AND age=23)" === text)
  }

  "building foreign key query with no row" should "give an empty query" in {
  	val rows = List[FKRow]()
	val criteria = ForeignKeyCriteria(rows, buildColumns())
  	val text = ForeignKeyTextBuilder.buildClause(criteria, DelimitersApplier.from(None))
  	assert("" === text)
  }


  "building foreign key query with multiple row" should "give a complex query" in {
  	val rows = List(buildRow("John", "23"), buildRow("Jane", "33"))
	val criteria = ForeignKeyCriteria(rows, buildColumns())
  	val text = ForeignKeyTextBuilder.buildClause(criteria, DelimitersApplier.from(None))
  	assert("(name='John' AND age=23)\nOR (name='Jane' AND age=33)" === text)
  }


  private def buildColumns() = List(
	Field("name", FieldType.STRING),
	Field("age", FieldType.INT)
	)

  private def buildRow(name : String, age: String) = FKRow(
	List(
		FieldWithValue("name", name),
		FieldWithValue("age", age)
	))
}
package dbtarzan.db.foreignkeys

import dbtarzan.db._
import org.scalatest.flatspec.AnyFlatSpec

class ForeignKeyTextBuilderTest extends AnyFlatSpec {
  "building foreign key query with delimiters" should "give a query with delimiters" in {
    val rows = List(buildRow("John", "23"))
    val criteria = ForeignKeyCriteria(rows, buildColumns(), buildFKColumns())
    val attributes = QueryAttributes(Some(IdentifierDelimitersValues.squareBrackets), DBDefinition(None, None), None, None)
    val text = ForeignKeyTextBuilder.buildClause(criteria, attributes)
    assert("([name]='John' AND [age]=23)" === text)
  }

  "building foreign key query without delimiters" should "give a query without delimiters" in {
    val rows = List(buildRow("John", "23"))
    val criteria = ForeignKeyCriteria(rows, buildColumns(), buildFKColumns())
    val text = ForeignKeyTextBuilder.buildClause(criteria, noneAttributes())
    assert("(name='John' AND age=23)" === text)
  }

  "building foreign key query with no row" should "give an empty query" in {
    val rows = List[FKRow]()
    val criteria = ForeignKeyCriteria(rows, buildColumns(), buildFKColumns())
    val text = ForeignKeyTextBuilder.buildClause(criteria, noneAttributes())
    assert("" === text)
  }

  "building foreign key query with multiple row and multiple columns with in clause active" should "give a complex query" in {
    val rows = List(buildRow("John", "23"), buildRow("Jane", "33"))
    val criteria = ForeignKeyCriteria(rows, buildColumns(), buildFKColumns())
    val text = ForeignKeyTextBuilder.buildClause(criteria, inClauseAttributes())
    assert("(name='John' AND age=23)\nOR (name='Jane' AND age=33)" === text)
  }

  "building foreign key query with multiple row and multiple columns without in clause" should "give a complex query" in {
    val rows = List(buildRow("John", "23"), buildRow("Jane", "33"))
    val criteria = ForeignKeyCriteria(rows, buildColumns(), buildFKColumns())
    val text = ForeignKeyTextBuilder.buildClause(criteria, noneAttributes())
    assert("(name='John' AND age=23)\nOR (name='Jane' AND age=33)" === text)
  }

  "building foreign key query with multiple row but only one column with in clause active" should "give a less complex query" in {
    val rows = List(buildRow("John", "23"), buildRow("Jane", "33"))
    val criteria = ForeignKeyCriteria(rows, buildColumns(), buildNameFKColumns())
    val text = ForeignKeyTextBuilder.buildClause(criteria, inClauseAttributes())
    assert("name IN ('John','Jane')" === text)
  }

  "building foreign key query with multiple row but only one column without in clause" should "give a less complex query" in {
    val rows = List(buildRow("John", "23"), buildRow("Jane", "33"))
    val criteria = ForeignKeyCriteria(rows, buildColumns(), buildNameFKColumns())
    val text = ForeignKeyTextBuilder.buildClause(criteria, noneAttributes())
    assert("(name='John') OR (name='Jane')" === text)
  }


  private def noneAttributes() = QueryAttributes(None, DBDefinition(None, None), None, None)

  private def inClauseAttributes() = QueryAttributes(None, DBDefinition(None, None), None, Some(1000))

  private def buildColumns() = List(
    Field("name", FieldType.STRING, ""),
    Field("age", FieldType.INT, "")
  )

  private def buildFKColumns() = List(
    "name",
    "age"
  )

  private def buildNameFKColumns() = List(
    "name"
  )

  private def buildRow(name : String, age: String) = FKRow(
  List(
    FieldWithValue("name", name),
    FieldWithValue("age", age)
  ))
}
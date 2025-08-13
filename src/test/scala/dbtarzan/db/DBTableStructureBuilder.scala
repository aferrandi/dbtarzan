package dbtarzan.db

import dbtarzan.db.foreignkeys.{FKRow, ForeignKeyCriteria}
import dbtarzan.testutil.TestDatabaseIds

object DBTableStructureBuilder {

  def buildForeignKeyCriteria(): ForeignKeyCriteria = {
    val rows = List(buildRow("John", "23"))
    ForeignKeyCriteria(rows, buildColumns())
  }

  def buildNameColumn(): Field = Field("name", FieldType.STRING, "")

  def buildAgeColumn(): Field = Field("age", FieldType.INT, "")

  def buildColumns(): List[Field] =
    List(
      buildNameColumn(),
      buildAgeColumn()
    )

  def buildDescription(): TableDescription =
    TableDescription(tableName, None, None)

  def tableName: String = "customer"

  def buildRow(name: String, age: String): FKRow = FKRow(buildFields(name, age))

  def buildAttributes(): QueryAttributes =
    QueryAttributes(Some(IdentifierDelimitersValues.squareBrackets), DBDefinition(Some(SchemaId(TestDatabaseIds.databaseId, TestDatabaseIds.simpleDatabaseId, SchemaName("TST"))), None), None, None)

  def buildFields(name: String, age: String): List[FieldWithValue] =
    List(
      FieldWithValue("name", name),
      FieldWithValue("age", age)
    )

  def buildOrderByFields(): OrderByFields =
    OrderByFields(List(
      OrderByField(DBTableStructureBuilder.buildNameColumn(), OrderByDirection.ASC),
      OrderByField(DBTableStructureBuilder.buildAgeColumn(), OrderByDirection.DESC)
    ))
}

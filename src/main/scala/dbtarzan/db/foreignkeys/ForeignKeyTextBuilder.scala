package dbtarzan.db.foreignkeys

import dbtarzan.db.*
import dbtarzan.db.sql.{SqlFieldBuilder, SqlPartsBuilder}

case class FKRow(values : List[FieldWithValue])

case class ForeignKeyCriteria(fkRows : List[FKRow], columnsInForeignKey : List[Field])

/* Builds the query clause related to the selected foreign key */
class ForeignKeyTextBuilder(criteria : ForeignKeyCriteria, attributes : QueryAttributes) {
  val sqlFieldBuilder = new SqlFieldBuilder(criteria.columnsInForeignKey, attributes)

  def buildClause() : String = {
    val filter = buildFilter(criteria.fkRows)
    filter
  }

  private def buildRowText(fkRow : FKRow): String =
    fkRow.values.map(fkValue => sqlFieldBuilder.buildFieldText(fkValue)).mkString("(", " AND ", ")")

  private def buildFilter(fkRows : List[FKRow]): String =
    if(thereIsOnlyOneColumn() && attributes.maxInClauseCount.isDefined)
      buildInClause(fkRows)
    else
      buildOrSequence(fkRows)

  private def buildOrSequence(fkRows: List[FKRow]): String = {
    val rowTexts = fkRows.map(fkRow => buildRowText(fkRow))
    rowTexts.mkString("\nOR ")
  }
  private def buildInClause(fkRows: List[FKRow]): String = {
    val firstColumn = criteria.columnsInForeignKey.head
    val maxInClauseCount = attributes.maxInClauseCount.get
    fkRows
      .map(fkRow => SqlPartsBuilder.buildFieldValueText(sqlFieldBuilder.typeOfField(firstColumn.name), fkRow.values.head.value))
      .grouped(maxInClauseCount)
      .map(chunk => s"${firstColumn.name.toLowerCase()} IN (${chunk.mkString(",")})")
      .mkString("\nOR ")
  }

  private def thereIsOnlyOneColumn(): Boolean =
    criteria.columnsInForeignKey.length == 1
}

object ForeignKeyTextBuilder {
  def buildClause(criteria : ForeignKeyCriteria, attributes :  QueryAttributes) : String =
    new ForeignKeyTextBuilder(criteria, attributes).buildClause()
}
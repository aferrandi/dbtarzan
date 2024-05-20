package dbtarzan.db.foreignkeys

import dbtarzan.db.*
import dbtarzan.db.sql.{SqlFieldBuilder, SqlPartsBuilder}

case class FKRow(values : List[FieldWithValue])

case class ForeignKeyCriteria(fkRows : List[FKRow], columns : List[Field], columnsInForeignKey: List[String])

/* Builds the query clause related to the selected foreign key */
class ForeignKeyTextBuilder(criteria : ForeignKeyCriteria, attributes : QueryAttributes) {
  val sqlFieldBuilder = new SqlFieldBuilder(criteria.columns, attributes)

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
      buildOrSequence()

  private def buildOrSequence(): String = {
    val rowTexts = fkRows.map(fkRow => buildRowText(fkRow))
    rowTexts.mkString("\nOR ")
  }
  private def buildInClause(fkRows: List[FKRow]): String = {
    val firstColumn = criteria.columnsInForeignKey.head
    val count = attributes.maxInClauseCount.get
    fkRows.map(fkRow => SqlPartsBuilder.buildFieldValueText(sqlFieldBuilder.typeOfField(firstColumn), fkRow.values.head.value)).grouped(count).map(
      chunk => firstColumn + " IN (" + chunk.mkString(",") + ")"
    ).mkString("\nOR ")
  }

  private def thereIsOnlyOneColumn(): Boolean =
    criteria.columnsInForeignKey.length == 1
}

object ForeignKeyTextBuilder {
  def buildClause(criteria : ForeignKeyCriteria, attributes :  QueryAttributes) : String =
    new ForeignKeyTextBuilder(criteria, attributes).buildClause()
}
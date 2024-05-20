package dbtarzan.db.foreignkeys

import dbtarzan.db.*
import dbtarzan.db.sql.{SqlFieldBuilder, SqlPartsBuilder}

case class FKRow(values : List[FieldWithValue])

case class ForeignKeyCriteria(fkRows : List[FKRow], columns : List[Field], fieldCount: Int)

/* Builds the query clause related to the selected foreign key */
class ForeignKeyTextBuilder(criteria : ForeignKeyCriteria, attributes : QueryAttributes) {
  val sqlFieldBuilder = new SqlFieldBuilder(criteria.columns, attributes)

  def buildClause() : String = {
    val filter = buildFilter(criteria.fkRows)
    filter
  }

  private def buildRowText(fkRow : FKRow): String =
    fkRow.values.map(fkValue => sqlFieldBuilder.buildFieldText(fkValue)).mkString("(", " AND ", ")")

  private def buildFilter(fkRows : List[FKRow]): String = {
    print(s"size: ${criteria.columns.size} isDefined: ${attributes.maxInClauseCount.isDefined}")
    if(criteria.fieldCount == 1 && attributes.maxInClauseCount.isDefined) {
      val firstColumn = criteria.columns.head
      val count = attributes.maxInClauseCount.get
      fkRows.map(fkRow => SqlPartsBuilder.buildFieldValueText(firstColumn.fieldType, fkRow.values.head.value)).grouped(count).map(
        chunk => firstColumn.name+" IN ("+chunk.mkString(",")+")"
      ).mkString("\nOR ")
    } else {
      val rowTexts = fkRows.map(fkRow => buildRowText(fkRow))
      rowTexts.mkString("\nOR ")
    }
  }
}

object ForeignKeyTextBuilder {
  def buildClause(criteria : ForeignKeyCriteria, attributes :  QueryAttributes) : String =
    new ForeignKeyTextBuilder(criteria, attributes).buildClause()
}
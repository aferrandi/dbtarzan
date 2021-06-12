package dbtarzan.db.foreignkeys

import dbtarzan.db._

case class FKRow(values : List[FieldWithValue])

case class ForeignKeyCriteria(fkRows : List[FKRow], columns : List[Field]) 

/**
	Builds the query clause related to the selected foreign key
*/
class ForeignKeyTextBuilder(criteria : ForeignKeyCriteria, attributes : QueryAttributes) {
  val sqlFieldBuilder = new SqlFieldBuilder(criteria.columns, attributes)

	def buildClause() : String = {
		val filter = buildFilter(criteria.fkRows)
		println("Filter: "+filter)
		filter
	}

	private def buildRowText(fkRow : FKRow): String =
		fkRow.values.map(fkValue => sqlFieldBuilder.buildFieldText(fkValue)).mkString("(", " AND ", ")")

	private def buildFilter(fkRows : List[FKRow]): String = {
		val rowTexts = fkRows.map(fkRow => buildRowText(fkRow))
		rowTexts.mkString("\nOR ")
	}
}

object ForeignKeyTextBuilder {
	def buildClause(criteria : ForeignKeyCriteria, attributes :  QueryAttributes) : String = 
		new ForeignKeyTextBuilder(criteria, attributes).buildClause()
}
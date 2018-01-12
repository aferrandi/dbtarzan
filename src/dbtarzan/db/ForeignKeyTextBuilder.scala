package dbtarzan.db

import scala.util.{Try, Success, Failure}

case class FKRow(values : List[FieldWithValue])

case class ForeignKeyCriteria(fkRows : List[FKRow], columns : List[Field]) 

/**
	Builds the query clause related to the selected foreign key
*/
class ForeignKeyTextBuilder(criteria : ForeignKeyCriteria, applier :  QueryAttributesApplier) {
	val mapColumnTypes = criteria.columns.map(field => (field.name.toUpperCase, field.fieldType)).toMap
	 def buildClause() : String = {
		val filter = buildFilter(criteria.fkRows)
		println("Filter: "+filter)
		filter
	}

	private def buildFieldText(fieldWithValue : FieldWithValue) = {
		val field = fieldWithValue.field
		mapColumnTypes.get(field.toUpperCase) match {
			case Some(fieldType) => buildFieldValueText(fieldWithValue, fieldType) 
			case None => throw new Exception("field "+field+" not found in column types "+mapColumnTypes.keys) 
		}
	}

	private def buildFieldValueText(fieldWithValue : FieldWithValue, fieldType : FieldType) = {
		var fieldRaw = fieldWithValue.field
		val field = applier.applyDelimiters(fieldRaw) 
		val fieldValue = fieldWithValue.value
		if(fieldValue == null)
			field + " IS NULL"
		else if(fieldType == FieldType.STRING)
			field + "='" + fieldValue + "'"
		else 
			field + "=" +  fieldValue.toString
	}

	private def buildRowText(fkRow : FKRow) =
		fkRow.values.map(fkValue => buildFieldText(fkValue)).mkString("(", " AND ", ")")

	private def buildFilter(fkRows : List[FKRow]) = {
		val rowTexts = fkRows.map(fkRow => buildRowText(fkRow))
		rowTexts.mkString("\nOR ")
	}
}

object ForeignKeyTextBuilder {
	def buildClause(criteria : ForeignKeyCriteria, applier :  QueryAttributesApplier) : String = 
		new ForeignKeyTextBuilder(criteria, applier).buildClause()
}
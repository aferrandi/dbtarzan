package dbtarzan.db.foreignkeys

import dbtarzan.db.{ FollowKey, Fields, QueryAttributes, DBTableStructure, TableDescription, Row, FieldWithValue }


/* builds the table that is the result of using a foreign key of another table*/
class ForeignKeyMapper(follow : FollowKey, newColumns : Fields, attributes : QueryAttributes) {
	val mapNameToIndex: Map[String, Int] = follow.columns.map(_.name.toUpperCase).zipWithIndex.toMap

	private def toFollowTable() : DBTableStructure = {
		val fkRows= follow.rows.map(row => buildKeyValuesForRow(row))
		val keyCriteria = ForeignKeyCriteria(fkRows, newColumns.fields)
		val description = TableDescription(follow.key.to.table, Option(follow.key.from.table), None)
		DBTableStructure(description, newColumns, Some(keyCriteria), None,  None, attributes)		
	}

	/* has a foreignkey FK(keyfrom, keyto), the columns from */
	private def buildKeyValuesForRow(row : Row) : FKRow = {
		val fromFields = follow.key.from.fields
		val toFields = follow.key.to.fields
		try {
			val indexes = fromFields.map(field => mapNameToIndex(field.toUpperCase))
			val values = indexes.map(index => row.values(index))
			val fieldWithValues = toFields.zip(values).map({case (field, value) => FieldWithValue(field, value) })
			FKRow(fieldWithValues)
		} catch {
			case e : Exception => throw new Exception("Building the key values for the row "+row+" from the map "+mapNameToIndex+" with foreign key  "+follow.key+" got", e)		
		}
	} 
}

object ForeignKeyMapper {
	def toFollowTable(follow : FollowKey, newColumns : Fields, attributes : QueryAttributes) : DBTableStructure = 
		new ForeignKeyMapper(follow, newColumns, attributes).toFollowTable()
}
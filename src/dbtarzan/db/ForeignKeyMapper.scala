package dbtarzan.db


/**
	builds the table that is the result of using a foreign key of another table
*/
class ForeignKeyMapper(follow : FollowKey, newColumns : Fields) {
	val mapNameToIndex = follow.columns.map(_.name.toUpperCase).zipWithIndex.toMap


	private def toFollowTable() : Table = {
		val fkRows= follow.rows.map(row => buildKeyValuesForRow(row))
		val keyCriteria = ForeignKeyCriteria(fkRows, newColumns.fields)
		val description = TableDescription(follow.key.to.table, Option(follow.key.from.table), None)
		Table.build(description, newColumns, Some(keyCriteria), None)		
	}

	/**
		has a foreignkey FK(keyfrom, keyto), the columns from 
	*/
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
	def toFollowTable(follow : FollowKey, newColumns : Fields) : Table = 
		new ForeignKeyMapper(follow, newColumns).toFollowTable()
}
package dbtarzan.db.foreignkeys

import dbtarzan.db.{ FollowKey, Fields, QueryAttributes, DBTableStructure, TableDescription, Row, FieldWithValue }


/* builds the table that is the result of using a foreign key of another table*/
class ForeignKeyMapper(follow : FollowKey, newColumns : Fields, attributes : QueryAttributes) {
	val mapNameToIndex: Map[String, Int] = follow.columns.map(_.name.toUpperCase).zipWithIndex.toMap

	private def buildFollowTable() : DBTableStructure = {
		try {
			val keyCriteria: Option[ForeignKeyCriteria] = follow.rows.map(buildCriteria)
			val description = TableDescription(follow.key.to.table.tableName, Option(follow.key.from.table.tableName), None)
			DBTableStructure(description, newColumns, keyCriteria, None, None, attributes)
		} catch
			case ex: Exception => throw  new Exception(s"Following to table with newColumns $newColumns and follow fields to ${follow.key.to.fields} got ", ex)
	}

	private def buildCriteria(row: List[Row]): ForeignKeyCriteria = {
		def findForeignKeyToFieldInToTable(name: String) =
			newColumns.fields.find(f => f.name.equalsIgnoreCase(name)).get

		val fkRows = row.map(row => buildKeyValuesForRow(row))
		val fkFields = follow.key.to.fields.map(findForeignKeyToFieldInToTable)
		val keyCriteria = ForeignKeyCriteria(fkRows, fkFields)
		keyCriteria
	}

	/* has a foreignkey FK(keyfrom, keyto), the columns from */
	private def buildKeyValuesForRow(row : Row) : FKRow = {
		val fromFields = follow.key.from.fields
		val toFields = follow.key.to.fields
		try
			val indexes = fromFields.map(field => mapNameToIndex(field.toUpperCase))
			val values = indexes.map(index => row.values(index))
			val fieldWithValues = toFields.zip(values).map({case (field, value) => FieldWithValue(field, value) })
			FKRow(fieldWithValues)
		catch
			case e : Exception => throw new Exception(s"Building the key values for the row $row from the map $mapNameToIndex with foreign key ${follow.key} got", e)
	} 
}

object ForeignKeyMapper {
	def toFollowTable(follow : FollowKey, newColumns : Fields, attributes : QueryAttributes) : DBTableStructure = 
		new ForeignKeyMapper(follow, newColumns, attributes).buildFollowTable()
}
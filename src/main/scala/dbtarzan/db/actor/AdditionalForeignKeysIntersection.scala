package dbtarzan.db.actor

import dbtarzan.db._

/* to check if additional foreign keys match existing foreign keys */
object AdditionalForeignKeysIntersection {
	private def equalsIgnoreCase(a: FieldsOnTable, b: FieldsOnTable) : Boolean = 
		a.table.tableName.equalsIgnoreCase(b.table.tableName) &&
    a.table.databaseId.databaseName.equalsIgnoreCase(b.table.databaseId.databaseName) &&
		a.fields.size == b.fields.size &&
		a.fields.sorted.zip(b.fields.sorted).forall({case (aa, bb) => aa.equalsIgnoreCase(bb)})


	private def intersectionOneTable(additionalKey :AdditionalForeignKey, foreignKeysForTable : List[ForeignKey] ) : Boolean = 
		foreignKeysForTable.exists(k => 
			(equalsIgnoreCase(k.from, additionalKey.from) && equalsIgnoreCase(k.to, additionalKey.to)) || 
			(equalsIgnoreCase(k.from, additionalKey.to) && equalsIgnoreCase(k.to, additionalKey.from))
			)
			
	private def extractForeignKeysForTable(foreignKeysByTable: scala.collection.Map[TableId, ForeignKeys], table : TableId) : List[ForeignKey] =
		foreignKeysByTable.get(table).map(_.keys).getOrElse(List.empty)

	def intersection(foreignKeysByTable: scala.collection.Map[TableId, ForeignKeys], additionalKeys :List[AdditionalForeignKey]) : List[String] =
			 additionalKeys.filter(ak => {
				 	val foreignKeysFrom = extractForeignKeysForTable(foreignKeysByTable, ak.from.table)
					val foreignKeysTo = extractForeignKeysForTable(foreignKeysByTable, ak.to.table)
			 		intersectionOneTable(ak, foreignKeysFrom) || intersectionOneTable(ak, foreignKeysTo)
				}).map(_.name)
}
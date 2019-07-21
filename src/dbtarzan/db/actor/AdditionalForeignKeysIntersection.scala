package dbtarzan.db.actor

import dbtarzan.db._

/* to check if additional foreign keys match existing foreign keys */
object AdditionalForeignKeysIntersection {
	private def equalsIgnoreCase(a: FieldsOnTable, b: FieldsOnTable) : Boolean = 
		a.table.equalsIgnoreCase(b.table) &&
		a.fields.size == b.fields.size &&
		a.fields.sorted.zip(b.fields.sorted).forall({case (aa, bb) => aa.equalsIgnoreCase(bb)})


	private def intersectionOneTable(additionalKey :AdditionalForeignKey, foreignKeysForTable : List[ForeignKey] ) : Boolean = 
		foreignKeysForTable.exists(k => 
			(equalsIgnoreCase(k.from, additionalKey.from) && equalsIgnoreCase(k.to, additionalKey.to)) || 
			(equalsIgnoreCase(k.from, additionalKey.to) && equalsIgnoreCase(k.to, additionalKey.from))
			)
	

	def intersection(foreignKeysForCache: scala.collection.Map[String, ForeignKeys], additionalKeys :List[AdditionalForeignKey]) : List[String] = 
			 additionalKeys.filter(ak => 
			 		intersectionOneTable(ak, foreignKeysForCache.get(ak.from.table).map(_.keys).getOrElse(List.empty)) ||
			 		intersectionOneTable(ak, foreignKeysForCache.get(ak.to.table).map(_.keys).getOrElse(List.empty))
				).map(_.name)
}
package dbtarzan.db

/* Represents the table of a database, or better the result of a query (where, order by) on a single table in the database */
class DBTable (structure : DBTableStructure) {
	def tableDescription : TableDescription = structure.description

	def fields : List[Field] = structure.columns.fields

	def hasOrderBy : Boolean = structure.orderByFields.isDefined

	def orderBys  : Option[OrderByFields] = structure.orderByFields

	/* to accumulate the existing filter + the new filter in the table that gets created with the new filter */
	private def addFilterToExisting(additionalFilter : Filter) : Filter = 
		structure.genericFilter
      .map("(" + _.text + ")\nAND (" + additionalFilter.text + ")")
      .map(Filter)
			.getOrElse(additionalFilter)

	def withAdditionalFilter(additionalFilter : Filter): DBTableStructure =
		DBTableStructure(structure.description, structure.columns, structure.foreignFilter, Some(addFilterToExisting(additionalFilter)), structure.orderByFields, structure.attributes)

	def withOrderByFields(newOrderByFields : OrderByFields): DBTableStructure =
		DBTableStructure(structure.description, structure.columns, structure.foreignFilter, structure.genericFilter, Some(newOrderByFields), structure.attributes)
}


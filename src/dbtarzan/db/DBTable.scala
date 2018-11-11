package dbtarzan.db

/* Represents the table of a database */
class DBTable private (
	description : TableDescription, 
	columns : Fields, 
	foreignFilter : Option[ForeignKeyCriteria], 
	genericFilter : Option[Filter], 
	orderByFields : Option[OrderByFields],
	attributesApplier : QueryAttributesApplier
	) {
	val sql = SqlBuilder.buildSql(description, foreignFilter, genericFilter, orderByFields, attributesApplier)

	def tableDescription : TableDescription= description

	def columnNames : List[Field] = columns.fields

	def hasFilter : Boolean = genericFilter.isDefined

	def hasOrderBy : Boolean = orderByFields.isDefined

	def orderBys  : Option[OrderByFields] = orderByFields

	/* to accumulate the existing filter + the new filter in the table that gets created with the new filter */
	private def addFilterToExisting(additionalFilter : Filter) : Filter = 
		genericFilter.map("(" + _.text + ")\nAND (" + additionalFilter.text + ")").map(Filter(_))
			.getOrElse(additionalFilter)

	def withAdditionalFilter(additionalFilter : Filter) =
		new DBTable(description, columns, foreignFilter, Some(addFilterToExisting(additionalFilter)), orderByFields, attributesApplier)

	def withOrderByFields(newOrderByFields : OrderByFields) = 
		new DBTable(description, columns, foreignFilter, genericFilter, Some(newOrderByFields), attributesApplier)
}

object DBTable {
	def build(
		description : TableDescription, 
		columns : Fields, 
		foreignFilter : Option[ForeignKeyCriteria], 
		genericFilter : Option[Filter], 
		orderByFields : Option[OrderByFields],
		attributesApplier : QueryAttributesApplier
		) : DBTable =
		new DBTable(description, columns, foreignFilter, genericFilter, orderByFields, attributesApplier)  

	def build(description : TableDescription, columns : Fields, attributesApplier : QueryAttributesApplier) : DBTable =
		build(description, columns, None, None, None, attributesApplier)
}
package dbtarzan.db

/* to apply the delimeters (e.g. [ and ] in SQL server) to the table names or to the field names when querying them, and/or prefixing the table
	names with a schema */
class QueryAttributesApplier(queryAttributes: QueryAttributes) {
	def applyDelimiters(identifier: String) : String =
		queryAttributes.delimiters.map(ds => ds.withDelimiters(identifier)).getOrElse(identifier)

	def applySchema(identifier: String) : String = 	
		queryAttributes.definition.schema.map(s => applyDelimiters(s.name)+"."+identifier).getOrElse(identifier)

	def applySchemaAndDelimiters(identifier: String) : String =
		applySchema(applyDelimiters(identifier))
}


object QueryAttributesApplier {
	def from(queryAttributes: QueryAttributes) = new QueryAttributesApplier(queryAttributes)

	def none(): QueryAttributesApplier = from(QueryAttributes(None, DBDefinition(None, None)))
} 


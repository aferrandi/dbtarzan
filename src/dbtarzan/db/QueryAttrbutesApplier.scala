package dbtarzan.db


class QueryAttributesApplier(queryAttributes: QueryAttributes) {
	def applyDelimiters(identifier: String) : String = 	
		queryAttributes.delimiters.map(ds => ds.withDelimiters(identifier)).getOrElse(identifier)

	def applySchema(identifier: String) : String = 	
		queryAttributes.schema.map(s => applyDelimiters(s)+"."+identifier).getOrElse(identifier)

	def applyBoth(identifier: String) : String = 	
		applySchema(applyDelimiters(identifier))
}


object QueryAttributesApplier {
	def from(queryAttributes: QueryAttributes) = new QueryAttributesApplier(queryAttributes)

	def none() = from(QueryAttributes(None, None))
} 


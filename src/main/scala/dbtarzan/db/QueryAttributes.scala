package dbtarzan.db

case class IdentifierDelimiters(start: Char, end: Char) {
	def withDelimiters(identifier: String) : String = "" + start + identifier + end			
}

object IdentifierDelimitersValues {
  val squareBrackets: IdentifierDelimiters = IdentifierDelimiters('[', ']')
  val doubleQuotes: IdentifierDelimiters = IdentifierDelimiters('"', '"')
}

case class DBDefinition(schemaId : Option[SchemaId], catalog : Option[String])

case class QueryAttributes(delimiters : Option[IdentifierDelimiters], definition : DBDefinition, maxFieldSize: Option[Int])

object QueryAttributes {
	def none(): QueryAttributes = QueryAttributes(None, DBDefinition(None, None), None)
} 
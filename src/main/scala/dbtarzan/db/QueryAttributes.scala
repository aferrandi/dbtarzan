package dbtarzan.db

case class IdentifierDelimiters(start: Char, end: Char) {
	def withDelimiters(identifier: String) : String = start + identifier + end			
}

case class DBDefinition(schema : Option[Schema], catalog : Option[String], maxFieldSize: Option[Int])

case class QueryAttributes(delimiters : Option[IdentifierDelimiters], definition : DBDefinition)

object QueryAttributes {
	def none() = QueryAttributes(None, DBDefinition(None, None, None))
} 
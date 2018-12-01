package dbtarzan.db

case class IdentifierDelimiters(start: Char, end: Char) {
	def withDelimiters(identifier: String) : String = start + identifier + end			
}

case class QueryAttributes(delimiters : Option[IdentifierDelimiters], schema : Option[String])

object QueryAttributes {
	def none() = QueryAttributes(None, None)
} 
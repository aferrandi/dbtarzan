package dbtarzan.db

case class IdentifierDelimiters(start: Char, end: Char) {
	def withDelimiters(identifier: String) : String = start + identifier + end			
}

class DelimitersApplier(delimiters: Option[IdentifierDelimiters])
{
	def apply(identifier: String) : String = 	
		delimiters.map(ds => ds.withDelimiters(identifier)).getOrElse(identifier)
}

object DelimitersApplier {
	def from(delimiters: Option[IdentifierDelimiters]) = new DelimitersApplier(delimiters)
} 
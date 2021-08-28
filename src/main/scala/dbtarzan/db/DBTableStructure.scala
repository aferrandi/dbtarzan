package dbtarzan.db

import dbtarzan.db.foreignkeys.ForeignKeyCriteria

case class DBTableStructure(	
	description : TableDescription, 
	columns : Fields, 
	foreignFilter : Option[ForeignKeyCriteria], 
	genericFilter : Option[Filter], 
	orderByFields : Option[OrderByFields],
	attributes : QueryAttributes
	)

object DBTableStructure {
	def build(description : TableDescription, columns : Fields, attributes : QueryAttributes) : DBTableStructure =
		DBTableStructure(description, columns, None, None, None, attributes)

  def hasFilter(structure : DBTableStructure) : Boolean = structure.genericFilter.isDefined
}

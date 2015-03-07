package dbtarzan.db

import dbtarzan.messages.{QueryRows, QueryForeignKeys, TableId}

/**
	Represents the table of a database 
*/
class Table private (description : TableDescription, columns : Fields, foreignFilter : Option[ForeignKeyCriteria], additionalFilter : Option[Filter]) {
	val sql = buildSql()

	def buildSql() : String = {
		def buildFilters(filters : List[String]) : String = { 
			if(!filters.isEmpty)   
				filters.mkString(" WHERE (\n", ") AND (\n", ")")
			else 
				""
		}

		var foreignClosure = foreignFilter.map(ForeignKeyTextBuilder.buildClause(_))
		val filters = List(foreignClosure, additionalFilter.map(_.text)).flatten
		"select * from " + description.name + buildFilters(filters)
	}

	def tableDescription = description

	def columnNames = columns.fields

	def hasFilter = additionalFilter.isDefined
	
	private def addFilterToExisting(filter : Filter) = 
		additionalFilter.map("(" + _.text + ")\nAND (" + filter.text + ")").map(Filter(_))
			.getOrElse(filter)

	def withAdditionalFilter(filter : Filter) =
		new Table(description, columns, foreignFilter, Some(addFilterToExisting(filter)))
}

object Table {
	def build(description : TableDescription, columns : Fields, foreignFilter : Option[ForeignKeyCriteria], additionalFilter : Option[Filter]) : Table =
		new Table(description, columns, foreignFilter, additionalFilter)  

	def build(description : TableDescription, columns : Fields) : Table =
		build(description, columns, None, None)
}
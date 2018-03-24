package dbtarzan.db

import dbtarzan.messages.{QueryRows, QueryForeignKeys, TableId}

/**
	Represents the table of a database 
*/
class Table private (
	description : TableDescription, columns : Fields, 
	foreignFilter : Option[ForeignKeyCriteria], 
	genericFilter : Option[Filter], 
	orderByFields : Option[OrderByFields],
	attributesApplier : QueryAttributesApplier
	) {
	val sql = buildSql()

	/* builds the SQL to query the table from the (potential) original foreign key (to know which rows it has to show), the potential where filter and the table name */
	def buildSql() : String = {
		def buildFilters(filters : List[String]) : String = { 
			if(!filters.isEmpty)   
				filters.mkString(" WHERE (\n", 
									") AND (\n"
									, ")")
			else 
				""
		}


		def directionText(direction : OrderByDirection) : String = direction match {
			case OrderByDirection.ASC => "ASC"
			case OrderByDirection.DESC => "DESC"
			case _ => "<ERROR>" 
		}  

		def buildOrderByOne(orderByField: OrderByField) : String = 
			orderByField.field.name + " " + directionText(orderByField.direction) 
		
		def buildOrderBy() : String = 
			orderByFields.map(" ORDER BY " + _.fields.map(buildOrderByOne).mkString(", ")).getOrElse("")	

		var foreignClosure = foreignFilter.map(ForeignKeyTextBuilder.buildClause(_, attributesApplier))
		val filters = List(foreignClosure, genericFilter.map(_.text)).flatten
		var delimitedTableNameWithSchema = attributesApplier.applyBoth(description.name)
		"SELECT * FROM " + delimitedTableNameWithSchema + buildFilters(filters) + buildOrderBy()
	}

	def tableDescription = description

	def columnNames = columns.fields

	def hasFilter = genericFilter.isDefined

	def hasOrderBy = orderByFields.isDefined

	def orderBys = orderByFields


	/* to accumulate the existing filter + the new filter in the table that gets created with the new filter */
	private def addFilterToExisting(additionalFilter : Filter) : Filter = 
		genericFilter.map("(" + _.text + ")\nAND (" + additionalFilter.text + ")").map(Filter(_))
			.getOrElse(additionalFilter)

	def withAdditionalFilter(additionalFilter : Filter) =
		new Table(description, columns, foreignFilter, Some(addFilterToExisting(additionalFilter)), orderByFields, attributesApplier)

	def withOrderByFields(newOrderByFields : OrderByFields) =
		new Table(description, columns, foreignFilter, genericFilter, Some(newOrderByFields), attributesApplier)	 
}

object Table {
	def build(
		description : TableDescription, 
		columns : Fields, 
		foreignFilter : Option[ForeignKeyCriteria], 
		genericFilter : Option[Filter], 
		orderByFields : Option[OrderByFields],
		attributesApplier : QueryAttributesApplier
		) : Table =
		new Table(description, columns, foreignFilter, genericFilter, orderByFields, attributesApplier)  

	def build(description : TableDescription, columns : Fields, attributesApplier : QueryAttributesApplier) : Table =
		build(description, columns, None, None, None, attributesApplier)
}
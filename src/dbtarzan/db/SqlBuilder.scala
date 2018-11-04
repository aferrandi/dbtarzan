package dbtarzan.db

class SqlBuilder(
	description : TableDescription, 
    foreignFilter : Option[ForeignKeyCriteria], 
    genericFilter : Option[Filter], 
    orderByFields : Option[OrderByFields],
    attributesApplier : QueryAttributesApplier
) {
    private def buildFilters(filters : List[String]) : String = { 
        if(!filters.isEmpty)   
            filters.mkString(" WHERE (\n", 
                                ") AND (\n"
                                , ")")
        else 
            ""
    }

    private def buildOrderByOne(orderByField: OrderByField) : String = 
        orderByField.field.name + " " + DBEnumsText.orderByDirectionToText(orderByField.direction) 

    private def buildOrderBy() : String = 
        orderByFields.filter(_.fields.nonEmpty).map(" ORDER BY " + _.fields.map(buildOrderByOne).mkString(", ")).getOrElse("")	


	/* builds the SQL to query the table from the (potential) original foreign key (to know which rows it has to show), the potential where filter and the table name */
	def buildSql() : QuerySql = {
		var foreignClosure = foreignFilter.map(ForeignKeyTextBuilder.buildClause(_, attributesApplier))
		val filters = List(foreignClosure, genericFilter.map(_.text)).flatten
		var delimitedTableNameWithSchema = attributesApplier.applyBoth(description.name)
		QuerySql("SELECT * FROM " + delimitedTableNameWithSchema + buildFilters(filters) + buildOrderBy())
	}
}

object SqlBuilder {
    def buildSql(	
    	description : TableDescription, 
        foreignFilter : Option[ForeignKeyCriteria], 
        genericFilter : Option[Filter], 
        orderByFields : Option[OrderByFields],
        attributesApplier : QueryAttributesApplier
    ) : QuerySql = new SqlBuilder(description, foreignFilter, genericFilter, orderByFields, attributesApplier).buildSql()
}
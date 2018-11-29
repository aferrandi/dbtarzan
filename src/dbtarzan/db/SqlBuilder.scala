package dbtarzan.db

class SqlBuilder(
    structure: DBTableStructure
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
        structure.orderByFields.filter(_.fields.nonEmpty).map(" ORDER BY " + _.fields.map(buildOrderByOne).mkString(", ")).getOrElse("")	


	/* builds the SQL to query the table from the (potential) original foreign key (to know which rows it has to show), the potential where filter and the table name */
	def buildSql() : QuerySql = {
		var foreignClosure = structure.foreignFilter.map(ForeignKeyTextBuilder.buildClause(_, structure.attributes))
		val filters = List(foreignClosure, structure.genericFilter.map(_.text)).flatten
		var delimitedTableNameWithSchema = QueryAttributesApplier.from(structure.attributes).applyBoth(structure.description.name)
		QuerySql("SELECT * FROM " + delimitedTableNameWithSchema + buildFilters(filters) + buildOrderBy())
	}
}

object SqlBuilder {
    def buildSql(	
        dbTableStructure: DBTableStructure
    ) : QuerySql = new SqlBuilder(dbTableStructure).buildSql()
}
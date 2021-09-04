package dbtarzan.db

import dbtarzan.db.foreignkeys.ForeignKeyTextBuilder

object SqlBuilder {
  val selectClause = "SELECT * FROM "

  /* builds the SQL to query the table from the (potential) original foreign key (to know which rows it has to show), the potential where filter and the table name */
  def buildSql(structure: DBTableStructure) : QuerySql = {
    val foreignClosure = structure.foreignFilter.map(ForeignKeyTextBuilder.buildClause(_, structure.attributes))
    val filters = List(foreignClosure, structure.genericFilter.map(_.text)).flatten
    val delimitedTableNameWithSchema = QueryAttributesApplier.from(structure.attributes).applySchemaAndDelimiters(structure.description.name)
    val orderBy: String = structure.orderByFields.map(SqlPartsBuilder.buildOrderBy).getOrElse("")
    QuerySql(selectClause + delimitedTableNameWithSchema + SqlPartsBuilder.buildFilters(filters) + orderBy)
  }

  def buildSql(structure: DBRowStructure) : QuerySql = {
    val delimitedTableNameWithSchema = QueryAttributesApplier.from(structure.attributes).applySchemaAndDelimiters(structure.tableName)
    val sqlFieldBuilder = new SqlFieldBuilder(structure.columns.fields, structure.attributes)
    QuerySql(selectClause + delimitedTableNameWithSchema + SqlPartsBuilder.buildFilters(structure.filter.map(sqlFieldBuilder.buildFieldText)))
  }
}
package dbtarzan.db.sql

import dbtarzan.db.foreignkeys.ForeignKeyTextBuilder
import dbtarzan.db.*


object SqlBuilder {

  val countClause = "SELECT COUNT(*) FROM "

  /* builds the SQL to query the table from the (potential) original foreign key (to know which rows it has to show), the potential where filter and the table name */
  def buildQuerySql(structure: DBTableStructure, maxFieldSize: Option[MaxFieldSize]) : QuerySql = {
    val foreignClosure = buildForeignClosure(structure)
    val filters = buildFilters(structure, foreignClosure)
    val delimitedTableNameWithSchema = buildTableName(structure.attributes, structure.description.name)
    val orderBy: String = structure.orderByFields.map(SqlPartsBuilder.buildOrderBy).getOrElse("")
    val selectClause: String = buildSqlClause(structure, maxFieldSize)
    QuerySql(s"SELECT $selectClause FROM $delimitedTableNameWithSchema ${SqlPartsBuilder.buildFilters(filters)} $orderBy")
  }

  private def buildSqlClause(structure: DBTableStructure, maxFieldSize: Option[MaxFieldSize]): String = {
    def extractFieldNameNoSubstring(field: Field): String = field.name
    def extractFieldNameSubstring(textApplier: TextLeftApplier)(field: Field): String =
      if(field.fieldType == FieldType.STRING )  textApplier.replaceColumnName(field.name) else field.name
    val extractFieldName: Field => String = maxFieldSize match {
      case Some(m) => m.lefSQLFunction match {
        case Some(l) => extractFieldNameSubstring(TextLeftApplier(l, m.value))
        case None => extractFieldNameNoSubstring
      }
      case None => extractFieldNameNoSubstring
    }
    structure.columns.fields.map(extractFieldName).mkString(", ")
  }

  def buildSingleRowSql(structure: DBRowStructure) : QuerySql = {
    val delimitedTableNameWithSchema = buildTableName(structure.attributes, structure.tableName)
    val sqlFieldBuilder = new SqlFieldBuilder(structure.columns.fields, structure.attributes)
    val selectClause = "SELECT * FROM "
    QuerySql(selectClause + delimitedTableNameWithSchema + SqlPartsBuilder.buildFilters(structure.filter.map(sqlFieldBuilder.buildFieldText)))
  }

  def buildCountSql(structure: DBTableStructure): QuerySql = {
    val foreignClosure = buildForeignClosure(structure)
    val filters = buildFilters(structure, foreignClosure)
    val delimitedTableNameWithSchema = buildTableName(structure.attributes, structure.description.name)
    QuerySql(countClause + delimitedTableNameWithSchema + SqlPartsBuilder.buildFilters(filters))
  }

  private def buildTableName(attributes: QueryAttributes, tableName: String) = {
    QueryAttributesApplier.from(attributes).applySchemaAndDelimiters(tableName)
  }

  private def buildForeignClosure(structure: DBTableStructure) = {
    structure.foreignFilter.map(ForeignKeyTextBuilder.buildClause(_, structure.attributes))
  }

  private def buildFilters(structure: DBTableStructure, foreignClosure: Option[String]) = {
    List(foreignClosure, structure.genericFilter.map(_.text)).flatten
  }
}
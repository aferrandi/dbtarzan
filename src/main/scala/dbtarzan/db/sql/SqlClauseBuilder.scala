package dbtarzan.db.sql

import dbtarzan.db.{DBTableStructure, Field, FieldType, MaxFieldSize}

class SqlClauseBuilder(maybeMaxFieldSize: Option[MaxFieldSize]) {
  private case class MaxFieldSizeWithApplier(textLeftApplier: TextLeftApplier, maxFieldSize: Int)

  private val maybeWithApplier = maybeMaxFieldSize.flatMap(buildTextLeftApplier)

  private def buildTextLeftApplier(maxFieldSize: MaxFieldSize): Option[MaxFieldSizeWithApplier] =
    maxFieldSize.leftSQLFunction.map(l => MaxFieldSizeWithApplier(TextLeftApplier(l, maxFieldSize.value), maxFieldSize.value))

  private def extractStringFieldNameSubstring(field: Field, withApplier: MaxFieldSizeWithApplier): String =
    field.maxLength match {
      case Some(maxLength) => if (maxLength > withApplier.maxFieldSize) withApplier.textLeftApplier.replaceColumnName(field.name) else field.name
      case None => withApplier.textLeftApplier.replaceColumnName(field.name)
    }

  private def extractFieldName(field: Field): String =
    if (field.fieldType == FieldType.STRING)
      extractStringFieldName(field)
    else
       field.name

  private def extractStringFieldName(field: Field): String = maybeWithApplier match {
    case Some(t) =>  extractStringFieldNameSubstring(field, t)
    case None => field.name
  }

  def buildSqlClause(structure: DBTableStructure): String =
    structure.columns.fields.map(extractFieldName).mkString(", ")
}

object SqlClauseBuilder {
  def buildSqlClause(structure: DBTableStructure, maxFieldSize: Option[MaxFieldSize]): String =
    SqlClauseBuilder(maxFieldSize).buildSqlClause(structure)
}

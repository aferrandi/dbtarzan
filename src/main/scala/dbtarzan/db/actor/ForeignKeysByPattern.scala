package dbtarzan.db.actor

import dbtarzan.db.{FieldsOnTable, ForeignKey}

object ForeignKeysByPattern {
  def filterForeignKeysByPattern(foreignKeys: List[ForeignKey], pattern: String): List[ForeignKey] = {
    val patternUppercase = pattern.toUpperCase()
    foreignKeys.filter(fk => foreignKeyContainsPattern(patternUppercase, fk))
  }

  private def foreignKeyContainsPattern(patternUppercase: String, fk: ForeignKey) = {
    fk.name.toUpperCase().contains(patternUppercase) ||
      fieldsOnTableContainsPattern(fk.to, patternUppercase) ||
      fieldsOnTableContainsPattern(fk.from, patternUppercase)
  }

  private def fieldsOnTableContainsPattern(fieldsOnTable: FieldsOnTable, patternUppercase: String) = {
    fieldsOnTable.table.tableName.toUpperCase().contains(patternUppercase) ||
      fieldsOnTable.fields.exists(field => field.toUpperCase().contains(patternUppercase))
  }
}

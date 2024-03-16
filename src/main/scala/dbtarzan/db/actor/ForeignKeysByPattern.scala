package dbtarzan.db.actor

import dbtarzan.db.{FieldsOnTable, ForeignKey}

object ForeignKeysByPattern {
  def filterForeignKeysByPattern(foreignKeys: List[ForeignKey], pattern: String): List[ForeignKey] = {
    val patternUppercase = pattern.toUpperCase()
    foreignKeys.filter(fk => foreignKeyContainsPattern(patternUppercase, fk))
  }

  /*
   looks for the pattern in:
       - the name of the foreign key
       - in  the to-table
       - in the fields of the to-table included in the foreign key
       - in the fields of the from-table included in the foreign key (the fields of the open table)
   */
  private def foreignKeyContainsPattern(patternUppercase: String, fk: ForeignKey) = {
      fk.name.toUpperCase().contains(patternUppercase) ||
      tableNameContainsPattern(fk.to, patternUppercase) ||
      fieldsOnTableContainsPattern(fk.to, patternUppercase) ||
      fieldsOnTableContainsPattern(fk.from, patternUppercase)
  }

  private def tableNameContainsPattern(fieldsOnTable: FieldsOnTable, patternUppercase: String) =
    fieldsOnTable.table.tableName.toUpperCase().contains(patternUppercase)

  private def fieldsOnTableContainsPattern(fieldsOnTable: FieldsOnTable, patternUppercase: String) =
    fieldsOnTable.fields.exists(field => field.toUpperCase().contains(patternUppercase))
}

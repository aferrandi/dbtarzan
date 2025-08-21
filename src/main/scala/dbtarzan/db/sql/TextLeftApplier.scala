package dbtarzan.db.sql

class TextLeftApplier(leftFunctionText: String, maxFieldSize: Int) {
 private val leftFunctionTextMaxReplaced = leftFunctionText.replace("$max", maxFieldSize.toString)
  
  def replaceColumnName(columnName: String): String =
    leftFunctionTextMaxReplaced.replace("$column", columnName)  
}

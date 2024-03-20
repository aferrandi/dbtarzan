package dbtarzan.db.actor

import dbtarzan.db.TableNames
import dbtarzan.db.basicmetadata.ColumnWithTable

class TableAncColumnByPatternCache(columnsTables: List[ColumnWithTable]) {
  private val tablesByNameUppercase: Map[String, List[String]] = buildTablesByTableAndColumnNameUppercase(columnsTables)

  def extractTablesMatchingPattern(pattern: String): TableNames = {
    val uppercasePattern = pattern.toUpperCase
    val tableNames = tablesByNameUppercase.filter({ case (k, v) => k.contains(uppercasePattern) })
      .flatMap({ case (k, v) => v })
      .toSet
    TableNames(tableNames.toList.sorted)
  }

  private def buildTablesByTableAndColumnNameUppercase(columnsTables: List[ColumnWithTable]): Map[String, List[String]] = {
    columnsTables
      .flatMap(ct => List((ct.tableName, ct.tableName), (ct.columnName, ct.tableName)))
      .map({ case (k, v) => (k.toUpperCase, v) })
      .groupMap({ case (k, v) => k })({ case (k, v) => v })
  }
}

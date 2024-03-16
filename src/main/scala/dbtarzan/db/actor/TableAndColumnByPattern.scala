package dbtarzan.db.actor

import dbtarzan.db.TableNames
import dbtarzan.db.basicmetadata.{MetadataTablesLoader, TableAndSchema}

import scala.collection.immutable.HashSet
import scala.collection.{MapView, immutable}

class TableAndColumnByPattern(tablesLoader: MetadataTablesLoader) {

  private var tablesByNameUppercaseOption: Option[Map[String, List[String]]] = None


  /* gets the tables whose names or whose column names match a pattern */
  def tablesByPattern(pattern: String): TableNames =
    tablesByNameUppercaseOption match {
      case Some(tablesByNameUppercase) => extractTablesMatchingPattern(tablesByNameUppercase, pattern)
      case None => {
        val tablesByNameUppercase = buildTablesByTableAndColumnNameUppercase()
        tablesByNameUppercaseOption = Some(tablesByNameUppercase)
        extractTablesMatchingPattern(tablesByNameUppercase, pattern)
      }
    }

  private def extractTablesMatchingPattern(tablesByNameUppercase: Map[String, List[String]], pattern: String): TableNames = {
    val uppercasePattern = pattern.toUpperCase
    val tableNames = tablesByNameUppercase.filter({ case (k, v) => k.contains(uppercasePattern) })
      .flatMap({ case (k, v) => v })
      .toSet
    TableNames(tableNames.toList)
  }

  private def buildTablesByTableAndColumnNameUppercase(): Map[String, List[String]] = {
    val columnsTables = tablesLoader.columnNamesWithTables()
    columnsTables
      .flatMap(ct => List((ct.tableName, ct.tableName), (ct.columnName, ct.tableName)))
      .map({ case (k, v) => (k.toUpperCase, v) })
      .groupMap({ case (k, v) => k })({ case (k, v) => v })
  }
}

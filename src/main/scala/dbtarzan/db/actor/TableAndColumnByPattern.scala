package dbtarzan.db.actor

import dbtarzan.db.TableNames
import dbtarzan.db.basicmetadata.MetadataTablesLoader

class TableAndColumnByPattern(tablesLoader: MetadataTablesLoader) {
  private var cacheOption: Option[TableAncColumnByPatternCache] = None

  /* gets the tables whose names or whose column names match a pattern */
  def tablesByPattern(pattern: String): TableNames =
    cacheOption match {
      case Some(cache) => cache.extractTablesMatchingPattern(pattern)
      case None => buildCache().extractTablesMatchingPattern(pattern)
    }

  private def buildCache() = {
    val columnsTables = tablesLoader.columnNamesWithTables()
    val cache = new TableAncColumnByPatternCache(columnsTables)
    cacheOption = Some(cache)
    cache
  }
}

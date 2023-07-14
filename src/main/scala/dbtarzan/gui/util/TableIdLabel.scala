package dbtarzan.gui.util

import dbtarzan.db.TableId

object TableIdLabel {
  def toLabel(tableId: TableId): String = tableId.databaseId.origin match {
    case Left(_) => tableId.tableName
    case Right(_) => s"${tableId.simpleDatabaseId.databaseName}.${tableId.tableName}"
  }
}
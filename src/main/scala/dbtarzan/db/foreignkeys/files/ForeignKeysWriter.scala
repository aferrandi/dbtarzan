package dbtarzan.db.foreignkeys.files

import dbtarzan.db.{DatabaseId, FieldsOnTable, ForeignKey, ForeignKeys, ForeignKeysForTable, SimpleDatabaseId, TableId}
import grapple.json.{*, given}

object ForeignKeysWriter {
  def toText(list: List[ForeignKeysForTable]): String = {
    val keys = mapFromForeignKeys(list)
    Json.toPrettyPrint(Json.toJson(keys))
  }

  private def mapFromForeignKeys(keys: List[ForeignKeysForTable]): List[ForeignKeysForTableOneDb] = {
    keys.map(k => ForeignKeysForTableOneDb(k.tableId.tableName,
      ForeignKeysOneDb(k.keys.keys.map(l => ForeignKeyOneDb(l.name,
        FieldsOnTableOneDb(l.from.table.tableName, l.from.fields),
        FieldsOnTableOneDb(l.to.table.tableName, l.to.fields),
        l.direction)))
    ))
  }
}

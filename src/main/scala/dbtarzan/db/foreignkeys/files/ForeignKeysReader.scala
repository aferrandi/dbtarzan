package dbtarzan.db.foreignkeys.files

import dbtarzan.db.{DatabaseId, FieldsOnTable, ForeignKey, ForeignKeys, ForeignKeysForTable, SimpleDatabaseId, TableId}
import grapple.json.{*, given}

class ForeignKeysReader(databaseId: DatabaseId, simpleDatabaseId: SimpleDatabaseId) {
  def parseText(text: String): List[ForeignKeysForTable] = {
    val keys = Json.parse(text).as[List[ForeignKeysForTableOneDb]]
    mapToForeignKeys(keys)
  }

  private def mapToForeignKeys(keys: List[ForeignKeysForTableOneDb]): List[ForeignKeysForTable] = {
    keys.map(k => {
      ForeignKeysForTable(TableId(databaseId, simpleDatabaseId, k.table),
        ForeignKeys(k.keys.keys.map(l => ForeignKey(l.name,
          FieldsOnTable(TableId(databaseId, simpleDatabaseId, l.from.table), l.from.fields),
          FieldsOnTable(TableId(databaseId, simpleDatabaseId, l.to.table), l.to.fields),
          l.direction)))
      )
    })
  }
}
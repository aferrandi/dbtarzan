package dbtarzan.db.foreignkeys.files

import dbtarzan.db.*
import grapple.json.{*, given}

object VirtualForeignKeysReader {
  def parseText(text: String): List[VirtualalForeignKey] = {
    Json.parse(text).as[List[VirtualalForeignKey]]
  }

  def readVer1(databaseId: DatabaseId, text: String): List[VirtualalForeignKey] = {
    databaseId.origin match {
      case Left(simpleDatabaseId: SimpleDatabaseId) =>
        Json.parse(text).as[List[VirtualForeignKeyVer1]]
          .map(k => VirtualalForeignKey(k.name,
            FieldsOnTable(TableId(databaseId, simpleDatabaseId, k.from.table), k.from.fields),
            FieldsOnTable(TableId(databaseId, simpleDatabaseId, k.to.table), k.to.fields)
          ))
      case _ => throw new NoSuchElementException("The database can only be simple, not a composite")
    }
  }
}

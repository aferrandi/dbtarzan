package dbtarzan.db.foreignkeys.files

import dbtarzan.db.*
import grapple.json.{*, given}

object AdditionalForeignKeysReader {
  def parseText(text: String): List[AdditionalForeignKey] = {
    Json.parse(text).as[List[AdditionalForeignKey]]
  }

  def readVer1(databaseId: DatabaseId, text: String): List[AdditionalForeignKey] = {
    databaseId.origin match {
      case Left(simpleDatabaseId: SimpleDatabaseId) =>
        Json.parse(text).as[List[AdditionalForeignKeyVer1]]
          .map(k => AdditionalForeignKey(k.name,
            FieldsOnTable(TableId(databaseId, simpleDatabaseId, k.from.table), k.from.fields),
            FieldsOnTable(TableId(databaseId, simpleDatabaseId, k.to.table), k.to.fields)
          ))
      case _ => throw new NoSuchElementException("The database can only be simple, not a composite")
    }
  }
}

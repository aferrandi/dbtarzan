package dbtarzan.db.actor

import dbtarzan.db._
import dbtarzan.db.foreignkeys.ForeignKeysFile
import dbtarzan.localization.Localization
import dbtarzan.messages.Logger

import java.nio.file.Path

class DatabaseForeignKeysFromFile(
  databaseId: DatabaseId,
  simpleDatabaseId : SimpleDatabaseId,
  localization: Localization,
  keyFilesDirPath : Path,
  log : Logger
  ) {
    def loadForeignKeysFromFile() : Map[TableId, ForeignKeys] = {
      val foreignKeysFile =  new ForeignKeysFile(keyFilesDirPath, simpleDatabaseId.databaseName, databaseId, simpleDatabaseId)
      if(foreignKeysFile.fileExist()) {
      log.info(localization.loadingForeignKeys(foreignKeysFile.fileName.toString))
      try {
        val tablesKeys = foreignKeysFile.readFromFile()
        tablesKeys.map(tableKeys => tableKeys.tableId -> tableKeys.keys).toMap
      } catch {
        case (e : Exception) => {
          log.error(localization.errorReadingKeys(foreignKeysFile.fileName.toString), e)
          Map.empty[TableId, ForeignKeys]
        }
      }
    }
    else
      Map.empty[TableId, ForeignKeys]
  }
}
package dbtarzan.db.actor

import dbtarzan.db.foreignkeys.files.AdditionalForeignKeysFile
import dbtarzan.db.{AdditionalForeignKey, DatabaseId}
import dbtarzan.localization.Localization
import dbtarzan.messages.{DatabaseIdUtil, Logger}

import java.nio.file.Path

class DatabaseAdditionalKeysFromFile (
                              databaseId: DatabaseId,
                              localization: Localization,
                              keyFilesDirPath: Path,
                              log: Logger
                            ) {
  def loadAdditionalForeignKeysFromFile(): List[AdditionalForeignKey] = {
    val foreignKeysFile = new AdditionalForeignKeysFile(keyFilesDirPath, DatabaseIdUtil.databaseIdText(databaseId))
    if (foreignKeysFile.fileExist()) {
      log.info(localization.loadingForeignKeys(foreignKeysFile.fileName.toString))
      try {
        foreignKeysFile.readFromFile(databaseId)
      } catch {
        case e: Exception => {
          log.error(localization.errorReadingKeys(foreignKeysFile.fileName.toString), e)
          List.empty[AdditionalForeignKey]
        }
      }
    }
    else
      List.empty[AdditionalForeignKey]
  }
}

package dbtarzan.db.actor

import dbtarzan.db.foreignkeys.files.VirtualForeignKeysFile
import dbtarzan.db.{VirtualalForeignKey, DatabaseId}
import dbtarzan.localization.Localization
import dbtarzan.log.actor.Logger
import dbtarzan.messages.DatabaseIdUtil

import java.nio.file.Path

class DatabaseVirtualKeysFromFile(
                              databaseId: DatabaseId,
                              localization: Localization,
                              keyFilesDirPath: Path,
                              log: Logger
                            ) {
  def loadVirtualForeignKeysFromFile(): List[VirtualalForeignKey] = {
    val foreignKeysFile = new VirtualForeignKeysFile(keyFilesDirPath, DatabaseIdUtil.databaseIdText(databaseId))
    if (foreignKeysFile.fileExist()) {
      log.info(localization.loadingForeignKeys(foreignKeysFile.fileName.toString))
      try
        foreignKeysFile.readFromFile(databaseId)
      catch
        case e: Exception => {
          log.error(localization.errorReadingKeys(foreignKeysFile.fileName.toString), e)
          List.empty[VirtualalForeignKey]
      }
    }
    else
      List.empty[VirtualalForeignKey]
  }
}

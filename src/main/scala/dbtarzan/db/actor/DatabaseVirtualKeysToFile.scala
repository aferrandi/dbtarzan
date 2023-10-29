package dbtarzan.db.actor

import java.nio.file.Path
import dbtarzan.db._
import dbtarzan.db.foreignkeys.files.VirtualForeignKeysFile
import dbtarzan.localization.Localization
import dbtarzan.messages.DatabaseIdUtil.databaseIdText
import dbtarzan.messages.Logger

class DatabaseVirtualKeysToFile(
  databaseId : DatabaseId,
  localization: Localization,
  keyFilesDirPath: Path,
  log : Logger
  ) {
  private def saveForeignKeysToFile(foreignKeysFile : VirtualForeignKeysFile, keys : List[VirtualalForeignKey]): Unit = {
    log.info(localization.savingForeignKeys(foreignKeysFile.fileName.toString))
    try
      foreignKeysFile.writeAsFile(keys)
    catch
      case e : Exception => log.error(localization.errorWritingKeys(foreignKeysFile.fileName.toString), e)
  }


  def saveVirtualForeignKeys(keys : List[VirtualalForeignKey]) : Unit =
    saveForeignKeysToFile(new VirtualForeignKeysFile(keyFilesDirPath, databaseIdText(databaseId)), keys)
}
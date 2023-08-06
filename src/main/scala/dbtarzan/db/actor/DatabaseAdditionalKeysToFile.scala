package dbtarzan.db.actor

import java.nio.file.Path
import dbtarzan.db._
import dbtarzan.db.foreignkeys.AdditionalForeignKeysFile
import dbtarzan.localization.Localization
import dbtarzan.messages.DatabaseIdUtil.databaseIdText
import dbtarzan.messages.Logger

class DatabaseAdditionalKeysToFile(
  databaseId : DatabaseId,
  localization: Localization,
  keyFilesDirPath: Path,
  log : Logger
  ) {
  private def saveForeignKeysToFile(foreignKeysFile : AdditionalForeignKeysFile, keys : List[AdditionalForeignKey]): Unit = {
    log.info(localization.savingForeignKeys(foreignKeysFile.fileName.toString))
    try {
      foreignKeysFile.writeAsFile(keys)
    } catch {
      case e : Exception => log.error(localization.errorWritingKeys(foreignKeysFile.fileName.toString), e)
    }
  }


  def saveAdditionalForeignKeys(keys : List[AdditionalForeignKey]) : Unit =
    saveForeignKeysToFile(new AdditionalForeignKeysFile(keyFilesDirPath, databaseIdText(databaseId)), keys)
}
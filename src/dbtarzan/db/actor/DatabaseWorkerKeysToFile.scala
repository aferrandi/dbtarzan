package dbtarzan.db.actor

import dbtarzan.db._
import dbtarzan.db.foreignkeys.{ForeignKeysFile, ForeignKeysFiles }
import dbtarzan.localization.Localization
import dbtarzan.messages.Logger
import dbtarzan.db.ForeignKeys

class DatabaseWorkerKeysToFile(
	databaseName : String, 
	localization: Localization,
	log : Logger
	) {
    private def saveForeignKeysToFile(foreignKeysFile : ForeignKeysFile, keys : ForeignKeysForTableList) = {
		log.info(localization.savingForeignKeys(foreignKeysFile.fileName.toString()))
		try {
			foreignKeysFile.toFile(keys)
		} catch { 
			case e : Exception => {
				log.error(localization.errorWritingKeys(foreignKeysFile.fileName.toString()), e) 
			}
		}
	}


	def saveAdditionalForeignKeys(keys : ForeignKeysForTableList) : Unit =
		saveForeignKeysToFile(ForeignKeysFiles.additional(databaseName), keys)
}
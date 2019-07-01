package dbtarzan.db.actor

import dbtarzan.db._
import dbtarzan.db.foreignkeys.{ AdditionalForeignKeysFile }
import dbtarzan.localization.Localization
import dbtarzan.messages.Logger

class DatabaseWorkerKeysToFile(
	databaseName : String, 
	localization: Localization,
	log : Logger
	) {
    private def saveForeignKeysToFile(foreignKeysFile : AdditionalForeignKeysFile, keys : List[AdditionalForeignKey]) = {
		log.info(localization.savingForeignKeys(foreignKeysFile.fileName.toString()))
		try {
			foreignKeysFile.toFile(keys)
		} catch { 
			case e : Exception => {
				log.error(localization.errorWritingKeys(foreignKeysFile.fileName.toString()), e) 
			}
		}
	}


	def saveAdditionalForeignKeys(keys : List[AdditionalForeignKey]) : Unit =
		saveForeignKeysToFile(new AdditionalForeignKeysFile(databaseName), keys)
}
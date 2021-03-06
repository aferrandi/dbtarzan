package dbtarzan.db.actor

import java.nio.file.Path

import dbtarzan.db._
import dbtarzan.db.foreignkeys.{ForeignKeysFile, AdditionalForeignKeysFile }
import dbtarzan.localization.Localization
import dbtarzan.messages.Logger
import dbtarzan.db.ForeignKeys

class DatabaseWorkerKeysFromFile(
	databaseName : String, 
	localization: Localization,
	keyFilesDirPath : Path,
	log : Logger
	) {
    def loadForeignKeysFromFile() : Map[String, ForeignKeys] = {
		val foreignKeysFile =  new ForeignKeysFile(keyFilesDirPath, databaseName)
        if(foreignKeysFile.fileExist()) {
			log.info(localization.loadingForeignKeys(foreignKeysFile.fileName.toString()))
			try {
				val tablesKeys = foreignKeysFile.fromFile()
				tablesKeys.keys.map(tableKeys => tableKeys.table -> tableKeys.keys).toMap
			} catch { 
				case e : Exception => {
					log.error(localization.errorReadingKeys(foreignKeysFile.fileName.toString()), e) 
				 	Map.empty[String, ForeignKeys]
				}
			}
		} 
		else 
			Map.empty[String, ForeignKeys]
	}


    def loadAdditionalForeignKeysFromFile() : List[AdditionalForeignKey] = {
        val foreignKeysFile =  new AdditionalForeignKeysFile(keyFilesDirPath, databaseName)
		if(foreignKeysFile.fileExist()) {
			log.info(localization.loadingForeignKeys(foreignKeysFile.fileName.toString()))
			try {
				foreignKeysFile.fromFile()
			} catch { 
				case e : Exception => {
					log.error(localization.errorReadingKeys(foreignKeysFile.fileName.toString()), e) 
				 	List.empty[AdditionalForeignKey]
				}
			}
		} 
		else 
			List.empty[AdditionalForeignKey]
	}
}
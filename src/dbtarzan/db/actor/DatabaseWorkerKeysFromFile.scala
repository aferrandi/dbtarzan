package dbtarzan.db.actor

import dbtarzan.db._
import dbtarzan.db.foreignkeys.{ForeignKeysFile, ForeignKeysFiles }
import dbtarzan.localization.Localization
import dbtarzan.messages.Logger
import dbtarzan.db.ForeignKeys

class DatabaseWorkerKeysFromFile(
	databaseName : String, 
	localization: Localization,
	log : Logger
	) {
    private def loadForeignKeysFromFile(foreignKeysFile : ForeignKeysFile) : Map[String, ForeignKeys] = 
        if(foreignKeysFile.fileExist()) {
			log.info(localization.loadingForeignKeys(foreignKeysFile.fileName.toString()))
			try
			{
				val tablesKeys = foreignKeysFile.fromFile()
				tablesKeys.keys.map(tableKeys => tableKeys.table -> tableKeys.keys).toMap
			} 
			catch { 
				case e : Exception => {
					log.error(localization.errorReadingKeys(foreignKeysFile.fileName.toString()), e) 
				 	Map.empty[String, ForeignKeys]
				}
			}
		} 
		else 
			Map.empty[String, ForeignKeys]


	def loadForeignKeysForCache() : Map[String, ForeignKeys] =
		loadForeignKeysFromFile(ForeignKeysFiles.forCache(databaseName))

	def loadAdditionalForeignKeys() : Map[String, ForeignKeys] =
		loadForeignKeysFromFile(ForeignKeysFiles.additional(databaseName))
}
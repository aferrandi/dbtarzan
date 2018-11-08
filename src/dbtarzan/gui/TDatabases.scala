package dbtarzan.gui

import dbtarzan.messages._

/**
	The GUI actor needs to update the GUI without knowing the GUI itself. These are interfaces for this purpose
*/
trait TTable {
	def addRows(rows : ResponseRows) : Unit
	def addForeignKeys(keys : ResponseForeignKeys) : Unit 
	def addPrimaryKeys(keys : ResponsePrimaryKeys) : Unit
}

trait TTables extends TTable {
	def addColumns(columns : ResponseColumns) : Unit
	def addColumnsFollow(columns : ResponseColumnsFollow) : Unit
}

trait TDatabases extends TTables {
	def addTables(tables : ResponseTables) : Unit
	def addDatabase(databaseData : ResponseDatabase) : Unit
	def showDatabase(databaseId : DatabaseId) : Unit
	def removeDatabase(databaseToClose : ResponseCloseDatabase) : Unit
	def removeTables(tables : ResponseCloseTables) : Unit 
    def requestRemovalTabsAfter(request : RequestRemovalTabsAfter) : Unit
	def requestRemovalTabsBefore(request : RequestRemovalTabsBefore) : Unit
  	def requestRemovalAllTabs(request : RequestRemovalAllTabs) : Unit
	def copySelectionToClipboard(copy : CopySelectionToClipboard) : Unit
	def copySQLToClipboard(copy : CopySQLToClipboard) : Unit
	def checkAllTableRows(check : CheckAllTableRows) : Unit
	def checkNoTableRows(check :  CheckNoTableRows) : Unit
	def switchRowDetails(switch: SwitchRowDetails) : Unit
}
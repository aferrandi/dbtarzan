package dbtarzan.gui

import dbtarzan.messages._

/**
	The GUI actor needs to update the GUI without knowing the GUI itself. These are interfaces for this purpose
*/
trait TTable {
	def addRows(rows : ResponseRows) : Unit
	def addForeignKeys(keys : ResponseForeignKeys) : Unit 
}

trait TTables extends TTable {
	def addColumns(columns : ResponseColumns) : Unit
	def addColumnsFollow(columns : ResponseColumnsFollow) : Unit
}

trait TDatabases extends TTables {
	def addTables(tables : ResponseTables) : Unit
	def addDatabase(databaseData : ResponseDatabase) : Unit
}
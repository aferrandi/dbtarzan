package dbtarzan.gui

import dbtarzan.messages._

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
}
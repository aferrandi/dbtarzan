package dbtarzan.gui

import dbtarzan.messages._
import dbtarzan.db.DatabaseId

trait TDatabases {
	def handleDatabaseIdMessage(msg: TWithDatabaseId) : Unit
	def handleQueryIdMessage(msg: TWithQueryId) : Unit
	def handleTableIdMessage(msg: TWithTableId) : Unit
	def showDatabase(databaseId : DatabaseId) : Unit 
}
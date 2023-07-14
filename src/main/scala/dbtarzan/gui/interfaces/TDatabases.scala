package dbtarzan.gui.interfaces

import dbtarzan.db.DatabaseId
import dbtarzan.messages._

trait TDatabases {
	def handleDatabaseIdMessage(msg: TWithDatabaseId) : Unit
	def handleQueryIdMessage(msg: TWithQueryId) : Unit
	def handleTableIdMessage(msg: TWithTableId) : Unit
	def showDatabase(databaseId : DatabaseId) : Boolean
}
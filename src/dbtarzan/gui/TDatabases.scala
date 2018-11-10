package dbtarzan.gui

import dbtarzan.messages._
import dbtarzan.db.DatabaseId

trait TDatabases {
	def handleMessage(msg: TWithDatabaseId) : Unit
	def handleMessage(msg: TWithTableId) : Unit
	def showDatabase(databaseId : DatabaseId) : Unit 
}
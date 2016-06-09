package dbtarzan.gui

import dbtarzan.messages._

/**
	The GUI actor needs to update the GUI without knowing the GUI itself. These are interfaces for this purpose
*/
trait TDatabaseList {
	def setDatabases(rows : DatabaseNames) : Unit
}


package dbtarzan.gui.interfaces

import dbtarzan.db.DatabaseInfo
import dbtarzan.messages.*

/**
	The GUI actor needs to update the GUI without knowing the GUI itself. These are interfaces for this purpose
*/
trait TDatabaseList {
	def setDatabaseInfos(rows : List[DatabaseInfo]) : Unit
}


package dbtarzan.gui

import dbtarzan.db.CompositeId
import dbtarzan.messages._

/**
	The GUI actor needs to update the GUI without knowing the GUI itself. These are interfaces for this purpose
*/
trait TCompositeList {
	def setCompositeIds(rows : List[CompositeId]) : Unit
}


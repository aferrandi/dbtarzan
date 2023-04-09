package dbtarzan.gui.interfaces

import dbtarzan.messages.CompositeIds

/**
	The GUI actor needs to update the GUI without knowing the GUI itself. These are interfaces for this purpose
*/
trait TCompositeList {
	def setCompositeIds(rows : CompositeIds) : Unit
}


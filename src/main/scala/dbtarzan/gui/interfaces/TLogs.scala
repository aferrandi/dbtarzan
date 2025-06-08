package dbtarzan.gui.interfaces

import dbtarzan.messages.TLogMessageGUI

trait TLogs {
	def addLogMessage(msg : TLogMessageGUI) : Unit
}
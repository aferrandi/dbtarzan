package dbtarzan.gui.interfaces

import dbtarzan.messages.TLogMessage

trait TLogs {
	def addLogMessage(msg : TLogMessage) : Unit
}
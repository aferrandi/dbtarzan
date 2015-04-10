package dbtarzan.gui

import dbtarzan.messages.TLogMessage

trait TLogs {
	def addLogMessage(msg : TLogMessage) : Unit
}
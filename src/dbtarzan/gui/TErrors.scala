package dbtarzan.gui

import dbtarzan.messages.TTextMessage

trait TErrors {
	def addTextMessage(msg : TTextMessage) : Unit
}
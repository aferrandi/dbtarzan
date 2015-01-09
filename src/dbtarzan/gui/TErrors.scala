package dbtarzan.gui

import dbtarzan.messages.Error

trait TErrors {
	def addError(err : Error) : Unit
}
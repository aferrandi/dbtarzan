package dbtarzan.gui

import scalafx.scene.control.TextField
import scalafx.event.ActionEvent
import scalafx.Includes._

/**
	the constraint text box (where:)
*/
class QueryText {
	val textBox = new TextField {
		text = ""
	}
	def onEnter(useText : String => Unit) : Unit = {
      textBox.onAction = (event: ActionEvent)  => { useText(textBox.text()) }
    }
}
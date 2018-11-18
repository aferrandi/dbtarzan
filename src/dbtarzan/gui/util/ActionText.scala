package dbtarzan.gui.util

import scalafx.scene.control.TextField
import scalafx.event.ActionEvent
import scalafx.Includes._

/* the constraint text box (where:) */
class ActionText {
	val textBox = new TextField {
		text = ""
	}
	def onEnter(useText : String => Unit) : Unit = {
      textBox.onAction = (event: ActionEvent)  => { useText(textBox.text()) }
    }
}
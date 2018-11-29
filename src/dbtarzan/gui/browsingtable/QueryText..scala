package dbtarzan.gui.browsingtable

import scalafx.scene.control.TextField
import scalafx.event.ActionEvent
import scalafx.Includes._

/* the constraint text box (where:) */
class QueryText {
	val textBox = new TextField {
		text = ""
		text.onChange { (_, _, _) => changeTextColorTo("black")}	
		def changeTextColorTo(color : String) : Unit =
			style = "-fx-text-inner-color: "+color+";"
	}

	def onEnter(useText : String => Unit) : Unit = {
      textBox.onAction = (event: ActionEvent)  => { useText(textBox.text()) }
    }
	def showError() : Unit = textBox.changeTextColorTo("red")
}

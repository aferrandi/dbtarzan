package dbtarzan.gui.browsingtable

import scalafx.scene.control.TextField
import scalafx.scene.input.{ KeyEvent, KeyCode }
import scalafx.Includes._

/* the constraint text box (where:) */
class QueryText {
	val textBox = new TextField {
		text = ""
		text.onChange { (_, _, _) => changeTextColorTo("black")}	
		def changeTextColorTo(color : String) : Unit =
			style = "-fx-text-inner-color: "+color+";"
	}

	def onEnter(useText : (String, Boolean) => Unit) : Unit = {
			textBox.onKeyPressed = (ev: KeyEvent) => if(ev.code == KeyCode.ENTER) 
				useText(textBox.text(), ev.controlDown)
      //textBox.onAction = (ev: ActionEvent)  => { useText(textBox.text(), ev.controlDown) }
    }
	def showError() : Unit = textBox.changeTextColorTo("red")
}

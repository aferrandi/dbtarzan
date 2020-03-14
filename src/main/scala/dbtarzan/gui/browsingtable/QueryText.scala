package dbtarzan.gui.browsingtable

import dbtarzan.db.Fields
import dbtarzan.gui.util.AutoComplete
import scalafx.Includes._
import scalafx.scene.input.{KeyCode, KeyEvent}

/* the constraint text box (where:) */
class QueryText(columns : Fields) {
	val textBox = new AutoComplete(columns.fields.map(_.name), 10) {
		text = ""
		text.onChange { (_, _, _) => changeTextColorTo("black")}

    def changeTextColorTo(color : String) : Unit =
			style = "-fx-text-inner-color: "+color+";"
	}

	def onEnter(useText : (String, Boolean) => Unit) : Unit = {
			textBox.onKeyPressed = (ev: KeyEvent) => if(ev.code == KeyCode.ENTER) 
				useText(textBox.text(), ev.controlDown)
  }

	def showError(): Unit = textBox.changeTextColorTo("red")
}

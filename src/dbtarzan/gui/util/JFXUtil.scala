package dbtarzan.gui.util

import scalafx.scene.Node
import scalafx.scene.control.Label
import scalafx.scene.layout.BorderPane
import scalafx.scene.text.TextAlignment
import scalafx.geometry.Insets
import scalafx.scene.input.{ MouseEvent, KeyEvent, KeyCode }
import scalafx.scene.control.ListView
import scalafx.Includes._

object JFXUtil {
	def withTitle(graphic : Node, title : String) = new BorderPane {
	    top = buildTitle(title)
	    center = graphic
	  }
	def withLeftTitle(graphic : Node, title : String) = new BorderPane {
	    left = buildTitle(title)
	    center = graphic
	  }	  
    def buildTitle(title : String) = new Label(title) {
	    	margin = Insets(5)
	    }
	def onAction[T](list : ListView[T] , action : T => Unit) = {
		def focusedItem(list : ListView[T]) = list.focusModel().focusedItem()
		list.onMouseClicked = (ev: MouseEvent) =>  if(ev.clickCount == 2) 
			action(focusedItem(list))
		list.onKeyPressed = (ev: KeyEvent) => if(ev.code == KeyCode.ENTER) 
			action(focusedItem(list))
	}
}
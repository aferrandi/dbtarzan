package dbtarzan.gui.util

import scalafx.scene.Node
import scalafx.scene.control.{ ListView, TableView, Label, MenuItem }
import scalafx.scene.layout.BorderPane
import scalafx.scene.text.TextAlignment
import scalafx.geometry.Insets
import scalafx.scene.input.{ MouseEvent, KeyEvent, KeyCode, Clipboard, ClipboardContent }
import scalafx.Includes._
import scalafx.event.ActionEvent

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

	private def focusedItem[T](list : ListView[T]) = list.focusModel().focusedItem()    
	def onAction[T](list : ListView[T] , action : T => Unit) = {
		list.onMouseClicked = (ev: MouseEvent) =>  if(ev.clickCount == 2) 
			action(focusedItem(list))
		list.onKeyPressed = (ev: KeyEvent) => if(ev.code == KeyCode.ENTER) 
			action(focusedItem(list))
	}


	private def focusedItem[T](table : TableView[T]) = table.focusModel().focusedItem()    
	def onAction[T](table : TableView[T] , action : T => Unit) = {
		table.onMouseClicked = (ev: MouseEvent) =>  if(ev.clickCount == 2) 
			action(focusedItem(table))
		table.onKeyPressed = (ev: KeyEvent) => if(ev.code == KeyCode.ENTER) 
			action(focusedItem(table))
	}


	def onContextMenu[T](menu : MenuItem, list : ListView[T] , action : T => Unit) = 
		menu.onAction = (ev: ActionEvent) => action(focusedItem(list))

	def copyTextToClipboard(text : String) : Unit = {
      val content = new ClipboardContent()
      content.putString(text)
      Clipboard.systemClipboard.setContent(content)		
	}
}
package dbtarzan.gui.util

import scalafx.scene.Node
import scalafx.scene.control.{ ListView, TableView, Label, Alert, MenuItem, ButtonType }
import scalafx.scene.layout.BorderPane
import scalafx.scene.text.TextAlignment
import scalafx.geometry.Insets
import scalafx.scene.input.{ MouseEvent, KeyEvent, KeyCode, Clipboard, ClipboardContent }
import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.scene.control.Alert.AlertType
import scalafx.collections.ObservableBuffer 

object JFXUtil {
	def threeLines : String = "\u2630"

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

 	def swapListBuffer[T](lb : ObservableBuffer[T], i : Int, j : Int) : Unit =	{
		val vi = lb(i)
		val vj = lb(j)
		lb.update(i, vj)
		lb.update(j, vi)
	}
	def bufferSet[T](b : ObservableBuffer[T], l: Traversable[T]) : ObservableBuffer[T] = {
		b.clear() 
		b ++= l
	}

   def areYouSure(text : String, header: String) = new Alert(AlertType.Confirmation, text, ButtonType.Yes, ButtonType.No ) {
      headerText = header
    }.showAndWait() match {
      case Some(ButtonType.Yes) => true
      case _ => false
    }

  	def showErrorAlert(header : String, error : String) : Unit = new Alert(AlertType.Error) { 
       headerText= header
       contentText= error
       }.showAndWait()
 }
package dbtarzan.gui

import scalafx.collections.ObservableBuffer 
import scalafx.scene.control.{ ListView, ListCell, Tooltip, ContextMenu, MenuItem}
import scalafx.Includes._
import dbtarzan.messages.TTextMessage
import dbtarzan.messages.ErrorText
import scalafx.event.ActionEvent
import dbtarzan.gui.util.JFXUtil
/**
  A list of the errors happened in the application, last error first
*/
class ErrorList extends TErrors {
  val buffer = ObservableBuffer.empty[TTextMessage]
  val list = new ListView[TTextMessage](buffer) {
    cellFactory = { _ => buildCell() }
  }   

  /* need to show only the "to table" as cell text. And a tooltip for each cell */
  private def buildCell() = new ListCell[TTextMessage] {
    item.onChange { (_, _, _) => 
      Option(item.value).foreach(err => {
        tooltip.value = Tooltip(ErrorText.extractWholeErrorText(err))
        text.value = ErrorText.extractErrorMessage(err)
        contextMenu = new ContextMenu(buildClipboardMenu(err))
      })
    }
  }    

  private def buildClipboardMenu(err : TTextMessage) = new MenuItem {
    text = "Copy Message To Clipboard"
    onAction = (ev: ActionEvent) =>  try {
        JFXUtil.copyTextToClipboard(ErrorText.extractErrorMessage(err))
      } catch {
        case ex : Exception => println("Copying the message to the clipboard got "+ex)
      }
    }


  /* Prepends: the last message come becomes the first in the list */
  def addTextMessage(msg :TTextMessage) : Unit = 
    msg +=: buffer 
}


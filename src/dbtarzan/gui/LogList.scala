package dbtarzan.gui

import scalafx.collections.ObservableBuffer 
import scalafx.scene.layout.Priority
import scalafx.scene.control.{ ListView, ListCell, Tooltip, ContextMenu, MenuItem, Alert, TextArea}
import scalafx.scene.control.Alert.AlertType
import scalafx.Includes._
import scalafx.scene.Parent
import dbtarzan.messages.TLogMessage
import dbtarzan.messages.LogText
import scalafx.event.ActionEvent
import dbtarzan.gui.util.JFXUtil
/**
  A list of the errors happened in the application, last error first
*/
class LogList extends TLogs with TControlBuilder {
  private val buffer = ObservableBuffer.empty[TLogMessage]
  private val list = new ListView[TLogMessage](buffer) {
    cellFactory = { _ => buildCell() }
  }   
  JFXUtil.onAction(list, { selectedMessage : TLogMessage => 
      new Alert(AlertType.Information) { 
         headerText="Message"
         contentText= "Details:"
         dialogPane().content =  new TextArea {
              text = LogText.extractWholeLogText(selectedMessage)
              editable = false
              wrapText = true
            } 
         }.showAndWait()

  })


  /* need to show only the "to table" as cell text. And a tooltip for each cell */
  private def buildCell() = new ListCell[TLogMessage] {
    item.onChange { (_, _, _) => 
      Option(item.value).foreach(err => {        
        text.value = LogText.extractLogPrefix(err)+"> "+LogText.extractLogMessage(err)
        contextMenu = new ContextMenu(ClipboardMenuMaker.buildClipboardMenu("Message", () => LogText.extractWholeLogText(err)))
      })
    }
  }    

  /* Prepends: the last message come becomes the first in the list */
  def addLogMessage(log :TLogMessage) : Unit = 
    log +=: buffer

  def control : Parent = list 
}


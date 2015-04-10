package dbtarzan.gui

import scalafx.collections.ObservableBuffer 
import scalafx.scene.control.{ ListView, ListCell, Tooltip, ContextMenu, MenuItem}
import scalafx.Includes._
import dbtarzan.messages.TLogMessage
import dbtarzan.messages.LogText
import scalafx.event.ActionEvent
import dbtarzan.gui.util.JFXUtil
/**
  A list of the errors happened in the application, last error first
*/
class LogList extends TLogs {
  val buffer = ObservableBuffer.empty[TLogMessage]
  val list = new ListView[TLogMessage](buffer) {
    cellFactory = { _ => buildCell() }
  }   

  /* need to show only the "to table" as cell text. And a tooltip for each cell */
  private def buildCell() = new ListCell[TLogMessage] {
    item.onChange { (_, _, _) => 
      Option(item.value).foreach(err => {
        tooltip.value = Tooltip(LogText.extractWholeLogText(err))
        text.value = LogText.extractLogPrefix(err)+"> "+LogText.extractLogMessage(err)
        contextMenu = new ContextMenu(ClipboardMenuMaker.buildClipboardMenu("Message", () => LogText.extractLogMessage(err)))
      })
    }
  }    

  /* Prepends: the last message come becomes the first in the list */
  def addLogMessage(log :TLogMessage) : Unit = 
    log +=: buffer 
}


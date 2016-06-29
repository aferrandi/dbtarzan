package dbtarzan.gui

import scalafx.collections.ObservableBuffer 
import scalafx.scene.layout.Priority
import scalafx.scene.control.TableColumn._
import scalafx.scene.control.{TableColumn, TableView, TableCell, SelectionMode, ContextMenu, Alert, TextArea, Label}
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.image.{ Image, ImageView }
import scalafx.Includes._
import scalafx.scene.Parent
import dbtarzan.messages.TLogMessage
import dbtarzan.messages.LogText
import scalafx.event.ActionEvent
import dbtarzan.gui.util.{ JFXUtil, LogIcons }
import scalafx.beans.property.{ StringProperty, ObjectProperty }

/**
  A list of the errors happened in the application, last error first
*/
class LogList extends TLogs with TControlBuilder {
  private val buffer = ObservableBuffer.empty[TLogMessage]
  private val logTable = buildTable()
  private val logIcons = new LogIcons()

  JFXUtil.onAction(logTable, showMessageInDialogBox(_))

/* builds table with the given columns with the possibility to check the rows and to select multiple rows */ 
  private def buildTable() = new TableView[TLogMessage](buffer) {
    columns ++= List ( iconColumn(), textColumn())
    editable = true
    placeholder = Label("") // prevent "no content in table" message to appear when the table is empty
    columnResizePolicy = TableView.ConstrainedResizePolicy
    contextMenu = new ContextMenu(ClipboardMenuMaker.buildClipboardMenu("Message", () => selectionToString()))
    stylesheets += "loglist.css"
  }

  /* build the column on the left, that shows the icon (error, warn, info) */
  private def iconColumn() = new TableColumn[TLogMessage, Image] {
    cellValueFactory = { msg => ObjectProperty(logIcons.iconForMessage(msg.value)) }
    cellFactory = {
      _ : TableColumn[TLogMessage, Image] => new TableCell[TLogMessage, Image] {
        item.onChange {
          (_, _, newImage) => graphic = new ImageView(newImage)
        }
      }
    }
    maxWidth = 24
    minWidth = 24
  }

  /* build the column on the right, that shows the message text */
  private def textColumn() = new TableColumn[TLogMessage, String] {
    cellValueFactory = { x => new StringProperty(LogText.extractLogMessage(x.value)) }
    resizable = true
  }

  /* when you double-click on a line it shows the whole message in a dialog box */ 
  private def showMessageInDialogBox(selectedMessage : TLogMessage) : Unit = 
      new Alert(AlertType.Information) { 
         headerText="Message"
         contentText= "Details:"
         dialogPane().content =  new TextArea {
              text = LogText.extractWholeLogText(selectedMessage)
              editable = false
              wrapText = true
            } 
         }.showAndWait()

  /* converts the selected part of the table to a string that can be written to the clipboard */
  private def selectionToString() : String = 
      LogText.extractWholeLogText(logTable.selectionModel().selectedItem())

  /* Prepends: the last message come becomes the first in the list */
  def addLogMessage(log :TLogMessage) : Unit = 
    log +=: buffer

  def control : Parent = logTable 
}


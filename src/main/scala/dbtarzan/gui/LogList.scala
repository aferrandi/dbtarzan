package dbtarzan.gui

import dbtarzan.gui.interfaces.{TControlBuilder, TLogs}
import dbtarzan.gui.util.{JFXUtil, LogIcons}
import dbtarzan.localization.Localization
import dbtarzan.messages.{LogText, TLogMessage}
import scalafx.Includes._
import scalafx.beans.property.{ObjectProperty, StringProperty}
import scalafx.collections.ObservableBuffer
import scalafx.event.ActionEvent
import scalafx.scene.Parent
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.TableColumn._
import scalafx.scene.control._
import scalafx.scene.image.{Image, ImageView}

import java.time.format.DateTimeFormatter

/**
  A list of the errors happened in the application, last error first
*/
class LogList(localization : Localization) extends TLogs with TControlBuilder {
  private val buffer = ObservableBuffer.empty[TLogMessage]
  private val logTable = buildTable()
  private val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")

  JFXUtil.onAction(logTable, (selectedMessage : TLogMessage, _) => showMessageInDialogBox(selectedMessage))

/* builds table with the given columns with the possibility to check the rows and to select multiple rows */ 
  private def buildTable() = new TableView[TLogMessage](buffer) {
    columns ++= List ( iconColumn(), producedColumn(), textColumn())
    editable = true
    placeholder = Label("") // prevent "no content in table" message to appear when the table is empty
    columnResizePolicy = TableView.ConstrainedResizePolicy
    contextMenu = new ContextMenu(new MenuItem(localization.copyMessageToClipboard) {
            onAction = (_: ActionEvent) =>  try {
              JFXUtil.copyTextToClipboard(selectionToString())
              println("Message copied")
            } catch {
              case ex : Exception => println("Copying message to the clipboard got "+ex)
            }
          })
    stylesheets += "loglist.css"
  }

  /* build the column on the left, that shows the icon (error, warn, info) */
  private def iconColumn() = new TableColumn[TLogMessage, Image] {
    cellValueFactory = { msg => ObjectProperty(LogIcons.iconForMessage(msg.value).delegate) }
    cellFactory = {
      (_ : TableColumn[TLogMessage, Image]) => new TableCell[TLogMessage, Image] {
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

  /* build the column on the center, that shows the date/time the message was produced */
  private def producedColumn() = new TableColumn[TLogMessage, String] {
    cellValueFactory = { x => new StringProperty(formatter.format(x.value.produced)) }
    resizable = true
    maxWidth = 96
    minWidth = 96
  }


  /* when you double-click on a line it shows the whole message in a dialog box */ 
  private def showMessageInDialogBox(selectedMessage : TLogMessage) : Unit = 
      new Alert(AlertType.Information) { 
         headerText = localization.message
         contentText= localization.details+":"
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
  def addLogMessage(log :TLogMessage) : Unit = {
    println("Log:"+log)
    log +=: buffer
  }

  def control : Parent = logTable 
}


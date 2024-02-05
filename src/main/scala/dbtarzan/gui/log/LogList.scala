package dbtarzan.gui.log

import dbtarzan.gui.interfaces.{TControlBuilder, TLogs}
import dbtarzan.gui.util.{DateUtils, JFXUtil, LogIcons}
import dbtarzan.localization.Localization
import dbtarzan.messages.{LogText, TLogMessageGUI}
import scalafx.Includes.*
import scalafx.beans.property.{ObjectProperty, StringProperty}
import scalafx.collections.ObservableBuffer
import scalafx.event.ActionEvent
import scalafx.scene.Parent
import scalafx.scene.control.*
import scalafx.scene.control.TableColumn.*
import scalafx.scene.image.{Image, ImageView}

import java.time.format.DateTimeFormatter



/**
 * A list of the errors happened in the application, last error first
*/
class LogList(localization : Localization) extends TLogs with TControlBuilder {
  private val buffer = ObservableBuffer.empty[TLogMessageGUI]
  private val logTable = buildTable()
  private val formatter = DateUtils.timeFormatter()

  JFXUtil.onAction(logTable, (selectedMessage : TLogMessageGUI, _) => LogDialog.showMessageInDialogBox(localization, selectedMessage))

/* builds table with the given columns with the possibility to check the rows and to select multiple rows */ 
  private def buildTable() = new TableView[TLogMessageGUI](buffer) {
    columns ++= List ( iconColumn(), producedColumn(), textColumn())
    editable = true
    placeholder = Label("") // prevent "no content in table" message to appear when the table is empty
    columnResizePolicy = TableView.ConstrainedResizePolicy
    contextMenu = new ContextMenu(new MenuItem(localization.copyMessageToClipboard) {
            onAction = (_: ActionEvent) =>  try {
              JFXUtil.copyTextToClipboard(selectionToString())
              // println("Message copied")
            } catch {
              case ex : Exception => println("Copying message to the clipboard got "+ex)
            }
          })
    stylesheets += "loglist.css"
  }

  /* build the column on the left, that shows the icon (error, warn, info) */
  private def iconColumn() = new TableColumn[TLogMessageGUI, Image] {
    cellValueFactory = { msg => ObjectProperty(LogIcons.iconForMessage(msg.value).delegate) }
    cellFactory = {
      (_ : TableColumn[TLogMessageGUI, Image]) => new TableCell[TLogMessageGUI, Image] {
        item.onChange {
          (_, _, newImage) => graphic = new ImageView(newImage)
        }
      }
    }
    maxWidth = 24
    minWidth = 24
  }

  /* build the column on the right, that shows the message text */
  private def textColumn() = new TableColumn[TLogMessageGUI, String] {
    cellValueFactory = { x => new StringProperty(LogText.extractLogMessage(x.value)) }
    resizable = true
  }

  /* build the column on the center, that shows the date/time the message was produced */
  private def producedColumn() = new TableColumn[TLogMessageGUI, String] {
    cellValueFactory = { x => new StringProperty(formatter.format(x.value.produced)) }
    resizable = true
    maxWidth = 96
    minWidth = 96
  }

  /* converts the selected part of the table to a string that can be written to the clipboard */
  private def selectionToString() : String = 
      LogText.extractWholeLogText(logTable.selectionModel().selectedItem())

  /* Prepends: the last message come becomes the first in the list */
  def addLogMessage(log :TLogMessageGUI) : Unit = {
    log +=: buffer
  }

  def control : Parent = logTable 
}


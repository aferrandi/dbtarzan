package dbtarzan.gui.log

import dbtarzan.gui.util.DateUtils
import dbtarzan.gui.util.LogIcons.alertType
import dbtarzan.localization.Localization
import dbtarzan.messages.{LogText, TLogMessage}
import scalafx.Includes.*
import scalafx.beans.property.StringProperty
import scalafx.scene.control.{Alert, ButtonType, TextArea, ToggleButton}
import scalafx.scene.layout.BorderPane

class LogDialog(localization: Localization, msg: TLogMessage) {
  private val printedText = new StringProperty(makePrintedText(false))
  private val watch = "\u23F1"
  private def makePrintedText(advanced: Boolean): String = {
    if (advanced)
      LogText.extractWholeLogText(msg)
    else
      LogText.extractLogMessage(msg)
  }

  /* when you double-click on a line it shows the whole message in a dialog box */
  def showMessageInDialogBox(): Unit =
    new Alert(alertType(msg), localization.message, ButtonType.OK) {
      headerText = s"$watch ${DateUtils.timeFormatter().format(msg.produced)}"
      dialogPane().content = new BorderPane() {
        center = new TextArea() {
              text <==> printedText
              editable = false
              wrapText = true
            }
        bottom =  new ToggleButton() {
            text = localization.advanced
            selected.onChange((_, _, newValue) => {
                printedText.value = makePrintedText(newValue)
              })
        }
        resizable = true
      }
    }.showAndWait()
}

object LogDialog {
  def showMessageInDialogBox(localization: Localization, msg: TLogMessage): Unit = {
   new LogDialog(localization: Localization, msg: TLogMessage).showMessageInDialogBox()
  }
}

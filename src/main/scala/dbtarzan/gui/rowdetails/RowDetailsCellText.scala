package dbtarzan.gui.rowdetails

import dbtarzan.db.{Field, FieldType}
import dbtarzan.gui.util.JFXUtil
import dbtarzan.localization.Localization
import scalafx.scene.Node
import scalafx.scene.control.*
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.layout.{BorderPane, VBox}
import scalafx.Includes._


class RowDetailsCellText(field: Field, localization: Localization) extends TRowDetailsCell {
  private var currentText = ""
  /* true if the field has been recognized as multiline text, so we don't need to check again if it is multilone */
  private var textControl: TextInputControl = new TextField() {
    editable = false
  }

  private val expandMenu: MenuItem = JFXUtil.menuItem(localization.expand, () => expand())

  private val mainControl = new BorderPane {
    center = textControl
    right = new MenuBar {
      menus = List(new Menu(JFXUtil.threeLines) {
        items = List(
          expandMenu,
          JFXUtil.menuItem(localization.open, () => open()),
          JFXUtil.menuItem(localization.download, () => download())
        )
        stylesheets += "orderByMenuBar.css"
      })
    }
  }

  private def expand(): Unit = {
    expandMenu.disable = true
    val newTextControl = new TextArea() {
      editable = false
      text = currentText
    }
    mainControl.center = newTextControl
    textControl = newTextControl
  }

  private def open(): Unit = {
    new Alert(AlertType.Information) {
      headerText = field.name
      contentText = localization.details + ":"
      dialogPane().content = new TextArea {
        text = currentText
        editable = false
        wrapText = true
      }
    }.showAndWait()
  }

  private def download(): Unit = {

  }


  def showText(value : String|Int|Double) : Unit = {
    currentText = Option(value).map(_.toString).getOrElse("")
    textControl.text = currentText
  }

  override def control: Node = mainControl
}
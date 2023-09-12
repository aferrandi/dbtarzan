package dbtarzan.gui.rowdetails

import dbtarzan.db.{Field, FieldType}
import dbtarzan.gui.util.JFXUtil
import dbtarzan.localization.Localization
import dbtarzan.types.Binaries.Binary
import scalafx.Includes.*
import scalafx.scene.Node
import scalafx.scene.control.*
import scalafx.scene.layout.BorderPane
import scalafx.stage.FileChooser

import java.io.{FileOutputStream, FileWriter}
import scala.util.Using


class RowDetailsCellText(field: Field, localization: Localization) extends TRowDetailsCell {
  private var currentValueMaybe: Option[String|Binary] = None
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
      text = valueAsText()
    }
    mainControl.center = newTextControl
    textControl = newTextControl
  }

  private def open(): Unit = {
    new Dialog[Unit]() {
      title = s"${localization.field} ${field.name}"
      dialogPane().content = new TextArea() {
          text = valueAsText()
          editable = false
          wrapText = true
        }
        resizable = true
        dialogPane().buttonTypes = Seq(ButtonType.OK)
      }.showAndWait()
  }



  private def download(): Unit = {
    currentValueMaybe match {
      case Some(value) => {
        val fileChooser = new FileChooser() {
          title = localization.download
        }
        val file = fileChooser.showSaveDialog(mainControl.scene().window())
        if (file != null)
          FileDownload.downloadData(file, field, value)
      }
      case _ => throw new RuntimeException(s"No data to store of type ${field.fieldType} in field ${field.name}")
    }
  }

  def showText(value : String|Int|Double|Binary) : Unit = {
    currentValueMaybe = Option(value.asInstanceOf[String|Binary])
    textControl.text = valueAsText()
  }

  private def valueAsText(): String = {
    field.fieldType match {
      case FieldType.STRING => currentValueMaybe.map(_.asInstanceOf[String]).getOrElse("")
      case FieldType.BINARY => currentValueMaybe.map(_.asInstanceOf[Binary].asString).getOrElse("")
      case _ => throw new RuntimeException(s"Field type not recognized ${field.fieldType} in field ${field.name}")
    }
  }

  override def control: Node = mainControl
}
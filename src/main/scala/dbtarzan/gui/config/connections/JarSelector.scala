package dbtarzan.gui.config.connections

import dbtarzan.gui.interfaces.TControlBuilder
import scalafx.scene.control.{Button, TextField}
import scalafx.scene.layout.{ColumnConstraints, GridPane, Priority}
import scalafx.scene.Parent
import scalafx.stage.FileChooser
import scalafx.stage.FileChooser.*
import scalafx.event.ActionEvent
import scalafx.Includes.*

import java.io.File
import dbtarzan.localization.Localization

object JarSelector {
  def normalizeWindowsPath(path: String): String =
    path.replace("\\", "/")
}

/* The list of database to choose from*/
class JarSelector(localization : Localization) extends TControlBuilder {
  private var optJarFilePath : Option[String] = None
  private val txtJar = new TextField {
    text = ""
  } 
  private val btnFile = new Button {
    text = "..."
    onAction = (_: ActionEvent)  => { chooseFile() }
  }
 
  private val grid =  new GridPane {
    columnConstraints = List(
      new ColumnConstraints() {
        hgrow = Priority.Always
      },
      new ColumnConstraints() {
        
      })
    add(txtJar, 0, 0)
    add(btnFile, 1, 0)
    vgap = 10
    hgap = 10
  }

  def chooseFile() : Unit = {
      val fileChooser = new FileChooser() {
        title = localization.jdbcUrlStrings
        extensionFilters.add(new ExtensionFilter(localization.jarFiles+" (*.jar)", "*.jar"))
      }
      optJarFilePath.map(new File(_).getParentFile()).foreach(fileChooser.initialDirectory = _)

      val optSelectedJar = Option(fileChooser.showOpenDialog(grid.scene().window()))

      optSelectedJar.foreach(file =>
        txtJar.text = JarSelector.normalizeWindowsPath(file.getPath)
      )
  }

  private def isWindows: Boolean =
    Option(System.getProperty("os.name")) match {
      case Some(osName) => osName.toLowerCase().contains("windows")
      case None => false
    }

  def jarFilePath() : String = txtJar.text()

  def show(optJarFilePath : Option[String]) : Unit = {
    this.optJarFilePath = optJarFilePath
    optJarFilePath.foreach(jarFilePath => 
      txtJar.text = jarFilePath
    )
  }

  def onChange[J1 >: String](op: => Unit): Unit = txtJar.text.onChange(op)

  def control : Parent = grid
}


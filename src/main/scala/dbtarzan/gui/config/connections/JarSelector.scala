package dbtarzan.gui.config.connections

import scalafx.scene.control.{ TextField, Button }
import scalafx.scene.layout.{ GridPane, ColumnConstraints, Priority }
import scalafx.scene.Parent
import scalafx.stage.FileChooser
import scalafx.stage.FileChooser._
import scalafx.event.ActionEvent
import scalafx.Includes._
import java.io.File
import dbtarzan.gui.TControlBuilder
import dbtarzan.localization.Localization

/* The list of database to choose from*/
class JarSelector(localization : Localization) extends TControlBuilder {
  var optJarFilePath : Option[String] = None
  val txtJar = new TextField {
    text = ""
  } 
  val btnFile = new Button {
    text = "..."
    onAction = (event: ActionEvent)  => { chooseFile() }
  }
 
  private val grid =  new GridPane {
    columnConstraints = List(
      new ColumnConstraints() {
        hgrow = Priority.ALWAYS
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
        txtJar.text = file.getPath()
      )
  }

  def jarFilePath() : String = txtJar.text()

  def show(optJarFilePath : Option[String]) : Unit = {
    this.optJarFilePath = optJarFilePath
    optJarFilePath.foreach(jarFilePath => 
      txtJar.text = jarFilePath
    )
  }

  def onChange[J1 >: String](op: ⇒ Unit): Unit = txtJar.text.onChange(op)

    def control : Parent = grid
}


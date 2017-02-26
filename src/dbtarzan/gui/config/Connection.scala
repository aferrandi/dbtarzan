package dbtarzan.gui.config

import scalafx.scene.control.{ TextField, Label }
import scalafx.scene.layout.{ GridPane, ColumnConstraints, Priority }
import scalafx.scene.Parent
import scalafx.geometry.Insets
import scalafx.Includes._
import dbtarzan.gui.util.OnChangeSafe
import dbtarzan.gui.TControlBuilder
import dbtarzan.config.ConnectionData

/**
  The list of database to choose from
*/
class Connection() extends TControlBuilder {
  val safe = new OnChangeSafe()
  val txtName = new TextField {
    text = ""
  } 
  val jarSelector = new JarSelector()
  val txtUrl = new TextField {
    text = ""
  }
  val txtDriver = new TextField {
    text = ""
  }
  val txtUser = new TextField {
    text = ""
  }  
  val txtPassword = new TextField {
    text = ""
  }
  val txtSchema = new TextField {
    text = ""
  }
 
  val cmbDelimiters = new ComboDelimiters()

  private val grid =  new GridPane {
    columnConstraints = List(
      new ColumnConstraints() {},
      new ColumnConstraints() {
        hgrow = Priority.ALWAYS
      })
    add(new Label { text = "Name:" }, 0, 0)
    add(txtName, 1, 0)
    add(new Label { text = "Jar:" }, 0, 1)
    add(jarSelector.control, 1, 1)
    add(new Label { text = "Url:" }, 0, 2)
    add(txtUrl, 1, 2)
    add(new Label { text = "Driver:" }, 0, 3)
    add(txtDriver, 1, 3)
    add(new Label { text = "User:" }, 0, 4)
    add(txtUser, 1, 4)    
    add(new Label { text = "Password:" }, 0, 5)
    add(txtPassword, 1, 5)
    add(new Label { text = "Schema:" }, 0, 6)
    add(txtSchema, 1, 6)
    add(new Label { text = "Delimiters:" }, 0, 7)
    add(cmbDelimiters.control, 1, 7)
    padding = Insets(10)
    vgap = 10
    hgap = 10
  }

  def show(data : ConnectionData) : Unit = safe.noChangeEventDuring(() => {
    txtName.text = data.name
    jarSelector.show(Some(data.jar))
    txtUrl.text = data.url
    txtDriver.text = data.driver
    txtUser.text = data.user
    txtPassword.text = data.password
    txtSchema.text = data.schema.getOrElse("")
    cmbDelimiters.show(data.identifierDelimiters); 
  })

  def toData() = {
    ConnectionData(
      jarSelector.jarFilePath(), 
      txtName.text(), 
      txtDriver.text(), 
      txtUrl.text(),
      if(!txtSchema.text().isEmpty) Some(txtSchema.text()) else None,
      txtUser.text(), 
      txtPassword.text(),
      None,
      cmbDelimiters.toDelimiters()    
  )}

  def control : Parent = grid

  def onChanged(useData : ConnectionData => Unit) : Unit = {  
    List(
      txtName.text,
      txtDriver.text,
      txtUrl.text,
      txtSchema.text,
      txtUser.text,
      txtPassword.text
    ).foreach(_.onChange(safe.onChange(() => useData(toData()))))
    jarSelector.onChange(safe.onChange(() => useData(toData())))
    cmbDelimiters.onChanged(() => safe.onChange(() => useData(toData())))
  }
}


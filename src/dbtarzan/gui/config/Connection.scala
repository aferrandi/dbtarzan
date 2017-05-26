package dbtarzan.gui.config

import scalafx.scene.control.{ TextField, Label, Spinner, PasswordField }
import scalafx.scene.layout.{ GridPane, ColumnConstraints, Priority }
import scalafx.scene.Parent
import scalafx.geometry.Insets
import scalafx.Includes._
import dbtarzan.gui.util.OnChangeSafe
import dbtarzan.gui.TControlBuilder
import dbtarzan.config.{ ConnectionData, PasswordEncryption }

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
  val txtPassword = new PasswordField {
    text = ""
  }
  val txtSchema = new TextField {
    text = ""
  }
 
  val cmbDelimiters = new ComboDelimiters()

  val txtMaxRows = new TextField {
    text = ""
    /* only digits allowed (or empty string) */
    text.onChange { (_, oldValue, newValue) => {
         if (!isAllDigits(newValue))
            text = oldValue
      }}
   }

    def isAllDigits(x: String) = x forall Character.isDigit

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
    add(new Label { text = "Max Rows:" }, 0, 8)
    add(txtMaxRows, 1, 8)
    padding = Insets(10)
    vgap = 10
    hgap = 10
  }

  private def decryptPasswordIfNeeded(password: String, passwordEncrypted : Boolean) : String =
      if(passwordEncrypted)
        try { 
          PasswordEncryption.decrypt(password)
        } catch {
          case ex: Exception => throw new Exception("Decrypting the password "+password+" got", ex) 
        }
      else
        password

  def show(data : ConnectionData) : Unit = safe.noChangeEventDuring(() => {
    txtName.text = data.name
    jarSelector.show(Some(data.jar))
    txtUrl.text = data.url
    txtDriver.text = data.driver
    txtUser.text = data.user
    txtPassword.text = decryptPasswordIfNeeded(data.password, data.passwordEncrypted.getOrElse(false))
    txtSchema.text = noneToEmpty(data.schema)
    cmbDelimiters.show(data.identifierDelimiters)
    txtMaxRows.text = noneToEmpty(data.maxRows.map(_.toString))
  })

  private def noneToEmpty(optS : Option[String]) : String = 
    optS.getOrElse("")
  private def emptyToNone(s : String) : Option[String] =
    Option(s).filter(_.trim.nonEmpty)

  private def encryptPassword(password: String) : String =
    try { 
      PasswordEncryption.encrypt(password)
    } catch {
      case ex: Exception => throw new Exception("Encrypting the password "+password+" got", ex) 
    }


  def toData() = {
    ConnectionData(
      jarSelector.jarFilePath(), 
      txtName.text(), 
      txtDriver.text(), 
      txtUrl.text(),
      emptyToNone(txtSchema.text()),
      txtUser.text(), 
      encryptPassword(txtPassword.text()),
      Some(true),
      None,
      cmbDelimiters.toDelimiters(),
      emptyToNone(txtMaxRows.text()).map(_.toInt) // it can only be None or Int
  )}

  def control : Parent = grid

  def onChanged(useData : ConnectionData => Unit) : Unit = {  
    List(
      txtName.text,
      txtDriver.text,
      txtUrl.text,
      txtSchema.text,
      txtUser.text,
      txtPassword.text,
      txtMaxRows.text
    ).foreach(_.onChange(safe.onChange(() => useData(toData()))))
    jarSelector.onChange(safe.onChange(() => useData(toData())))
    cmbDelimiters.onChanged(() => safe.onChange(() => useData(toData())))
  }
}


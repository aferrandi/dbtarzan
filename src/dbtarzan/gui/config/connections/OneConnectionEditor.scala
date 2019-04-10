package dbtarzan.gui.config.connections

import scalafx.scene.control.{ TextField, Label, PasswordField, Hyperlink, CheckBox }
import scalafx.scene.layout.{ GridPane, ColumnConstraints, Priority }
import scalafx.scene.Parent
import scalafx.event.ActionEvent
import scalafx.geometry.{ Insets, HPos }
import scalafx.Includes._

import dbtarzan.gui.util.{ OnChangeSafe, JFXUtil }
import dbtarzan.gui.TControlBuilder
import dbtarzan.config.connections.ConnectionData
import dbtarzan.config.{ EncryptionKey, PasswordEncryption, Password }
import dbtarzan.localization.Localization

/* The list of database to choose from */
class OneConnectionEditor(
  openWeb : String => Unit, 
  encryptionKey : EncryptionKey,
  localization: Localization
  ) extends TControlBuilder {
  val passwordEncryption = new PasswordEncryption(encryptionKey)
  val safe = new OnChangeSafe()
  val txtName = new TextField {
    text = ""
  } 
  val jarSelector = new JarSelector(localization)
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
   val txtCatalog = new TextField {
    text = ""
  }
  val chkAdvanced = new CheckBox {
    text = localization.advanced
    selected.onChange((_, _, newValue) => changeAdvancedVisibility(newValue))
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

  val lblDelimiters = new Label { text = localization.delimiters+":" }
  val lblMaxRows = new Label { text = localization.maxRows+":" }
  val lblCatalog = new Label { text = localization.catalog+":" }   
  val linkToJdbcUrls = new Hyperlink {
    text = "Jdbc connections url strings"
    onAction = (event: ActionEvent)  => openWeb("https://vladmihalcea.com/jdbc-driver-connection-url-strings/")
  }
    def isAllDigits(x: String) = x forall Character.isDigit

  private val grid =  new GridPane {
    columnConstraints = List(
      new ColumnConstraints() {},
      new ColumnConstraints() {
        hgrow = Priority.ALWAYS
      })
    add(new Label { text = localization.name+":" }, 0, 0)
    add(txtName, 1, 0)
    add(new Label { text = "Jar:" }, 0, 1)
    add(jarSelector.control, 1, 1)
    add(new Label { text = "Url:" }, 0, 2)
    add(txtUrl, 1, 2)
    add(new Label { text = "Driver:" }, 0, 3)
    add(txtDriver, 1, 3)
    add(new Label { text = localization.user+":" }, 0, 4)
    add(txtUser, 1, 4)    
    add(new Label { text = localization.password+":" }, 0, 5)
    add(txtPassword, 1, 5)
    add(new Label { text = localization.schema+":" }, 0, 6)
    add(txtSchema, 1, 6)
    add(chkAdvanced, 0, 7)
    add(lblDelimiters, 0, 8)
    add(cmbDelimiters.control, 1, 8)
    add(lblMaxRows, 0, 9)
    add(txtMaxRows, 1, 9)
    add(lblCatalog, 0, 10)
    add(txtCatalog, 1, 10)    
    add(linkToJdbcUrls, 1, 11)
    GridPane.setHalignment(linkToJdbcUrls, HPos.RIGHT) 
    padding = Insets(10)
    vgap = 10
    hgap = 10
  }

  private def decryptPasswordIfNeeded(password: Password, passwordEncrypted : Boolean) : Password =
      if(passwordEncrypted)
        try { 
          passwordEncryption.decrypt(password)
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
    txtPassword.text = decryptPasswordIfNeeded(data.password, data.passwordEncrypted.getOrElse(false)).key
    txtSchema.text = noneToEmpty(data.schema)
    cmbDelimiters.show(data.identifierDelimiters)
    txtMaxRows.text = noneToEmpty(data.maxRows.map(_.toString))
    txtCatalog.text = noneToEmpty(data.catalog)
    chkAdvanced.selected = false
    changeAdvancedVisibility(false)
  })

  private def noneToEmpty(optS : Option[String]) : String = 
    optS.getOrElse("")
  private def emptyToNone(s : String) : Option[String] =
    Option(s).filter(_.trim.nonEmpty)

  private def changeAdvancedVisibility(visible : Boolean) : Unit = {
    JFXUtil.changeControlsVisibility(visible,
      lblDelimiters,
      cmbDelimiters.control,
      lblMaxRows,
      txtMaxRows,
      lblCatalog,
      txtCatalog
    )
  }

  private def encryptPassword(password: Password) : Password =
    try { 
      passwordEncryption.encrypt(password)
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
      encryptPassword(Password(txtPassword.text())),
      Some(true),
      None,
      cmbDelimiters.toDelimiters(),
      emptyToNone(txtMaxRows.text()).map(_.toInt), // it can only be None or Int
      emptyToNone(txtCatalog.text())
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
      txtMaxRows.text,
      txtCatalog.text
    ).foreach(_.onChange(safe.onChange(() => useData(toData()))))
    jarSelector.onChange(safe.onChange(() => useData(toData())))
    cmbDelimiters.onChanged(() => safe.onChange(() => useData(toData())))
  }
}


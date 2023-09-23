package dbtarzan.gui.config.connections

import scalafx.scene.control.{Button, CheckBox, Hyperlink, Label, PasswordField, TextField}
import scalafx.scene.layout.{ColumnConstraints, GridPane, HBox, Priority}
import scalafx.scene.Parent
import scalafx.event.ActionEvent
import scalafx.geometry.{HPos, Insets}
import scalafx.Includes._
import dbtarzan.gui.util.{JFXUtil, OnChangeSafe, StringUtil}
import dbtarzan.config.connections.ConnectionData
import dbtarzan.config.password.{EncryptionKey, Password, PasswordEncryption}
import dbtarzan.db.{SchemaName}
import dbtarzan.gui.OpenWeb
import dbtarzan.gui.interfaces.TControlBuilder
import dbtarzan.localization.Localization

/* The editor for one single connection */
class OneConnectionEditor(
  encryptionKey : EncryptionKey,
  localization: Localization
  ) extends TControlBuilder {
  val passwordEncryption = new PasswordEncryption(encryptionKey)
  val safe = new OnChangeSafe()
  private val txtName = new TextField {
    text = ""
  }
  private val jarSelector = new JarSelector(localization)
  private val txtUrl = new TextField {
    text = ""
  }
  private val txtDriver = new TextField {
    text = ""
  }
  private val txtUser = new TextField {
    text = ""
  }
  private val txtPassword = new PasswordField {
    text = ""
  }
  private val cmbSchemas = new ComboSchemas()
  private val btnSchemaChoices =   new Button {
    text = localization.choices
  }
  private val txtCatalog = new TextField {
    text = ""
  }
  private val chkAdvanced = new CheckBox {
    text = localization.advanced
    selected.onChange((_, _, newValue) => changeAdvancedVisibility(newValue))
  }

  private val cmbDelimiters = new ComboDelimiters()
  private val txtMaxRows = JFXUtil.numTextField()
  private val txtQueryTimeoutInSeconds = JFXUtil.numTextField()
  private val txtMaxFieldSize = JFXUtil.numTextField()

  private val lblDelimiters = new Label { text = localization.delimiters+":" }
  private val lblMaxRows = new Label { text = localization.maxRows+":" }
  private val lblQueryTimeoutInSeconds = new Label { text = localization.queryTimeoutInSeconds+":" }
  private val lblMaxFieldSize = new Label { text = localization.maxFieldSize+":" }
  private val lblCatalog = new Label { text = localization.catalog+":" }
  private val linkToJdbcUrls = new Hyperlink {
    text = "Jdbc connections url strings"
    onAction = (_: ActionEvent)  => OpenWeb.openWeb("https://vladmihalcea.com/jdbc-driver-connection-url-strings/")
  }

  private val grid =  new GridPane {
    columnConstraints = List(
      new ColumnConstraints() {},
      new ColumnConstraints() {
        hgrow = Priority.Always
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
    add(new HBox {
      children = List(cmbSchemas.control, btnSchemaChoices)
      spacing = 10
    }, 1, 6)
    add(chkAdvanced, 0, 7)
    add(lblDelimiters, 0, 8)
    add(cmbDelimiters.control, 1, 8)
    add(lblMaxRows, 0, 9)
    add(new HBox { children = List(txtMaxRows)}, 1, 9)
    add(lblQueryTimeoutInSeconds, 0, 10)
    add(new HBox { children = List(txtQueryTimeoutInSeconds)}, 1, 10)
    add(lblMaxFieldSize, 0, 11)
    add(new HBox { children = List(txtMaxFieldSize)}, 1, 11)
    add(lblCatalog, 0, 12)
    add(txtCatalog, 1, 12)
    add(linkToJdbcUrls, 1, 13)
    GridPane.setHalignment(linkToJdbcUrls, HPos.Right)
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
    cmbSchemas.show(data.schema)
    cmbSchemas.clearSchemasToChooseFrom()
    cmbDelimiters.show(data.identifierDelimiters)
    txtMaxRows.fromOptInt(data.maxRows)
    txtQueryTimeoutInSeconds.fromOptInt(data.queryTimeoutInSeconds)
    txtMaxFieldSize.fromOptInt(data.maxFieldSize)
    txtCatalog.text = StringUtil.noneToEmpty(data.catalog)
    chkAdvanced.selected = false
    changeAdvancedVisibility(false)
  })

  private def changeAdvancedVisibility(visible : Boolean) : Unit = {
    JFXUtil.changeControlsVisibility(visible,
      lblDelimiters,
      cmbDelimiters.control,
      lblMaxRows,
      txtMaxRows,
      lblQueryTimeoutInSeconds,
      txtQueryTimeoutInSeconds,
      lblMaxFieldSize,
      txtMaxFieldSize,
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


  def toData: ConnectionData = ConnectionData(
        jarSelector.jarFilePath(),
        txtName.text(),
        txtDriver.text(),
        txtUrl.text(),
        cmbSchemas.chosenSchema(),
        txtUser.text(),
        encryptPassword(Password(txtPassword.text())),
        Some(true),
        None,
        cmbDelimiters.retrieveDelimiters(),
        txtMaxRows.toOptInt,
        txtQueryTimeoutInSeconds.toOptInt,
        txtMaxFieldSize.toOptInt,
        StringUtil.emptyToNone(txtCatalog.text())
    )

  def control : Parent = grid

  def onChanged(useData : ConnectionData => Unit) : Unit = {  
    List(
      txtName.text,
      txtDriver.text,
      txtUrl.text,
      txtUser.text,
      txtPassword.text,
      txtMaxRows.text,
      txtQueryTimeoutInSeconds.text,
      txtMaxFieldSize.text,
      txtCatalog.text
    ).foreach(_.onChange(safe.onChange(() => useData(toData))))
    jarSelector.onChange(safe.onChange(() => useData(toData)))
    List(
      cmbDelimiters,
      cmbSchemas
    ).foreach(_.onChanged(() => safe.onChange(() => useData(toData))))
  }

  def schemasToChooseFrom(schemas: List[SchemaName]) : Unit =
    cmbSchemas.schemasToChooseFrom(schemas)


  def onSchemasLoad(action : () => Unit ): Unit =
    btnSchemaChoices.onAction = (_: ActionEvent)  => action()
}


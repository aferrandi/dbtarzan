package dbtarzan.gui.config.connections

import dbtarzan.config.connections.ConnectionData
import dbtarzan.config.password.{EncryptionKey, Password}
import dbtarzan.db.SimpleDatabaseId
import dbtarzan.gui.interfaces.TControlBuilder
import dbtarzan.gui.login.PasswordDialog
import dbtarzan.gui.util.JFXUtil
import dbtarzan.localization.Localization
import dbtarzan.log.actor.Logger
import dbtarzan.messages.{ExceptionText, ResponseSchemaExtraction, ResponseTestConnection}
import scalafx.scene.Parent
import scalafx.scene.control.SplitPane
import scalafx.scene.layout.BorderPane

/* table + constraint input box + foreign keys */
class ConnectionEditor(
  connectionDatas : List[ConnectionData],
  encryptionKey : EncryptionKey,
  localization: Localization,
  log: Logger
  ) extends TControlBuilder {
  private val list = new ConnectionList(connectionDatas, localization)
  private val connection = new OneConnectionEditor(encryptionKey, localization)
  private val buttons = new ConnectionButtons(localization) 
  private val layout = new BorderPane {
    center = buildSplitPane()
    bottom = buttons.control
  }
  connection.onChanged(list.changeSelected)
  list.onConnectionSelected(showConnection)
  buttons.onNew(() => list.addNew())
  buttons.onRemove(() => list.removeCurrent())
  buttons.onDuplicate(() => list.duplicateCurrent())
  list.selectFirst()

    /* builds the split panel containing the table and the foreign keys list */
  private def buildSplitPane() = new SplitPane {
    maxHeight = Double.MaxValue
    maxWidth = Double.MaxValue
    items.addAll(list.control, connection.control)
    dividerPositions = 0.3
    SplitPane.setResizableWithParent(list.control, value = false)
  }

  private def showConnection(data : ConnectionData) : Unit = try {
      connection.show(data)
    } catch {
      case ex : Exception => JFXUtil.showErrorAlert(localization.errorDisplayingConnections+": ", ex.getMessage)
    } 

  private def saveIfPossible(save : List[ConnectionData]  => Unit) : Unit = {
    val errors = list.validate()
    if(errors.isEmpty) {
      if(JFXUtil.areYouSure(localization.areYouSureSaveConnections, localization.saveConnections))
        try { save(list.content()) } 
        catch {
          case ex : Exception => JFXUtil.showErrorAlert(localization.errorSavingConnections+": ", ex.getMessage)
        }
    } else
      showConnectionDataErrors(errors)
  }

  def cancelIfPossible(cancel : () => Unit) : Unit =
    if(JFXUtil.areYouSure(localization.areYouSureClose, localization.cancel))
        cancel()

  private def showConnectionDataErrors(errors : List[ConnectionDataErrors]) : Unit = {
    val errorText = errors.map(error => error.name + ":" + error.errors.mkString(",")).mkString(";")
    log.error(errorText)
    JFXUtil.showErrorAlert(localization.errorSavingConnections+": ", errorText)
  }

  def onTestConnection(test : (ConnectionData, Option[Password])  => Unit): Unit =
    buttons.onTest(() => {
      connection.toData.password match
        case Some(password) => test(connection.toData, None)
        case None =>
          PasswordDialog.show(localization, List(SimpleDatabaseId(connection.toData.name))).foreach(loginPassword =>
            test(connection.toData, Some(loginPassword.loginPasswords.head._2))
          )
      })

  def onSave(save : List[ConnectionData]  => Unit): Unit =
    buttons.onSave(() => saveIfPossible(save))

  def onCancel(cancel : ()  => Unit): Unit =
    buttons.onCancel(() => cancelIfPossible(cancel))

  def onSchemasLoad(schemasLoad : (ConnectionData, Option[Password])  => Unit): Unit =
    connection.onSchemasLoad(() => connection.toData.password match {
      case Some(password) => schemasLoad(connection.toData, None)
      case None => schemasLoad(connection.toData, PasswordDialog.show(localization, List(SimpleDatabaseId(connection.toData.name))).map(_.loginPasswords.head._2))
    })

  def testConnectionResult(rsp : ResponseTestConnection) : Unit =
    rsp.ex match {
      case Some(ex) => JFXUtil.showErrorAlert(localization.connectionRefused, ExceptionText.extractMessageText(ex))
      case None => JFXUtil.showInfoAlert(localization.connectionSuccessful, localization.connectionToDatabaseSuccesful(rsp.data.name))
    }

  def schemaExtractionResult(rsp: ResponseSchemaExtraction): Unit =
    rsp.schemas match {
      case Some(schemas)  => connection.schemasToChooseFrom(schemas.names)
      case None => rsp.ex match {
        case Some(ex) => JFXUtil.showErrorAlert(localization.connectionRefused, ExceptionText.extractMessageText(ex))
        case None =>   JFXUtil.showErrorAlert(localization.connectionRefused, "")
      }
    }

  def control : Parent = layout
}
package dbtarzan.gui.config

import scalafx.scene.control.{ TableView, SplitPane, Button, Alert, ButtonType }
import scalafx.scene.layout.BorderPane
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.Parent
import dbtarzan.config.ConnectionData
import dbtarzan.gui.TControlBuilder

/**
  table + constraint input box + foreign keys
*/
class ConnectionEditor(connectionDatas : List[ConnectionData]) extends TControlBuilder {
  private val list = new ConnectionList(connectionDatas)
  private val connection = new Connection()
  private val buttons = new ConnectionButtons() 
  private val layout = new BorderPane {
    center = buildSplitPane()
    bottom = buttons.control
  }
  connection.onChanged(list.changeSelected(_))
  list.onConnectionSelected(connection.show(_))
  buttons.onNew(() => list.addNew())
  buttons.onRemove(() => list.removeCurrent())
  buttons.onCancel(() => list.removeCurrent())
  list.selectFirst()
    /* builds the split panel containing the table and the foreign keys list */
  private def buildSplitPane() = new SplitPane {
        maxHeight = Double.MaxValue
        maxWidth = Double.MaxValue
        items.addAll(list.control, connection.control)
        dividerPositions = 0.3
        SplitPane.setResizableWithParent(list.control, false)
  }

  private def areYouSure(text : String, header: String) = new Alert(AlertType.Confirmation, text, ButtonType.Yes, ButtonType.No ) {
      headerText = header
    }.showAndWait() match {
      case Some(ButtonType.Yes) => true
      case _ => false
    }

  private def saveIfPossible(save : List[ConnectionData]  => Unit) : Unit ={
      val errors = list.validate()
      if(errors.isEmpty) {
        if(areYouSure("Are you sure you want to save the connections?", "Save connections"))
          save(list.content())
      }
      else 
        showErrorAlert(errors)
  }

  def cancelIfPossible(cancel : () => Unit) : Unit = {
      if(areYouSure("Are you sure you want to close without saving?", "Cancel"))
          cancel()
  }

  private def showErrorAlert(errors : List[ConnectionDataErrors]) : Unit = {
    val errorText = errors.map(error => error.name + ":" + error.errors.mkString(",")).mkString(";")
    new Alert(AlertType.Error) { 
         headerText="Saving the connections got: "
         contentText= errorText
         }.showAndWait()
  }

  def onSave(save : List[ConnectionData]  => Unit): Unit =
    buttons.onSave(() => saveIfPossible(save))

  def onCancel(cancel : ()  => Unit): Unit =
    buttons.onCancel(() => cancelIfPossible(cancel))


  def control : Parent = layout
}
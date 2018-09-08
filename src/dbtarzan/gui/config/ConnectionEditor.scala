package dbtarzan.gui.config

import scalafx.scene.control.SplitPane
import scalafx.scene.layout.BorderPane
import scalafx.scene.Parent
import dbtarzan.config.ConnectionData
import dbtarzan.gui.TControlBuilder
import dbtarzan.gui.util.JFXUtil

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
  list.onConnectionSelected(showConnection(_))
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
    SplitPane.setResizableWithParent(list.control, false)
  }

  private def showConnection(data : ConnectionData) : Unit = try {
      connection.show(data)
    } catch {
      case ex : Exception => JFXUtil.showErrorAlert("Displaying connections got: ", ex.getMessage())
    } 

  private def saveIfPossible(save : List[ConnectionData]  => Unit) : Unit = {
    val errors = list.validate()
    if(errors.isEmpty) {
      if(JFXUtil.areYouSure("Are you sure you want to save the connections?", "Save connections"))
        try { save(list.content()) }
        catch {
          case ex : Exception => JFXUtil.showErrorAlert("Saving the connections got: ", ex.getMessage())
        }
    }
    else 
      showConnectionDataErrors(errors)
  }

  def cancelIfPossible(cancel : () => Unit) : Unit = {
    if(JFXUtil.areYouSure("Are you sure you want to close without saving?", "Cancel"))
        cancel()
  }

  private def showConnectionDataErrors(errors : List[ConnectionDataErrors]) : Unit = {
    val errorText = errors.map(error => error.name + ":" + error.errors.mkString(",")).mkString(";")
    JFXUtil.showErrorAlert("Saving the connections got: ", errorText)
  }

  def onSave(save : List[ConnectionData]  => Unit): Unit =
    buttons.onSave(() => saveIfPossible(save))

  def onCancel(cancel : ()  => Unit): Unit =
    buttons.onCancel(() => cancelIfPossible(cancel))

  def control : Parent = layout
}
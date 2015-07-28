package dbtarzan.gui.config

import scalafx.scene.control.{ TableView, SplitPane, Button }
import scalafx.scene.layout.BorderPane
import dbtarzan.config.ConnectionData
import dbtarzan.gui.TControlBuilder
import scalafx.scene.Parent

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
  list.selectFirst()
    /* builds the split panel containing the table and the foreign keys list */
  private def buildSplitPane() =new SplitPane {
        maxHeight = Double.MaxValue
        maxWidth = Double.MaxValue
        items.addAll(list.control, connection.control)
        dividerPositions = 0.3
        SplitPane.setResizableWithParent(list.control, false)
  }

  private def saveIfPossible(save : List[ConnectionData]  => Unit) : Unit ={
      val errors = list.validate()
      if(errors.isEmpty) 
        save(list.content())
      else
        buttons.showErrors(errors.map(error => error.name + ":" + error.errors.mkString(",")).mkString(";"))
  }

  def onSave(save : List[ConnectionData]  => Unit): Unit =
    buttons.onSave(() => saveIfPossible(save))

  def control : Parent = layout
}
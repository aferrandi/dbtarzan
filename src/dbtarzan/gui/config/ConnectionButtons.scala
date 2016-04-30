package dbtarzan.gui.config

import scalafx.scene.layout. { HBox, Region, Priority }
import scalafx.scene.control.{ Button, Label}
import scalafx.event.ActionEvent
import dbtarzan.gui.TControlBuilder
import scalafx.Includes._
import scalafx.scene.Parent
import scalafx.geometry.{ Insets, Pos }


class ConnectionButtons() extends TControlBuilder {
  val buttonNew = new Button {
    text = "New"
  }
  val buttonRemove = new Button {
    text = "Remove"
  }
  val buttonCancel = new Button {
    text = "Cancel"
    alignmentInParent = Pos.CENTER_RIGHT
  }

  val buttonSave = new Button {
    text = "Save"
    alignmentInParent = Pos.CENTER_RIGHT
  }

	private val layout = new HBox {
    children = List(buttonNew, buttonRemove, new Region() { hgrow = Priority.Always }, buttonSave, buttonCancel )
  	padding = Insets(10)
  	spacing = 10
  }
 
  def onNew(action : () => Unit ): Unit = 
    buttonNew.onAction = (event: ActionEvent)  => action()

  def onRemove(action : () => Unit ): Unit =
    buttonRemove.onAction = (event: ActionEvent)  => action()

  def onSave(action : () => Unit ): Unit =
    buttonSave.onAction = (event: ActionEvent)  => action()

  def onCancel(action : () => Unit ): Unit =
    buttonCancel.onAction = (event: ActionEvent)  => action()
  
  def control : Parent = layout
}

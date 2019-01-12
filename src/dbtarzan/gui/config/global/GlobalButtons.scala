package dbtarzan.gui.config.global

import scalafx.scene.layout. { HBox, Region, Priority }
import scalafx.scene.control.Button
import scalafx.event.ActionEvent
import dbtarzan.gui.TControlBuilder
import scalafx.Includes._
import scalafx.scene.Parent
import scalafx.geometry.{ Insets, Pos }


class GlobalButtons() extends TControlBuilder {
  val buttonCancel = new Button {
    text = "Cancel"
    alignmentInParent = Pos.CENTER_RIGHT
  }

  val buttonSave = new Button {
    text = "Save"
    alignmentInParent = Pos.CENTER_RIGHT
  }

	private val layout = new HBox {
    children = List(buttonSave, buttonCancel )
  	padding = Insets(10)
  	spacing = 10
  }
 
  def onSave(action : () => Unit ): Unit =
    buttonSave.onAction = (event: ActionEvent)  => action()

  def onCancel(action : () => Unit ): Unit =
    buttonCancel.onAction = (event: ActionEvent)  => action()
  
  def control : Parent = layout
}

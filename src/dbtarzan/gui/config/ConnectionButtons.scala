package dbtarzan.gui.config

import scalafx.scene.layout.HBox
import scalafx.scene.control.{ Button, Label }
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
    val labelError = new Label()

    val buttonSave = new Button {
          text = "Save"
          alignmentInParent = Pos.CENTER_RIGHT
        }

	private val layout = new HBox {
    content = List(buttonNew, buttonRemove, labelError, buttonSave )
  	padding = Insets(10)
  	spacing = 10
  }
 
  def onNew(action : () => Unit ): Unit = 
	   buttonNew.onAction = (event: ActionEvent)  => action()

  def onRemove(action : () => Unit ): Unit =
      buttonRemove.onAction = (event: ActionEvent)  => action()

  def onSave(action : () => Unit ): Unit =
      buttonSave.onAction = (event: ActionEvent)  => action()
  
  def showErrors(errors : String) : Unit =  
    labelError.text = errors

  def control : Parent = layout
}

package dbtarzan.gui.config.composite

import dbtarzan.gui.interfaces.TControlBuilder
import dbtarzan.localization.Localization
import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Parent
import scalafx.scene.control.Button
import scalafx.scene.layout.{HBox, Priority, Region}


class CompositeButtons(localization: Localization) extends TControlBuilder {
  private val buttonNew = new Button {
    text = localization.new_
  }

  private val buttonRemove = new Button {
    text = localization.remove
  }

  private val buttonCancel = new Button {
    text = localization.cancel
    alignmentInParent = Pos.CenterRight
  }

  private val buttonSave = new Button {
    text = localization.save
    alignmentInParent = Pos.CenterRight
  }

	private val layout = new HBox {
    children = List(
      buttonNew,
      buttonRemove,
      new Region() { hgrow = Priority.Always },
      buttonSave,
      buttonCancel
    )
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

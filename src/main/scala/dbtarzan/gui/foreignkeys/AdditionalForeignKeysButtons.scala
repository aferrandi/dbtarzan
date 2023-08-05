package dbtarzan.gui.foreignkeys

import dbtarzan.gui.interfaces.TControlBuilder
import scalafx.scene.layout. { HBox, Region, Priority }
import scalafx.scene.control.Button
import scalafx.event.ActionEvent
import scalafx.Includes._
import scalafx.scene.Parent
import scalafx.geometry.{ Insets, Pos }
import dbtarzan.localization.Localization


/* the buttons on the bottom of the additinal foreign keys editor, to add foreign keys, to save them and to exit from the editor without saving */
class AdditionalForeignKeysButtons(localization: Localization) extends TControlBuilder {
  private val buttonNew = new Button {
    text = localization.new_
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
    children = List(buttonNew, new Region() { hgrow = Priority.Always }, buttonSave, buttonCancel )
  	padding = Insets(10)
  	spacing = 10
  }
 
  def onNew(action : () => Unit ): Unit = 
    buttonNew.onAction = (_: ActionEvent)  => action()

  def onSave(action : () => Unit ): Unit =
    buttonSave.onAction = (_: ActionEvent)  => action()

  def onCancel(action : () => Unit ): Unit =
    buttonCancel.onAction = (_: ActionEvent)  => action()
  
  def control : Parent = layout
}

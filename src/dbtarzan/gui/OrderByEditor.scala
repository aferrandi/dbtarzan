package dbtarzan.gui.config

import scalafx.scene.control.{ ListView, ListCell, SplitPane, Button, Alert, ButtonType, ComboBox }
import scalafx.scene.layout.{ BorderPane, VBox, HBox, Region, Priority }
import scalafx.scene.control.Alert.AlertType
import scalafx.event.ActionEvent
import scalafx.scene.Parent
import scalafx.geometry.{ Insets, Pos }
import scalafx.collections.ObservableBuffer 
import scalafx.Includes._

import dbtarzan.config.ConnectionData
import dbtarzan.gui.TControlBuilder 
import dbtarzan.db.{ OrderByFields, Field }

/**
  to change the order by columns. A list of order by columns with a panel on the side to change it.
*/
class OrderByEditor(possibleOrderByFields: List[Field], currentOrderBys : Option[OrderByFields]) extends TControlBuilder { 
  private val currentOrderByFields = currentOrderBys.map(_.fields).getOrElse(List.empty[Field])
  private val listBuffer = ObservableBuffer()
  private val comboBuffer = ObservableBuffer(possibleOrderByFields.diff(currentOrderByFields))

  private val layout = new BorderPane {
    center = buildListWithButtons()
    bottom = buildSaveCancelButtons()
  }

  private def buildListWithButtons() = new BorderPane {
    center = buildList()
    right = buildListButtons()
  }  

 private def buildSaveCancelButtons() = new HBox {
    children = List(buttonSave, new Region() { hgrow = Priority.Always }, buttonCancel )
  	padding = Insets(10)
  	spacing = 10
  }

  private def buildList() = new ListView[Field](listBuffer) {
	    cellFactory = { _ => buildCell() }
	  }		

  private def buildListButtons() = new VBox {
      buildCombo()
      
  }

  private def buildCombo() = new ComboBox[Field] {
        items = comboBuffer
        editable = false
        cellFactory = { _ => buildCell() }
    }

	private def buildCell() = new ListCell[Field] {
	        item.onChange { (_, _, _) => text.value = item.name
	        }} 	      



  val buttonCancel = new Button {
    text = "Cancel"
    alignmentInParent = Pos.CENTER_RIGHT
  }


  val buttonSave = new Button {
    text = "Save"
    alignmentInParent = Pos.CENTER_RIGHT
  }

   

  def onSave(save : OrderByFields  => Unit): Unit =
    buttonSave.onAction = (event: ActionEvent)  => save(OrderByFields(listBuffer.toList))

  def onCancel(cancel : ()  => Unit): Unit =
    buttonCancel.onAction = (event: ActionEvent)  => cancel()

  def control : Parent = layout
}
package dbtarzan.gui.orderby

import scalafx.scene.control.{ ListView, ListCell, SplitPane, Button, Alert, ButtonType, ComboBox }
import scalafx.scene.layout.{ BorderPane, VBox, HBox, Region, Priority }
import scalafx.scene.control.Alert.AlertType
import scalafx.event.ActionEvent
import scalafx.scene.Parent
import scalafx.geometry.{ Insets, Pos }
import scalafx.collections.ObservableBuffer 
import scalafx.Includes._
import scalafx.beans.property.{BooleanProperty, ObjectProperty}

import dbtarzan.config.ConnectionData
import dbtarzan.db.{ OrderByField, OrderByFields, Field, OrderByDirection, DBEnumsText }
import dbtarzan.gui.util.JFXUtil
import dbtarzan.gui.TControlBuilder
/**
  to change the order by columns. A list of order by columns with a panel on the side to change it.
*/
class OrderByEditor(
  possibleOrderByFields: List[Field], 
  currentOrderBys : Option[OrderByFields],
  onSave : OrderByFields  => Unit,
  onCancel : ()  => Unit
) extends TControlBuilder { 
  private val currentOrderByFields = currentOrderBys.map(_.fields).getOrElse(List.empty[OrderByField])
  private val listBuffer = ObservableBuffer[OrderByField](currentOrderByFields)
  private val comboFieldsBuffer = ObservableBuffer(possibleOrderByFields.diff(currentOrderByFields))
  private val comboOrderByDirectionsBuffer = ObservableBuffer(OrderByDirection.ASC, OrderByDirection.DESC)
  private val chosenField =  new ObjectProperty[Field]()
  private val chosenDirection = new ObjectProperty[OrderByDirection]()
  private var listFieldsCurrentIndex : Option[Int] = None
  private var editButtonsDisabled = BooleanProperty(true)

  private val layout = new BorderPane {
    center = listWithButtons()
    bottom = saveCancelButtons()
  }

 private def saveCancelButtons() : HBox = {
    new HBox {
      children = List(buttonSave, new Region() { hgrow = Priority.Always }, buttonCancel )
      padding = Insets(10)
      spacing = 10
    }
 }


  private def listWithButtons() = new BorderPane {
    center = listFields
    right = new BorderPane {
      top = listCombos
      bottom = listButtons
    }
  }  

  private def listCombos() = new VBox {
      children = List(comboFields, comboDirection)      
      padding = Insets(10)
      spacing = 10
      alignment = Pos.BASELINE_CENTER
  }

  private def listButtons() = new VBox  {
      children = List(buttonAdd, listEditButtons)      
      alignment = Pos.CENTER
  }

  private def listEditButtons() = new VBox {
      children = List(buttonUpdate, buttonMoveUp, buttonMoveDown, buttonDelete)      
      padding = Insets(10)
      spacing = 10
      alignment = Pos.BOTTOM_CENTER
      disable <==> editButtonsDisabled
  }

  private def listFields() = new ListView[OrderByField](listBuffer) {
	    cellFactory = { _ => buildOrderByFieldsCell() }
      selectionModel().selectedIndex.onChange {  (item, oldIndexNum, newIndexNum) => {
        val newIndex =  newIndexNum.intValue()
        if(newIndex >= 0) {
          listFieldsCurrentIndex = Some(newIndex)
          editButtonsDisabled.value = false
          val selection = listBuffer(newIndex) 
          chosenField.value = selection.field
          chosenDirection.value = selection.direction
        } 
      }}
	  }		

  private def buttonCancel() = new Button {
    text = "Cancel"
    alignmentInParent = Pos.CENTER_RIGHT
    onAction = (event: ActionEvent)  => onCancel()
  }

  private def buttonAdd() = new Button {
    text = "Add"
    onAction = (event: ActionEvent) => withChosenOrderByField(listBuffer.add(_)) 
  }
  
  private def buttonUpdate() = new Button {
    text = "Update"
    onAction = (event: ActionEvent)  =>  
      withChosenOrderByField(of => 
          listFieldsCurrentIndex.foreach(i => 
            listBuffer.update(i, of)
          )
        )
    }
  
  private def buttonMoveUp() = new Button {
    text = "Move Up"
    onAction = (event: ActionEvent)  => 
          listFieldsCurrentIndex.foreach(i => 
            if(i > 0)
              JFXUtil.swapListBuffer(listBuffer, i-1, i)
            )          
    }

  private def buttonMoveDown() = new Button {
    text = "Move Down"
    onAction = (event: ActionEvent)  => 
          listFieldsCurrentIndex.foreach(i => 
            if(i < listBuffer.size() - 1)
              JFXUtil.swapListBuffer(listBuffer, i, i+1)
            )          
    }

  private def buttonDelete() = new Button {
    text = "Delete"
    onAction = (event: ActionEvent)  => 
          listFieldsCurrentIndex.foreach(i => { 
              listBuffer.remove(i)
              editButtonsDisabled.value = listBuffer.isEmpty
            })          
    }

  private def buttonSave() = new Button {
    text = "Save"
    alignmentInParent = Pos.CENTER_RIGHT
    onAction = (event: ActionEvent)  => onSave(OrderByFields(listBuffer.toList))
 }


  private def comboFields() = new ComboBox[Field] {
      items = comboFieldsBuffer
      editable = false
      cellFactory = { _ => buildFieldsCell() }
      buttonCell =  buildFieldsCell()
      value <==> chosenField
  }

  private def comboDirection() = new ComboBox[OrderByDirection] {
      items = comboOrderByDirectionsBuffer
      cellFactory = { _ => buildDirectionCell() }
      buttonCell =  buildDirectionCell()
      editable = false
      value  <==> chosenDirection
  }

  private def buildOrderByFieldsCell() =  buildCell[OrderByField](
      of => of.field.name + " " + DBEnumsText.orderByDirectionToText(of.direction) 
  )


  private def buildDirectionCell() = buildCell[OrderByDirection]( 
    DBEnumsText.orderByDirectionToText(_)
  )   

  private def buildFieldsCell() = buildCell[Field]( _.name)       
    
  private def withChosenOrderByField(action : OrderByField => Unit) : Unit =
      Option(chosenField()).foreach(f => 
        Option(chosenDirection()).foreach(d =>
          action(OrderByField(f, d))
        ))

  private def buildCell[T](toText : T => String) = new ListCell[T] {
      item.onChange { 
        (_, _, value) => text = Option(value).map(v => toText(v)).getOrElse("") 
        }
  } 	     

  def control : Parent = layout
}
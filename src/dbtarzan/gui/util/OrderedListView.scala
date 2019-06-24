package dbtarzan.gui.util

import scalafx.collections.ObservableBuffer 
import scalafx.scene.control.{ListView, Button, Label, ListCell, ComboBox}
import scalafx.scene.layout.{ HBox, BorderPane }
import scalafx.geometry.{ Pos }
import scalafx.Includes._
import scalafx.scene.Parent
import scalafx.beans.property.{ ObjectProperty, BooleanProperty }
import scalafx.event.ActionEvent

/**
  A list of the errors happened in the application, last error first
*/
class OrderedListView[T](show : T => String, addButtonLabel : String) {
  val comboBuffer = ObservableBuffer.empty[T] 
  val listBuffer = ObservableBuffer.empty[T]
  private val emptyCombo = new BooleanProperty { value = comboBuffer.isEmpty }
  private val list = new ListView[T](listBuffer) {
    cellFactory = { _ => buildListCell() }
  }

  comboBuffer.onChange((buffer, changes) =>
      emptyCombo.value = buffer.isEmpty  
  )

  val comboAdd = new ComboBox[T] {
    items = comboBuffer
    editable = false
    buttonCell = buildComboCell()
    cellFactory = { _ => buildComboCell() }
    maxWidth = Double.MaxValue
  }
  
  val buttonAdd = new Button {
    text = addButtonLabel
  }

	private val layout = new BorderPane {
     center = list
     bottom = new BorderPane {
        center = comboAdd
        right = buttonAdd
        disable <==>  emptyCombo
      }
  }
 
 buttonAdd.onAction = (event: ActionEvent)  => {
    Option(comboAdd.selectionModel().selectedItem.value).foreach(
      choice => {
        listBuffer += choice
        comboBuffer -= choice
      }
    )
 }

  private def buildComboCell() = new ListCell[T] {
      item.onChange { 
        (_, _, value) => text = Option(value).map(v => show(v)).getOrElse("") 
        }
  } 


  private def buttonUp(value : T) = new Button {
    text = "▲"
    onAction = {
      (e: ActionEvent) => {                            
        val index = listBuffer.indexOf(value)
        if(index > 0) {
          listBuffer.remove(index)
          listBuffer.insert(index - 1, value)
        }
      }
    }                          
  }


  private def buttonDown(value : T) = new Button {
    text = "▼"
    onAction = {
      (e: ActionEvent) => {                            
        val index = listBuffer.indexOf(value)
        if(index < listBuffer.length - 1) {
          listBuffer.remove(index)
          listBuffer.insert(index + 1, value)
        }
      }
    }
  }

  private def buttonDelete(value : T) = new Button {
    text = "X"
    onAction = {
      (e: ActionEvent) => {
        listBuffer -= value
        comboBuffer += value
      }
    }
  }

  private def buildListCell() = new ListCell[T]() {
    item.onChange { (_ , _, value) => 
              if(value != null) {
                val panel = new BorderPane {            
                    center = new Label { 
                      text = show(value)
                      }
                    right = new HBox {
                      children = List(
                        buttonUp(value),
                        buttonDown(value),
                        buttonDelete(value)
                      )
                    }              
                }
                BorderPane.setAlignment(panel.center.value, Pos.CENTER_LEFT)
                graphic = panel
              }
              else 
                graphic = null
          }
        }

  def setComboData(data : List[T]) : Unit = {
    comboBuffer.clear()
    comboBuffer ++= data
    listBuffer.clear() // if we change the choices we need to clean up what has been chosen before
  }

  def onChange(action : List[T] => Unit) : Unit = 
    listBuffer.onChange((buffer, changes) =>  { action(buffer.toList) })

  def control : Parent = layout 
}


package dbtarzan.gui.util

import dbtarzan.gui.util.orderedlist.OrderedListButtonBuilder
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.{Button, ComboBox, Label, ListCell, ListView}
import scalafx.scene.layout.{BorderPane, HBox}
import scalafx.geometry.Pos
import scalafx.Includes._
import scalafx.scene.Parent
import scalafx.beans.property.BooleanProperty
import scalafx.event.ActionEvent

/**
  a list of items selected from a combo box. Eech item is displayed with buttons to move it  up and down  or to remove it
*/
class OrderedListView[T](show : T => String, addButtonLabel : String, rowButtons: List[OrderedListButtonBuilder[T]]) {
  val comboBuffer: ObservableBuffer[T] = ObservableBuffer.empty[T]
  private val listBuffer = ObservableBuffer.empty[T]
  private val emptyCombo = new BooleanProperty { value = comboBuffer.isEmpty }
  private val buttonDisabled = new BooleanProperty { value = comboBuffer.isEmpty }

  val safe = new OnChangeSafe()
  private val list = new ListView[T](listBuffer) {
    cellFactory = { _ => buildListCell() }
  }

  comboBuffer.onChange((buffer, changes) =>
      emptyCombo.value = buffer.isEmpty  
  )

  val comboAdd: ComboBox[T] = new ComboBox[T] {
    items = comboBuffer
    editable = false
    buttonCell = buildComboCell()
    cellFactory = { _ => buildComboCell() }
    maxWidth = Double.MaxValue
  }

  comboAdd.selectionModel().selectedItem.onChange(
      (_, _, nullableValue) => {
        val buttonEnabled = Option(nullableValue).exists(_ => !emptyCombo.value)
        buttonDisabled.value = !buttonEnabled 
    })
  
  val buttonAdd: Button = new Button {
    text = addButtonLabel
    disable <==>  buttonDisabled
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
        (_, _, nullableValue) => {
            text = Option(nullableValue).map(v => show(v)).getOrElse("") 
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
                      children = rowButtons.map(_.onChange(value, listBuffer, comboBuffer))
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
    val remainingData = data.diff(listBuffer.toList)
    JFXUtil.bufferSet(comboBuffer, remainingData)
  }

  def onChange(action : List[T] => Unit) : Unit = 
    listBuffer.onChange((buffer, changes) =>  
      safe.noChangeEventDuring(() =>  action(buffer.toList)) 
    )

  def setListData(data : List[T]) : Unit = 
    safe.onChange(() =>
      JFXUtil.bufferSet(listBuffer, data)
    )

  def listData() : List[T] = listBuffer.toList

  def control : Parent = layout 
}


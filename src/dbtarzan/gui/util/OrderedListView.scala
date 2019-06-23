package dbtarzan.gui.util

import scalafx.collections.ObservableBuffer 
import scalafx.scene.control.TableColumn._
import scalafx.scene.control.{TableColumn, TableView, TableCell,ListView, Button, Label, ListCell, ComboBox}
import scalafx.scene.layout.{ HBox, VBox, Pane, BorderPane }
import scalafx.geometry.{ Insets, Pos }
import scalafx.Includes._
import scalafx.scene.Parent
import scalafx.beans.property.{ StringProperty, ObjectProperty }
import scalafx.event.ActionEvent
import dbtarzan.gui.util.JFXUtil

/**
  A list of the errors happened in the application, last error first
*/
class OrderedListView[T](show : T => String, addButtonLabel : String, comboBuffer : ObservableBuffer[T]) {
  private val tableBuffer = ObservableBuffer.empty[T]
  private val list = new ListView[T](tableBuffer) {
    cellFactory = { _ => buildListCell() }
  }

  val comboAdd = new ComboBox[T] {
    items = comboBuffer
    editable = false
    buttonCell = buildComboCell()
    cellFactory = { _ => buildComboCell() }
    maxWidth = Double.MaxValue
  }
  
  val buttonAdd = new Button {
    text = addButtonLabel
    //alignmentInParent = Pos.CENTER_RIGHT
//    disable <===>  
  }

	private val layout = new BorderPane {
     center = list
     bottom = new BorderPane {
        center = comboAdd
        right = buttonAdd
      }
  }
 
 buttonAdd.onAction = (event: ActionEvent)  => {
    Option(comboAdd.selectionModel().selectedItem.value).foreach(
      choice => {
        tableBuffer += choice
        comboBuffer -= choice
        println("added "+tableBuffer.length)
        
      }
    )
 }

  private def buildComboCell() = new ListCell[T] {
      item.onChange { 
        (_, _, value) => text = Option(value).map(v => show(v)).getOrElse("") 
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
                        new Button {
                          text = "▲"
                          onAction = {
                            (e: ActionEvent) => println("pressed the button")
                          }
                          
                        },
                        new Button {
                          text = "▼"
                          onAction = {
                            (e: ActionEvent) => println("pressed the button")
                          }
                        },
                        new Button {
                          text = "X"
                          onAction = {
                            (e: ActionEvent) => println("pressed the button")
                          }
                        }
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
  }

  def control : Parent = layout 
}


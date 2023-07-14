package dbtarzan.gui.util

import dbtarzan.gui.util.listviewbuttons.{ButtonBuilder, TListViewButton}
import scalafx.Includes._
import scalafx.beans.property.BooleanProperty
import scalafx.collections.ObservableBuffer
import scalafx.event.ActionEvent
import scalafx.geometry.Pos
import scalafx.scene.Parent
import scalafx.scene.control.{Button, ComboBox, ListCell, ListView}
import scalafx.scene.layout.{BorderPane, HBox}


/**
a list of items selected from a combo box. Eech item is displayed with buttons to move it  up and down  or to remove it
 */
class ListViewAddFromCombo[T](
                          addButtonLabel: String,
                          cellBuilder: Option[T] => Parent,
                          comboStrategy: TComboStrategy[T],
                          additionalButtons: List[TListViewButton[T]]
                        ) {
  val comboBuffer: ObservableBuffer[T] = ObservableBuffer.empty[T]
  private val listBuffer = ObservableBuffer.empty[T]
  private val comboEmpty = new BooleanProperty { value = comboBuffer.isEmpty }
  private val buttonDisabled = new BooleanProperty { value = comboBuffer.isEmpty }

  val safe = new OnChangeSafe()
  private val list = new ListView[T](listBuffer) {
    cellFactory = { _ => buildListCell() }
  }

  comboBuffer.onChange((buffer, changes) =>
    comboEmpty.value = buffer.isEmpty
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
      val buttonEnabled = Option(nullableValue).isDefined && !comboEmpty.value
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
      disable <==>  comboEmpty
    }
  }

  buttonAdd.onAction = (event: ActionEvent)  => {
    Option(comboAdd.selectionModel().selectedItem.value).foreach(
      choice => {
        listBuffer += choice
        comboStrategy.removeFromCombo(comboBuffer, choice)
      }
    )
  }

  private def buildComboCell() = new ListCell[T] {
    item.onChange {
      (_, _, nullableValue) => {
        graphic = cellBuilder(Option(nullableValue))
      }
    }
  }

  private def buttonDelete(value : T) = ButtonBuilder.buildButton(value, "X", (value: T) => {
    listBuffer -= value
    comboStrategy.addToCombo(comboBuffer, value)
  })

  private def buildListCell() = new ListCell[T]() {
    item.onChange { (_ , _, value) =>
      if(value != null) {
        val panel = new BorderPane {
          center = cellBuilder(Option(value))
          right = new HBox {
            children = additionalButtons.map(button => button.buildFor(value, listBuffer)) ++ List(
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

  def setComboData(comboData : List[T]) : Unit = {
    JFXUtil.bufferSet(comboBuffer, comboData)
    removeListDataFromCombo()
  }

  private def removeListDataFromCombo(): Unit = {
    listBuffer.foreach(v => comboStrategy.removeFromCombo(comboBuffer, v))
  }

  def onChange(action : List[T] => Unit) : Unit =
    listBuffer.onChange((buffer, _) =>
      safe.noChangeEventDuring(() =>  action(buffer.toList))
    )

  def setListData(listData : List[T]) : Unit =
    safe.onChange(() => {
      JFXUtil.bufferSet(listBuffer, listData)
      removeListDataFromCombo()
    })

  def setListAndComboData(listData : List[T], comboData: List[T]): Unit =
    safe.onChange(() => {
      JFXUtil.bufferSet(listBuffer, listData)
      JFXUtil.bufferSet(comboBuffer, comboData)
      removeListDataFromCombo()
    })


  def listData() : List[T] = listBuffer.toList

  def control : Parent = layout
}


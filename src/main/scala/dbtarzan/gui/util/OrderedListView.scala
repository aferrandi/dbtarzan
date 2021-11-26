package dbtarzan.gui.util

import scalafx.Includes._
import scalafx.beans.property.BooleanProperty
import scalafx.collections.ObservableBuffer
import scalafx.event.ActionEvent
import scalafx.geometry.Pos
import scalafx.scene.Parent
import scalafx.scene.control.{Button, ComboBox, ListCell, ListView}
import scalafx.scene.layout.{BorderPane, HBox}

trait TComboStrategy[T] {
  def removeFromCombo(comboBuffer: ObservableBuffer[T], item : T): Unit
  def addToCombo(comboBuffer: ObservableBuffer[T], item : T): Unit
}

/**
a list of items selected from a combo box. Eech item is displayed with buttons to move it  up and down  or to remove it
 */
class OrderedListView[T](
                          addButtonLabel: String,
                          cellBuilder: Option[T] => Parent,
                          comboStrategy: TComboStrategy[T]
                        ) {
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
      val buttonEnabled = Option(nullableValue).isDefined && !emptyCombo.value
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

  private def buttonUp(value : T) = buildButton(value, "▲", (value: T) =>
    moveUp(value)
  )


  private def buttonDown(value : T) = buildButton(value, "▼", (value: T) =>
    moveDown(value)
  )


  private def buttonDelete(value : T) = buildButton(value, "X", (value: T) => {
    listBuffer -= value
    comboStrategy.addToCombo(comboBuffer, value)
  })

  private def buildButton(value: T, icon: String, onClick: T => Unit) = {
    new Button {
      text = icon
      stylesheets += "rowButton.css"
      onAction = {
        (e: ActionEvent) => onClick(value)
      }
    }
  }

  private def moveUp(value: T): Unit = {
    val index = listBuffer.indexOf(value)
    if (index > 0) {
      listBuffer.remove(index)
      listBuffer.insert(index - 1, value)
    }
  }
  private def moveDown(value: T): Unit = {
    val index = listBuffer.indexOf(value)
    if (index < listBuffer.length - 1) {
      listBuffer.remove(index)
      listBuffer.insert(index + 1, value)
    }
  }

  private def buildListCell() = new ListCell[T]() {
    item.onChange { (_ , _, value) =>
      if(value != null) {
        val panel = new BorderPane {
          center = cellBuilder(Option(value))
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
    JFXUtil.bufferSet(comboBuffer, data)
    listBuffer.foreach(v => comboStrategy.removeFromCombo(comboBuffer, v))
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


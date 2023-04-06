package dbtarzan.gui.orderby

import dbtarzan.db.{Field, OrderByDirection, OrderByField, OrderByFields}
import dbtarzan.gui.TControlBuilder
import dbtarzan.gui.util.{JFXUtil, ListViewAddFromComboBuilder, TComboStrategy}
import dbtarzan.localization.Localization
import scalafx.Includes._
import scalafx.beans.property.BooleanProperty
import scalafx.collections.ObservableBuffer
import scalafx.event.ActionEvent
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Parent
import scalafx.scene.control.{Button, Label}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{BorderPane, HBox, Priority, Region}
import scalafx.scene.paint.Color

/**
  to change the order by columns. A list of order by columns with a panel on the side to change it.
*/
class OrderByEditor(
  possibleOrderByFields: List[Field], 
  currentOrderBys : Option[OrderByFields],
  onSave : OrderByFields  => Unit,
  onCancel : ()  => Unit,
  localization : Localization
) extends TControlBuilder {
  private var saveButtonDisabled = BooleanProperty(true)

  val upIcon: Image = JFXUtil.loadIcon("up.png")
  val downIcon: Image = JFXUtil.loadIcon("down.png")

  private val currentOrderByFields = currentOrderBys.map(_.fields).getOrElse(List.empty[OrderByField])

  private val showField: Option[OrderByField] => BorderPane = (value: Option[OrderByField]) => new BorderPane {
    center =   new Label {
      alignmentInParent = Pos.CenterLeft
      textFill = Color.Black
      text = value.map(v => v.field.name).getOrElse("")
    }
    right = new ImageView(iconFromDirection(value))
    padding = Insets(0,20,0, 0)
  }
  private val comboStrategy = new TComboStrategy[OrderByField] {
    override def removeFromCombo(comboBuffer: ObservableBuffer[OrderByField], item: OrderByField): Unit =
      comboBuffer --= fieldInBothDirections(item.field)
    override def addToCombo(comboBuffer: ObservableBuffer[OrderByField], item: OrderByField): Unit =
      comboBuffer ++= fieldInBothDirections(item.field)
  }

  private def fieldInBothDirections(field: Field) = {
    OrderByDirection.directions().map(d => OrderByField(field, d))
  }

  private var list = ListViewAddFromComboBuilder.buildOrdered[OrderByField](localization.add, showField, comboStrategy)
  list.setListData(currentOrderByFields)
  list.setComboData(possibleOrderByFields.flatMap(f => OrderByDirection.directions().map(d => OrderByField(f, d))))
  list.onChange(data =>
    saveButtonDisabled.value = data.isEmpty
  )

  private def iconFromDirection(value: Option[OrderByField]): Image = {
    value.map(v =>
      v.direction match {
        case OrderByDirection.ASC => upIcon
        case OrderByDirection.DESC => downIcon
      }).orNull
  }


  private val layout = new BorderPane {
    center = list.control
    bottom = saveCancelButtons()
  }

 private def saveCancelButtons() : HBox = {
    new HBox {
      children = List(buttonSave(), new Region() { hgrow = Priority.Always }, buttonCancel() )
      padding = Insets(10)
      spacing = 10
    }
 }

  private def buttonCancel() = new Button {
    text = localization.cancel
    alignmentInParent = Pos.CenterRight
    onAction = (event: ActionEvent)  => onCancel()
  }

  private def buttonSave() = new Button {
    text = localization.save
    alignmentInParent = Pos.CenterRight
    disable <==> saveButtonDisabled
    onAction = (_: ActionEvent)  => {
      if(list.listData().nonEmpty || JFXUtil.areYouSure(localization.unorderedQueryResults, localization.saveOrder))
        onSave(OrderByFields(list.listData()))
    }
 }

  def control : Parent = layout
}
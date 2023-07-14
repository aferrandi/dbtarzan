package dbtarzan.gui.info

import akka.actor.ActorRef
import dbtarzan.db.{Index, OrderByDirection}
import dbtarzan.gui.interfaces.TControlBuilder
import dbtarzan.gui.util.JFXUtil
import dbtarzan.localization.Localization
import scalafx.beans.property.{ObjectProperty, StringProperty}
import scalafx.collections.ObservableBuffer
import scalafx.scene.Parent
import scalafx.scene.control.{Label, TableCell, TableColumn, TableView}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.VBox

class IndexInfo(guiActor : ActorRef, localization : Localization, index: Index) extends TControlBuilder {
  case class TableLine(fieldName: String, direction: Option[OrderByDirection])

  private val buffer = ObservableBuffer.empty[TableLine]
  /* the table */
  private val table = buildTable()
  addIndex(index)

  val content: VBox = new VBox {
    fillWidth = true
    children = List(new Label(localization.name+": "+index.name), table)
  }
  val upIcon: Image = JFXUtil.loadIcon("up.png")
  val downIcon: Image = JFXUtil.loadIcon("down.png")

  /* builds table with the two columns (name and description) */
  def buildTable(): TableView[TableLine] = new TableView[TableLine](buffer) {
    columns ++= List ( fieldNameColumn(), directionColumn())
    editable = false
    columnResizePolicy = TableView.ConstrainedResizePolicy
    stylesheets += "loglist.css"
  }

  private def fieldNameColumn() = new TableColumn[TableLine, String] {
    text = localization.field
    cellValueFactory = { x => new StringProperty(x.value.fieldName) }
    resizable = true
  }

  private def directionToImage(direction: Option[OrderByDirection]): Image =
    direction match {
      case Some(OrderByDirection.ASC) => upIcon
      case Some(OrderByDirection.DESC) => downIcon
      case _ => upIcon
    }

  /* the column with the description of the database field */
  private def directionColumn() = new TableColumn[TableLine, Image] {
    cellValueFactory = { x => ObjectProperty(directionToImage(x.value.direction)) }
    cellFactory = {
      _ : TableColumn[TableLine, Image] => new TableCell[TableLine, Image] {
        item.onChange {
          (_, _, newImage) => graphic = new ImageView(newImage)
        }
      }
    }
    maxWidth = 24
    minWidth = 24
  }

  /* adds the database rows (the database table fields) to the table */
  private def addIndex(index: Index) : Unit = {
    val lines = index.fields.map(field => TableLine(field.name, field.direction))
    buffer ++= lines
  }

  def control : Parent = content
}
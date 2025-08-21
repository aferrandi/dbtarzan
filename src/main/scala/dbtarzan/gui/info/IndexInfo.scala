package dbtarzan.gui.info

import dbtarzan.db.{Index, OrderByDirection}
import dbtarzan.gui.interfaces.TControlBuilder
import dbtarzan.gui.orderby.UpDownIcons
import dbtarzan.gui.util.{JFXUtil, TableUtil}
import dbtarzan.localization.Localization
import scalafx.collections.ObservableBuffer
import scalafx.scene.Parent
import scalafx.scene.control.{Label, TableColumn, TableView}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.VBox
import scalafx.Includes.*

object IndexInfo {
  def uniqueText(index: Index): String =
    if (index.unique) "UNIQUE" else "NOT UNIQUE"
}

class IndexInfo(localization : Localization, index: Index) extends TControlBuilder {
  case class TableLine(fieldName: String, direction: Option[OrderByDirection])

  private val buffer = ObservableBuffer.empty[TableLine]
  /* the table */
  private val table = buildTable()
  addIndex(index)

  val content: VBox = new VBox {
    fillWidth = true
    children = List(indexLabel(), table)
  }

  private def indexLabel() = new Label {
      text = s"${localization.name}: ${index.name} ${IndexInfo.uniqueText(index)}"
      graphic = if (index.unique) new ImageView(JFXUtil.loadIcon("primaryKey.png")) else null
    }

  val upIcon: Image = JFXUtil.loadIcon("up.png")
  val downIcon: Image = JFXUtil.loadIcon("down.png")

  /* builds table with the two columns (name and description) */
  def buildTable(): TableView[TableLine] = new TableView[TableLine](buffer) {
    columns ++= List ( fieldNameColumn(), directionColumn())
    editable = false
    columnResizePolicy = javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
    stylesheets += "loglist.css"
  }

  private def fieldNameColumn() = TableUtil.buildTextTableColumn[TableLine](localization.field, _.value.fieldName )

  private def directionToImage(direction: Option[OrderByDirection]): Image =
    direction.map(UpDownIcons.iconFromDirection).getOrElse(UpDownIcons.upIcon)

  /* the column with the description of the database field */
  private def directionColumn() = TableUtil.buildImageTableColumn[TableLine](x => directionToImage(x.value.direction))

  /* adds the database rows (the database table fields) to the table */
  private def addIndex(index: Index) : Unit = {
    val lines = index.fields.map(field => TableLine(field.name, field.direction))
    buffer ++= lines
  }

  def control : Parent = content
}
package dbtarzan.gui.info

import dbtarzan.db.{Field, Fields}
import dbtarzan.gui.interfaces.TControlBuilder
import dbtarzan.gui.util.{JFXUtil, TableUtil}
import dbtarzan.localization.Localization
import scalafx.collections.ObservableBuffer
import scalafx.scene.Parent
import scalafx.scene.control.{TableColumn, TableView}


/** The GUI table control showing the description of the columns of a database table */
class ColumnsTable(fields: Fields, localization : Localization) extends TControlBuilder {
  private val buffer = ObservableBuffer.empty[Field]
  /* the table */
  private val table = buildTable()
  addRows()


  /* builds table with the two columns (name and description) */ 
  def buildTable(): TableView[Field] = new TableView[Field](buffer) {
    columns ++= List ( nameColumn(), descriptionColumn())
    editable = false
    columnResizePolicy = javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
  }

   /* the column with the name of the database field */
  private def nameColumn() = TableUtil.buildTextTableColumn[Field](localization.field, _.value.name)

  /* the column with the description of the database field */
  private def descriptionColumn() = TableUtil.buildTextTableColumn[Field](localization.description, _.value.typeDescription)

  private def contentAsText() : String =
    "Name\tDescription\n" +  fields.fields.map(f => s"${f.name}\t${f.typeDescription}").mkString("\n")

  def contentToClipboard(): Unit =
    JFXUtil.copyTextToClipboard(contentAsText())

  /* adds the database rows (the database table fields) to the table */
  def addRows() : Unit = 
    buffer ++= fields.fields

  def control : Parent = table
}

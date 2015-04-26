package dbtarzan.gui

import scalafx.scene.control.TableColumn._
import scalafx.scene.control.{TableColumn, TableView, SelectionMode, ContextMenu}
import scalafx.beans.property.{StringProperty, ObjectProperty, BooleanProperty}
import scalafx.collections.ObservableBuffer 
import scalafx.scene.control.cell.CheckBoxTableCell
import scalafx.Includes._
import dbtarzan.config.{ Config, ConfigReader }
import dbtarzan.db.{Field, Row, Rows}
import dbtarzan.messages._
import akka.actor.ActorRef

/** The GUI table control showing the content of a database table in a GUI table*/
class Table(dbActor: ActorRef, id : TableId, dbTable : dbtarzan.db.Table){

  val names = dbTable.columnNames
  println("ColumnNames: "+names)
  /* the content of the table in terms of rows. Updated by the table itself */
  val buffer = ObservableBuffer.empty[JFXRow]
  /* keeps track of the rows that have the check box turned on */ 
  val checkedRows = new CheckedRows()
  /* converts rows to structures usable from the table */
  val fromRow = new JFXRowFromRow(checkedRows)
  /* the table */
  val table = buildTable()
  /* requests the rows for the table to the database actor. They come back using the addRows function */
  dbActor ! QueryRows(id, dbTable.sql, 500) 
  /* requests the foreign keys for this table. */
  dbActor ! QueryForeignKeys(id)

  /* builds table with the given columns with the possibility to check the rows and to select multiple rows */ 
  def buildTable() = new TableView[JFXRow](buffer) {
    columns += buildCheckColumn()
    columns ++= names.zipWithIndex.map({ case (field, i) => buildColumn(field, i) })
    editable = true
    selectionModel().selectionMode() = SelectionMode.MULTIPLE
    contextMenu = new ContextMenu(ClipboardMenuMaker.buildClipboardMenu("Selection", () => selectionToString()))
  }

 /* gets the nth column from the database row */
  def buildColumn(field : Field, index : Int) = new TableColumn[JFXRow,String]() {
		text = field.name
		cellValueFactory = { _.value.values(index) } // when showing a row, shows the value for the column field
    prefWidth = 180
	}.delegate

  /* the ckeck box column is special */
  def buildCheckColumn() =  {
    val checkColumn = new TableColumn[JFXRow, java.lang.Boolean] {
        text = ""
        cellValueFactory = { _.value.checked.delegate  }
        prefWidth = 40
        editable = true
    }
    println("Check column created")
    checkColumn.setCellFactory(CheckBoxTableCell.forTableColumn(checkColumn))
    checkColumn
  }

  /* converts the selected part of the table to a string that can be written to the clipboard */
  private def selectionToString() : String = 
  {
      val rows = table.getSelectionModel().getSelectedItems()
     rows.map(cellsInRow => cellsInRow.values.map(cell => cell()).mkString("\t") ).mkString("\n")
  }

  /* adds the database rows to the table */
  def addRows(rows : Rows) : Unit = 
    buffer ++= fromRow(rows, names)

  /* the unique id for the table */
  def getId = id
}

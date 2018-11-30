package dbtarzan.gui

import scalafx.scene.control.TableColumn._
import scalafx.scene.control.{TableColumn, TableView, SelectionMode}
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.cell.CheckBoxTableCell
import scalafx.scene.image.ImageView
import scalafx.scene.Parent
import scalafx.Includes._
import akka.actor.ActorRef
import dbtarzan.db.{Field, Row, Rows, DBEnumsText, PrimaryKeys, ForeignKeys, DBTable}
import dbtarzan.messages._
import dbtarzan.gui.util.JFXUtil
import dbtarzan.gui.table.{CheckedRow, CheckedRowFromRow, CheckedRowsBuffer, TableColumnsHeadings, TableContextMenu, HeadingText}
import dbtarzan.messages.Logger


/** The GUI table control showing the content of a database table in a GUI table*/
class Table(dbActor: ActorRef, guiActor : ActorRef, queryId : QueryId, dbTable : DBTable) extends TControlBuilder {
  private val log = new Logger(guiActor)
  val names : List[Field] = dbTable.columnNames
  println("ColumnNames: "+names.map(f => f.name+ DBEnumsText.fieldTypeToText(f.fieldType)))
  /* the content of the table in terms of rows. Updated by the table itself */
  private val buffer = ObservableBuffer.empty[CheckedRow]
  /* keeps track of the rows that have the check box turned on */ 
  private val checkedRows = new CheckedRowsBuffer()
  /* the table */
  private val table = buildTable()
  /* a row click listener (to show the row in the external list) */
  private var rowClickListener : Option[Row => Unit] = None
   /* converts rows to structures usable from the table */
  private val fromRow = new CheckedRowFromRow(checkedRows, table.selectionModel()) 
  /* to build automatically the headings of the table colums */
  private val headings = new TableColumnsHeadings(names)

  /* requests the foreign keys for this table. */
  dbActor ! QueryForeignKeys(queryId)
  /* requests the primary keys for this table. */
  dbActor ! QueryPrimaryKeys(queryId)


  /* builds table with the given columns with the possibility to check the rows and to select multiple rows */ 
  def buildTable() = new TableView[CheckedRow](buffer) {
    columns += buildCheckColumn()
    columns ++= names.zipWithIndex.map({ case (field, i) => buildColumn(field, i) })
    editable = true
    selectionModel().selectionMode() = SelectionMode.MULTIPLE
    selectionModel().selectedItem.onChange(
      (_, _, row) => 
        Option(row).map(_.row).foreach(rowValues =>
          rowClickListener.foreach(listener => listener(rowValues))
        )
    )
    contextMenu = new TableContextMenu(queryId, guiActor).buildContextMenu()
  }

  private def checkedIfOnlyOne() =
    if(buffer.length == 1)
      checkAll(true)    

  /* check the check box of all the loaded rows */
  def checkAll(check : Boolean) : Unit = 
    buffer.foreach(row => row.checked.value = check)

 /* gets the nth column from the database row */
  def buildColumn(field : Field, index : Int) = new TableColumn[CheckedRow,String]() {
		text = field.name
		cellValueFactory = { _.value.values(index) } // when showing a row, shows the value for the column field
    prefWidth = 180
	}.delegate

  /* the ckeck box column is special */
  def buildCheckColumn() =  {
    val checkColumn = new TableColumn[CheckedRow, java.lang.Boolean] {
        text = ""
        cellValueFactory = { _.value.checked.delegate  }
        prefWidth = 40
        editable = true
    }
    println("Check column created")
    checkColumn.setCellFactory(CheckBoxTableCell.forTableColumn(checkColumn))
    checkColumn
  }

  private def selectedRows() : ObservableBuffer[CheckedRow]  = table.selectionModel().selectedItems
  
  /* the first row in the selection (if any), the row you want to display in the RowDetailsView */
  def firstSelectedRow() : Option[Row] = selectedRows().headOption.map(_.row) 

  /* adds the database rows to the table */
  def addRows(rows : Rows) : Unit = { 
    buffer ++= fromRow(rows, names)
    checkedIfOnlyOne()
    } 

  def setRowClickListener(listener : Row => Unit) : Unit = {
    rowClickListener = Some(listener)
  }

  private def displayKeyForFields(headingsTexts : List[HeadingText]) : Unit = 
    headingsTexts.foreach(ht => {
        val column = table.columns(ht.index+1)
        column.text = ht.text
        ht.icon.foreach(icon => column.graphic = new ImageView(icon))
      })  

  /* adds the database rows to the table */
  def addPrimaryKeys(keys : PrimaryKeys) : Unit =     
    displayKeyForFields(headings.addPrimaryKeys(keys))

  def addForeignKeys(keys : ForeignKeys) : Unit = {
    displayKeyForFields(headings.addForeignKeys(keys))
  }

  /* the unique id for the table */
  def getId = queryId

  def getCheckedRows = checkedRows.rows

  def rowsNumber = buffer.length

  /* converts the selected part of the table to a string that can be written to the clipboard */
  private def selectedRowsToString() : String = {
    selectedRows().map(cellsInRow => cellsInRow.values.map(cell => cell()).mkString("\t") ).mkString("\n")
  }

  private def headersToString() : String =
    names.map(_.name).mkString("\t")

  def copySelectionToClipboard(includeHeaders : Boolean) : Unit = 
    try {
      includeHeaders match {
        case true => JFXUtil.copyTextToClipboard(headersToString()+ "\n" + selectedRowsToString()) 
        case false => JFXUtil.copyTextToClipboard(selectedRowsToString())
      } 
      log.info("Selection copied")
    } catch {
      case ex : Exception => log.error("Copying selection to the clipboard got ", ex)
    }

  def control : Parent = table
}

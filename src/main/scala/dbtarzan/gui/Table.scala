package dbtarzan.gui

import java.lang

import scalafx.scene.control.TableColumn._
import scalafx.scene.control.{SelectionMode, TableColumn, TableView}
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.cell.CheckBoxTableCell
import scalafx.scene.image.ImageView
import scalafx.scene.Parent
import scalafx.Includes._
import akka.actor.ActorRef
import dbtarzan.db.{DBEnumsText, DBTable, Field, ForeignKeys, PrimaryKeys, Row, Rows}
import dbtarzan.messages._
import dbtarzan.gui.util.JFXUtil
import dbtarzan.gui.table.{CheckedRow, CheckedRowFromRow, CheckedRowsBuffer, HeadingText, TableColumnsFitter, TableColumnsHeadings, TableContextMenu, TableToClipboard}
import dbtarzan.messages.Logger
import dbtarzan.localization.Localization


/** The GUI table control showing the content of a database table in a GUI table*/
class Table(dbActor: ActorRef, guiActor : ActorRef, queryId : QueryId, dbTable : DBTable, localization : Localization) extends TControlBuilder {
  private val log = new Logger(guiActor)
  val names : List[Field] = dbTable.columnNames
  println("ColumnNames: "+names.map(f => f.name+ DBEnumsText.fieldTypeToText(f.fieldType)))
  /* the content of the table in terms of rows. Updated by the table itself */
  private val buffer = ObservableBuffer.empty[CheckedRow]
  /* keeps track of the rows that have the check box turned on */ 
  private val checkedRows = new CheckedRowsBuffer()
  /* the table */
  private val table = buildTable()
  /* to resize the columns by their sizes */
  private val tableFit = new TableColumnsFitter(table, names)
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
  def buildTable(): TableView[CheckedRow] = new TableView[CheckedRow](buffer) {
    columns += buildCheckColumn()
    columns ++= names.zipWithIndex.map({ case (field, i) => buildColumn(field, i) })
    editable = true
    selectionModel().selectionMode() = SelectionMode.Multiple
    selectionModel().selectedItem.onChange(
      (_, _, row) => 
        Option(row).map(_.row).foreach(rowValues =>
          rowClickListener.foreach(listener => listener(rowValues))
        )
    )
    contextMenu = new TableContextMenu(queryId, guiActor, localization).buildContextMenu()
  }

  private def checkedIfOnlyOne(): Unit =
    if(buffer.length == 1)
      checkAll(true)    

  /* check the check box of all the loaded rows */
  def checkAll(check : Boolean) : Unit = 
    buffer.foreach(row => row.checked.value = check)

 /* gets the nth column from the database row */
  def buildColumn(field : Field, index : Int): TableColumn[CheckedRow,String] = new TableColumn[CheckedRow,String]() {
		text = field.name
		cellValueFactory = { _.value.values(index) } // when showing a row, shows the value for the column field
    prefWidth = 180
	}.delegate

  /* the ckeck box column is special */
  def buildCheckColumn(): TableColumn[CheckedRow, lang.Boolean] =  {
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

  def selectOneIfNoneSelected(): Unit =
    if (buffer.length > 0 && selectedRows().isEmpty)
      table.selectionModel().select(0)

  /* adds the database rows to the table */
  def addRows(rows : Rows) : Unit = try { 
    buffer ++= fromRow(rows, names)
    selectOneIfNoneSelected()
    checkedIfOnlyOne()
    tableFit.addRows(rows.rows)
    } catch {
      case ex : Exception => log.error(localization.errorDisplayingRows, ex)
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
  def getId: QueryId = queryId

  def getCheckedRows: List[Row] = checkedRows.rows

  def rowsNumber: Int = buffer.length

  def copySelectionToClipboard(includeHeaders : Boolean) : Unit = 
    try {
      val toClipboard = new TableToClipboard(selectedRows(), names)
      toClipboard.copySelectionToClipboard(includeHeaders)
      log.info(localization.selectionCopied)
    } catch {
      case ex : Exception => log.error(localization.errorCopyingSelection, ex)
    }

  def control : Parent = table
}

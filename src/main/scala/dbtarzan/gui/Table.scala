package dbtarzan.gui

import dbtarzan.db.*
import dbtarzan.gui.interfaces.TControlBuilder
import dbtarzan.gui.table.*
import dbtarzan.gui.util.JFXUtil
import dbtarzan.localization.Localization
import dbtarzan.log.actor.Logger
import dbtarzan.messages.*
import org.apache.pekko.actor.ActorRef


/** The GUI table control showing the content of a database table in a GUI table*/
class Table(guiActor : ActorRef, queryId : QueryId, dbTable : DBTable, localization : Localization, log: Logger) extends TControlBuilder {
  val fields : List[Field] = dbTable.fields
  log.debug("ColumnNames: "+fields.map(f => f.name+ DBEnumsText.fieldTypeToText(f.fieldType)))
  /* the content of the table in terms of rows. Updated by the table itself */
  private val buffer = ObservableBuffer.empty[CheckedRow]
  /* keeps track of the rows that have the check box turned on */
  private val checkedRows = new CheckedRowsBuffer()
  /* the table */
  private val table = buildTable()
  /* to resize the columns by their sizes */
  private val tableFit = new TableColumnsFitter(table, fields)
  /* a row click listener (to show the row in the external list) */
  private var rowClickListener : Option[Row => Unit] = None
   /* converts rows to structures usable from the table */
  private val fromRow = new CheckedRowFromRow(checkedRows, table.selectionModel())
  /* to build automatically the headings of the table colums */
  private val headings = new TableColumnsHeadings(fields)

  /* builds table with the given columns with the possibility to check the rows and to select multiple rows */
  def buildTable(): TableView[CheckedRow] = new TableView[CheckedRow](buffer) {
    columns += TableColumnsBuild.buildCheckColumn()
    columns ++= fields.zipWithIndex.map({ case (field, i) => field.fieldType match {
      case FieldType.STRING => TableColumnsBuild.buildStringColumn(field, i)
      case FieldType.INT => TableColumnsBuild.buildIntColumn(field, i)
      case FieldType.FLOAT => TableColumnsBuild.buildFloatColumn(field, i)
      case FieldType.BINARY => TableColumnsBuild.buildBinaryColumn(field, i)
    } })
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

  private def selectedRows() : ObservableBuffer[CheckedRow]  =
    table.selectionModel().selectedItems

  /* the first row in the selection (if any), the row you want to display in the RowDetailsView */
  def firstSelectedRow() : Option[Row] = selectedRows().headOption.map(_.row)

  def selectOneIfNoneSelected(): Unit =
    if (buffer.length > 0 && selectedRows().isEmpty)
      table.selectionModel().select(0)

  /* adds the database rows to the table */
  def addRows(rows : Rows) : Unit = try {
    buffer ++= fromRow(rows, fields)
    // selectOneIfNoneSelected()
    checkedIfOnlyOne()
    tableFit.addRows(rows.rows)
    } catch {
      case ex : Exception => log.error(localization.errorDisplayingRows, ex)
    }

  def setRowClickListener(listener : Row => Unit) : Unit =
    rowClickListener = Some(listener)

  def setRowDoubleClickListener(listener: Row => Unit) : Unit =
    JFXUtil.onAction(table, (row: CheckedRow, _: Boolean) => listener(row.row))

  private def displayKeyForFields(headingsTexts : List[HeadingTextAndIcon]) : Unit =
    headingsTexts.foreach(ht => {
        val column = table.columns(ht.index+1)
        column.text = ht.text
        ht.icon.foreach(icon => column.graphic = new ImageView(icon))
      })

  /* adds the database rows to the table */
  def addPrimaryKeys(keys : PrimaryKeys) : Unit =
    displayKeyForFields(headings.addPrimaryKeys(keys))

  def addForeignKeys(keys : ForeignKeys) : Unit =
    displayKeyForFields(headings.addForeignKeys(keys))

  /* the unique id for the table */
  def getId: QueryId = queryId

  def getCheckedRows: List[Row] = checkedRows.rows

  def rowsNumber: Int = buffer.length

  def copySelectionToClipboard(includeHeaders : Boolean) : Unit =
    try {
      val toClipboard = new TableToClipboard(selectedRows(), fields)
      toClipboard.copySelectionToClipboard(includeHeaders)
      log.info(localization.selectionCopied)
    } catch {
      case ex : Exception => log.error(localization.errorCopyingSelection, ex)
    }

  def control : Parent = table
}

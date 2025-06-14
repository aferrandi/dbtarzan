package dbtarzan.gui.browsingtable

import dbtarzan.db.{ForeignKey, ForeignKeys}
import dbtarzan.gui.interfaces.TControlBuilder
import dbtarzan.gui.util.{ForeignKeyIcons, JFXUtil, TableIdLabel}
import dbtarzan.messages.TLogger
import scalafx.collections.ObservableBuffer
import scalafx.scene.Parent
import scalafx.scene.control.{ListView, TableCell, TableColumn, TableView, Tooltip}
import dbtarzan.localization.Localization
import scalafx.beans.property.{ObjectProperty, StringProperty}
import scalafx.scene.image.{Image, ImageView}
import scalafx.Includes.*


/**	foreign keys list */
class ForeignKeyList(localization : Localization, log: TLogger) extends TControlBuilder {
  private val buffer = ObservableBuffer.empty[ForeignKey]
  private val keyTable = buildTable()


  /* builds table with the two columns (name and description) */
  def buildTable(): TableView[ForeignKey] = new TableView[ForeignKey](buffer) {
    columns ++= List(directionColumn(), tableToColumn(), tableFromFields(), tableToFields())
    editable = false
    columnResizePolicy = javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
  }

  /* the column with the from table description  */
  private def tableFromColumn() = new TableColumn[ForeignKey, String] {
    text = localization.tableFrom
    cellValueFactory = { x => new StringProperty(TableIdLabel.toLabel(x.value.from.table)) }
    resizable = true
  }

  /* the column with the from table description  */
  private def tableFromFields() = new TableColumn[ForeignKey, String] {
    text = localization.columnsFrom
    cellValueFactory = { x => new StringProperty(x.value.from.fields.mkString(",")) }
    resizable = true
  }

  /* the column with the from table description  */
  private def tableToColumn() = new TableColumn[ForeignKey, String] {
    text = localization.tableTo
    cellValueFactory = { x => new StringProperty(TableIdLabel.toLabel(x.value.to.table)) }
    resizable = true
  }

  /* the column with the from table description  */
  private def tableToFields() = new TableColumn[ForeignKey, String] {
    text = localization.columnsTo
    cellValueFactory = { x => new StringProperty(x.value.to.fields.mkString(",")) }
    resizable = true
  }

  /* build the column on the left, that shows the icon (error, warn, info) */
  private def directionColumn() = new TableColumn[ForeignKey, Image] {
    cellValueFactory = { row => ObjectProperty(ForeignKeyIcons.iconForDirection(row.value.direction).delegate) }
    cellFactory = {
      (_: TableColumn[ForeignKey, Image]) =>
        new TableCell[ForeignKey, Image] {
          item.onChange {
            (_, _, newImage) => graphic = new ImageView(newImage)
          }
        }
    }
    maxWidth = 24
    minWidth = 24
  }


  /** need to show only the "to table" as cell text. And a tooltip for each cell	*/
  def addForeignKeys(newForeignKeys : ForeignKeys) : Unit = {
    setForeignKeys(newForeignKeys)
  }

  private def setForeignKeys(newForeignKeys: ForeignKeys): Unit = {
    def moreThanOneItem(l: List[?]) = l.length > 1
    log.debug(s"newForeignKeys $newForeignKeys")
    val allForeignKeys = buffer.toList ++ newForeignKeys.keys
    JFXUtil.bufferSet(buffer, allForeignKeys)
  }

  def setForeignKeysByPattern(newForeignKeys : ForeignKeys) : Unit = {
    buffer.clear()
    setForeignKeys(newForeignKeys)
  }

  /* foreign key double-clicked. handled by BrowsingTable that has knowledge of tables too */
  def onForeignKeySelected(useKey : (ForeignKey, Boolean)  => Unit) : Unit =
     JFXUtil.onAction(keyTable, { (selectedKey : ForeignKey, ctrlDown) =>
        log.debug(s"Selected $selectedKey")
        Option(selectedKey).foreach(key => useKey(key, ctrlDown))
      })

  def control : Parent = keyTable
}


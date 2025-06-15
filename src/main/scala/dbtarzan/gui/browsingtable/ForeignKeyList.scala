package dbtarzan.gui.browsingtable

import dbtarzan.db.{ForeignKey, ForeignKeys}
import dbtarzan.gui.interfaces.TControlBuilder
import dbtarzan.gui.util.{ForeignKeyIcons, JFXUtil, TableIdLabel, TableUtil}
import dbtarzan.localization.Localization
import dbtarzan.messages.TLogger
import scalafx.Includes.*
import scalafx.beans.property.{ObjectProperty, StringProperty}
import scalafx.collections.ObservableBuffer
import scalafx.scene.Parent
import scalafx.scene.control.{Label, SelectionMode, TableCell, TableColumn, TableView, TextArea}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{BorderPane, VBox}


/**	foreign keys list */
class ForeignKeyList(localization : Localization, log: TLogger) extends TControlBuilder {
  private val buffer = ObservableBuffer.empty[ForeignKey]
  private val keyTable = buildTable()
  private val keyDescription = new TextArea {
    text = ""
    editable = false
    wrapText = true
  }

  private val layout = new BorderPane {
    center = keyTable
  }


  /* builds table with the two columns (name and description) */
  def buildTable(): TableView[ForeignKey] = new TableView[ForeignKey](buffer) {
    columns ++= List(directionColumn(), tableToColumn(), tableFromFields(), tableToFields())
    editable = false
    columnResizePolicy = javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
    selectionModel().selectedItem.onChange(
      (_, _, key) => {
        keyDescription.visible = true
        keyDescription.text = keyToText(key)
        layout.bottom = keyDescription
      }
    )
  }

  private def keyToText(key: ForeignKey): String =
    List(
      s"Name:${key.name}",
      f"From Table:${TableIdLabel.toLabel(key.from.table)}",
      s"From Fields:${key.from.fields.mkString(" ")}",
      s"To Table:${TableIdLabel.toLabel(key.to.table)}",
      s"To Columns:${key.to.fields.mkString(" ")}"
    ).mkString("\n")

  /* the column with the from table description  */
  private def tableFromFields() = TableUtil.buildTextTableColumn[ForeignKey](localization.columnsFrom, _.value.from.fields.mkString(" "))

  /* the column with the from table description  */
  private def tableToColumn() = TableUtil.buildTextTableColumn[ForeignKey](localization.tableTo, x => TableIdLabel.toLabel(x.value.to.table))
  
  /* the column with the from table description  */
  private def tableToFields() = TableUtil.buildTextTableColumn[ForeignKey](localization.columnsFrom, _.value.from.fields.mkString(" "))
  
  /* build the column on the left, that shows the icon (error, warn, info) */
  private def directionColumn() = TableUtil.buildImageTableColumn((row: TableColumn.CellDataFeatures[ForeignKey, Image]) => ForeignKeyIcons.iconForDirection(row.value.direction))

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


  def control : Parent = layout
}


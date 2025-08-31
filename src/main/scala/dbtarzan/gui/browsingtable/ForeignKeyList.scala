package dbtarzan.gui.browsingtable

import dbtarzan.db.{ForeignKey, ForeignKeys}
import dbtarzan.gui.interfaces.TControlBuilder
import dbtarzan.gui.util.{ForeignKeyIcons, JFXUtil, TableIdLabel, TableUtil}
import dbtarzan.localization.Localization
import dbtarzan.messages.{QueryId, TLogger}
import org.apache.pekko.actor.ActorRef
import scalafx.Includes.*
import scalafx.collections.ObservableBuffer
import scalafx.scene.Parent
import scalafx.scene.control.{TableColumn, TableView, TextArea, ToggleButton}
import scalafx.scene.image.Image
import scalafx.scene.layout.BorderPane


/**	foreign keys list */
class ForeignKeyList(queryId : QueryId, dbActor: ActorRef, localization : Localization, log: TLogger) extends TControlBuilder {
  private var showRowsNumber = false
  private val buffer = ObservableBuffer.empty[ForeignKey]
  private val keyTable = buildTable()
  private val keyDescription = new TextArea {
    text = ""
    editable = false
    wrapText = true
    maxHeight = JFXUtil.averageCharacterHeight() * 7
  }
  private val buttonRowsNumber = new ToggleButton() {
    text = localization.foreignKeyRowsNumber
    selected.onChange((_, _, newValue) => {
      showRowsNumber = newValue
    })
  }

  private val layout = new BorderPane {
    center = new BorderPane {
      center = keyTable
      bottom = buttonRowsNumber
    }
  }
  
  /* builds table with the two columns (name and description) */
  def buildTable(): TableView[ForeignKey] = new TableView[ForeignKey](buffer) {
    columns ++= List(directionColumn(), tableToColumn(), tableFromFields(), tableToFields())
    editable = false
    columnResizePolicy = javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
  }

  private def keyToText(key: ForeignKey): String =
    List(
      s"${localization.name}: ${key.name}",
      f"${localization.tableFrom}: ${TableIdLabel.toLabel(key.from.table)}",
      s"${localization.columnsFrom}: ${key.from.fields.mkString(" ")}",
      s"${localization.tableTo}: ${TableIdLabel.toLabel(key.to.table)}",
      s"${localization.columnsTo}: ${key.to.fields.mkString(" ")}",
    ).mkString("\n")

  private def keyWithRowCountToText(key: ForeignKey, rowsCount: Int): String =
    List(
      keyToText(key),
      s"To Rows Number:$rowsCount",
    ).mkString("\n")


  /* the column with the from table description  */
  private def tableFromFields() = TableUtil.buildTextTableColumn[ForeignKey](localization.columnsFrom, _.value.from.fields.mkString(" "))

  /* the column with the from table description  */
  private def tableToColumn() = TableUtil.buildTextTableColumn[ForeignKey](localization.tableTo, x => TableIdLabel.toLabel(x.value.to.table))
  
  /* the column with the to table description  */
  private def tableToFields() = TableUtil.buildTextTableColumn[ForeignKey](localization.columnsTo, _.value.to.fields.mkString(" "))
  
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

  def showForeignKeyRowsNumber(foreigKey: ForeignKey, rowsNumber: Int): Unit = {
    keyDescription.text = keyWithRowCountToText(foreigKey, rowsNumber)
  }

  /* foreign key double-clicked. handled by BrowsingTable that has knowledge of tables too */
  def onForeignKeyDoubleClicked(useKey : (ForeignKey, Boolean)  => Unit) : Unit =
     JFXUtil.onAction(keyTable, { (selectedKey : ForeignKey, ctrlDown) =>
        log.debug(s"Selected $selectedKey")
        Option(selectedKey).foreach(key => useKey(key, ctrlDown))
      })

  /* foreign key selected. handled by BrowsingTable that has knowledge of tables too */
  def onForeignKeySelected(useKey : ForeignKey  => Unit) : Unit =
    keyTable.selectionModel().selectedItem.onChange(
      (_, _, key) => {
        keyDescription.visible = true
        keyDescription.text = keyToText(key)
        layout.bottom = keyDescription
        if(showRowsNumber)
          useKey(key)
      }
    )
  
  
  def control : Parent = layout
}


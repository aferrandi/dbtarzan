package dbtarzan.gui

import scalafx.scene.control.{ TableView, SplitPane }
import scalafx.scene.layout.BorderPane
import scalafx.scene.Parent
import dbtarzan.db.{ ForeignKey, ForeignKeyMapper, Filter, FollowKey, Fields}
import dbtarzan.gui.util.JFXUtil
import dbtarzan.messages._
import akka.actor.ActorRef

/**
  table + constraint input box + foreign keys
*/
class BrowsingTable(dbActor : ActorRef, guiActor : ActorRef, dbTable : dbtarzan.db.Table, databaseId : DatabaseId) extends TTable with TControlBuilder {
  private val foreignKeyList = new ForeignKeyList()
  private val id = IDGenerator.tableId(databaseId, dbTable.tableDescription.name)
  private val table = new Table(dbActor, id, dbTable)
  private val queryText = new QueryText()
  private val progressBar = new TableProgressBar()
  private val layout = new BorderPane {
    top = JFXUtil.withLeftTitle(queryText.textBox, "Where:")
    center = buildSplitPane()
    bottom = progressBar.control
  }
  foreignKeyList.onForeignKeySelected(key => openTableConnectedByForeignKey(key))

  private def openTableConnectedByForeignKey(key : ForeignKey) : Unit = {
      println("Selected "+key)
      val checkedRows = table.getCheckedRows
      if(!checkedRows.isEmpty) {
        dbActor ! QueryColumnsFollow(databaseId, key.to.table, FollowKey(dbTable.columnNames, key, checkedRows))
      } else {
        dbActor ! QueryColumns(databaseId, key.to.table)
        guiActor ! Warning("No rows selected with key "+key.name+". Open table "+key.to.table+" without filter.")
      }
  } 
  /* builds the split panel containing the table and the foreign keys list */
  private def buildSplitPane() =new SplitPane {
        maxHeight = Double.MaxValue
        maxWidth = Double.MaxValue
        val foreignKeyListWithTitle = JFXUtil.withTitle(foreignKeyList.control, "Foreign keys") 
        items.addAll(table.control, foreignKeyListWithTitle)
        dividerPositions = 0.8
        SplitPane.setResizableWithParent(foreignKeyListWithTitle, false)
  }

  /* if someone entere a query in the text box on the top of the table it creates a new table that depends by this query */
  def onTextEntered(useTable : dbtarzan.db.Table => Unit) : Unit =
    queryText.onEnter(text => {
        val tableWithFilters = dbTable.withAdditionalFilter(Filter(text))
        useTable(tableWithFilters)
    })    

  /* adds the following rows to the table */
  def addRows(rows : ResponseRows) : Unit  = { 
    table.addRows(rows.rows)
    progressBar.receivedRows()
  }

  /* adds the foreign keys to the foreign key list */
  def addForeignKeys(keys : ResponseForeignKeys) : Unit = {
    foreignKeyList.addForeignKeys(keys.keys)
    progressBar.receivedForeignKeys()
  }

  def getId = id
  def control : Parent = layout
}
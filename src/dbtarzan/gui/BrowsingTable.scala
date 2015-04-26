package dbtarzan.gui

import scalafx.scene.control.{ TableView, SplitPane }
import scalafx.scene.layout.BorderPane
import dbtarzan.db.{ ForeignKey, ForeignKeyMapper, Filter, FollowKey, Fields}
import dbtarzan.gui.util.JFXUtil
import dbtarzan.messages._
import akka.actor.ActorRef

/**
  table + constraint input box + foreign keys
*/
class BrowsingTable(dbActor : ActorRef, guiActor : ActorRef, dbTable : dbtarzan.db.Table, databaseId : DatabaseId) extends TTable {
  val foreignKeyList = new ForeignKeyList()
  val id = IDGenerator.tableId(databaseId, dbTable.tableDescription.name)
  val table = new Table(dbActor, id, dbTable)
  val queryText = new QueryText()
  val progressBar = new TableProgressBar()
  val layout = new BorderPane {
    top = JFXUtil.withLeftTitle(queryText.textBox, "Where:")
    center = buildSplitPane()
    bottom = progressBar.bar
  }
  foreignKeyList.onForeignKeySelected(key => openTableConnectedByForeignKey(key))

  private def openTableConnectedByForeignKey(key : ForeignKey) : Unit = {
      println("Selected "+key)
      val checkedRows = table.checkedRows.rows
      if(!checkedRows.isEmpty) {
        dbActor ! QueryColumnsFollow(databaseId, key.to.table, FollowKey(dbTable.columnNames, key, checkedRows))
      } else {
        dbActor ! QueryColumns(databaseId, key.to.table)
        guiActor ! Warning("No rows selected with key "+key.name+". Open table "+key.to.table+" without filter.")
      }
  } 

  private def buildSplitPane() =new SplitPane {
        maxHeight = Double.MaxValue
        maxWidth = Double.MaxValue
        items.addAll(table.table, JFXUtil.withTitle(foreignKeyList.list, "Foreign keys"))
        dividerPositions = 0.8
        SplitPane.setResizableWithParent(foreignKeyList.list, false)
  }

  def onTextEntered(useTable : dbtarzan.db.Table => Unit) : Unit =
    queryText.onEnter(text => {
        val tableWithFilters = dbTable.withAdditionalFilter(Filter(text))
        useTable(tableWithFilters)
    })    

  def addRows(rows : ResponseRows) : Unit  = { 
    table.addRows(rows.rows)
    progressBar.receivedRows()
  }

  def addForeignKeys(keys : ResponseForeignKeys) : Unit = {
    foreignKeyList.addForeignKeys(keys.keys)
    progressBar.receivedForeignKeys()
  }
}
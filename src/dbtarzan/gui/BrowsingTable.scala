package dbtarzan.gui

import scalafx.scene.control.{ TableView, SplitPane }
import scalafx.scene.layout.BorderPane
import dbtarzan.db.{ ForeignKey, ForeignKeyMapper, Constraint, FollowKey, Fields}
import dbtarzan.gui.util.JFXUtil
import dbtarzan.messages._
import akka.actor.ActorRef

/**
  table + constraint input box + foreign keys
*/
class BrowsingTable(dbActor : ActorRef, dbTable : dbtarzan.db.Table, databaseName : String) extends TTable {
  val foreignKeyList = new ForeignKeyList()
  val id = IDGenerator.tableId(databaseName, dbTable.tableDescription.name)
  val table = new Table(dbActor, id, dbTable)
  val queryText = new QueryText()
  val progressBar = new TableProgressBar()

  val layout = new BorderPane {
    top = JFXUtil.withLeftTitle(queryText.textBox, "Where:")
    center = buildSplitPane()
    bottom =  progressBar.bar
  }
    foreignKeyList.onForeignKeySelected(key => {
      println("Selected "+key)
      val selectedRows = table.selected.rows
      // val mapper = dbTable.withKey(key)      
      if(!selectedRows.isEmpty) {
        dbActor ! QueryColumnsFollow(DatabaseId(databaseName), key.to.table, FollowKey(dbTable.columnNames, key, selectedRows))
      } else 
        println("No rows selected")
    })

  private def buildSplitPane() =new SplitPane {
        maxHeight = Double.MaxValue
        maxWidth = Double.MaxValue
        items.addAll(table.table, JFXUtil.withTitle(foreignKeyList.list, "Foreign keys"))
        dividerPositions = 0.8
        SplitPane.setResizableWithParent(foreignKeyList.list, false)
    }

  def onTextEntered(useTable : dbtarzan.db.Table => Unit) : Unit =
    queryText.onEnter(text => {
      val tableWithConstraints = dbTable.withAdditionalConstraint(Constraint(text))
      useTable(tableWithConstraints)
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
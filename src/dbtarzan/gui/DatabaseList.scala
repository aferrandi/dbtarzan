package dbtarzan.gui

import scalafx.scene.control.{ ListView, ListCell, SplitPane, ContextMenu, MenuItem }
import scalafx.scene.Parent
import scalafx.collections.ObservableBuffer 
import dbtarzan.gui.util.JFXUtil
import dbtarzan.messages.DatabaseIds
import dbtarzan.db.DatabaseId

/*	The list of database to choose from*/
class DatabaseList() extends TControlBuilder with TDatabaseList {
  private val menuForeignKeyToFile = new MenuItem("Build foreign keys file")
  private val buffer = ObservableBuffer.empty[DatabaseId]
  private val list = new ListView[DatabaseId](buffer) {
  	SplitPane.setResizableWithParent(this, false) 
  	contextMenu = new ContextMenu(menuForeignKeyToFile)   
    cellFactory = { _ => buildCell() }
  }

  private def buildCell() = new ListCell[DatabaseId] {
      item.onChange { (_, _, _) => 
	          Option(item.value).foreach(databaseId => {
		          text.value = databaseId.databaseName
	      	  })
	        }} 

  def setDatabaseIds(databaseIds: DatabaseIds) : Unit = {
    println("Got new database list:"+databaseIds)
    buffer.clear()
    buffer ++= databaseIds.names.sortBy(_.databaseName)
  }

  def onDatabaseSelected(use : DatabaseId => Unit) : Unit = 
      JFXUtil.onAction(list, { selectedDatabaseId : DatabaseId => 
        println("Selected "+selectedDatabaseId.databaseName)
        use(selectedDatabaseId)
      })

  def onForeignKeyToFile(use : DatabaseId => Unit) : Unit =
  	JFXUtil.onContextMenu(menuForeignKeyToFile, list, {selectedDatabaseId : DatabaseId => 
        println("Selected "+selectedDatabaseId.databaseName)
        use(selectedDatabaseId)
      })

  def control : Parent = list
}
package dbtarzan.gui

import scalafx.scene.control.{ ListView, SplitPane, ContextMenu, MenuItem }
import scalafx.scene.Parent
import scalafx.collections.ObservableBuffer 
import scalafx.Includes._
import dbtarzan.gui.util.JFXUtil
import dbtarzan.messages.DatabaseNames
import scalafx.event.ActionEvent

/**
	The list of database to choose from
*/
class DatabaseList() extends TControlBuilder with TDatabaseList {
  private val menuForeignKeyToFile = new MenuItem("Build foreign keys file")
  private val buffer = ObservableBuffer.empty[String]
  private val list = new ListView[String](buffer) {
  	SplitPane.setResizableWithParent(this, false) 
  	contextMenu = new ContextMenu(menuForeignKeyToFile)   
  }

  def setDatabases(databases: DatabaseNames) : Unit = {
    println("Got new database list:"+databases)
    buffer.clear()
    buffer ++= databases.names
  }

  def onDatabaseSelected(use : String => Unit) : Unit = 
      JFXUtil.onAction(list, { selectedDatabase : String => 
        println("Selected "+selectedDatabase)
        use(selectedDatabase)
      })
  def onForeignKeyToFile(use : String => Unit) : Unit =
  	JFXUtil.onContextMenu(menuForeignKeyToFile, list, {selectedDatabase : String => 
        println("Selected "+selectedDatabase)
        use(selectedDatabase)
      })
  def control : Parent = list
}

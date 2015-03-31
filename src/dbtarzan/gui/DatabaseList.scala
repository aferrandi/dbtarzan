package dbtarzan.gui

import scalafx.scene.control.{ ListView, SplitPane, ContextMenu, MenuItem }

import scalafx.Includes._
import dbtarzan.gui.util.JFXUtil


/**
	The list of database to choose from
*/
class DatabaseList(databases :List[String]) {
  val menuForeignKeyToFile = new MenuItem("Build foreign keys file")
  val list = new ListView[String](databases) {
  	SplitPane.setResizableWithParent(this, false) 
  	contextMenu = new ContextMenu(menuForeignKeyToFile)   
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
}

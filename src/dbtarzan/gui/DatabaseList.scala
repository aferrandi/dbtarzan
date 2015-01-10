package dbtarzan.gui

import scalafx.scene.control.{ ListView, SplitPane }
import scalafx.Includes._
import dbtarzan.gui.util.JFXUtil


/**
	The list of database to choose from
*/
class DatabaseList(databases :List[String]) {
  val list = new ListView[String](databases) {
  	SplitPane.setResizableWithParent(this, false)    
  }

  def onDatabaseSelected(use : String => Unit) : Unit = 
      JFXUtil.onAction(list, { selectedDatabase : String => 
        println("Selected "+selectedDatabase)
        use(selectedDatabase)
      })
}

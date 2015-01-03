package dbtarzan.gui

import java.io.File
import scalafx.collections.ObservableBuffer 
import scalafx.scene.control.{ ListView, ListCell, Tooltip }
import scalafx.Includes._
import dbtarzan.config.{ Config, ConfigReader }
import akka.actor.ActorRef
import dbtarzan.gui.util.JFXUtil
import scalafx.scene.control.SplitPane

/**
	The list of database to choose from
*/
class DatabaseList(guiActor : ActorRef) {

  val config = new Config(ConfigReader.read(new File("connections.config")))
  val list = new ListView[String](config.connections()) {
  	SplitPane.setResizableWithParent(this, false)    
  }

  def onDatabaseSelected(use : (String, ActorRef) => Unit) : Unit = 
      JFXUtil.onAction(list, { selectedDatabase : String => 
        println("Selected "+selectedDatabase)      
        val dbActor = config.connect(selectedDatabase, guiActor).get
        use(selectedDatabase, dbActor)
      })
}

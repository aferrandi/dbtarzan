package dbtarzan.gui

import java.io.File
import scalafx.collections.ObservableBuffer 
import scalafx.scene.control.{ ListView, ListCell, Tooltip }
import scalafx.Includes._
import dbtarzan.config.{ Config, ConfigReader }
import akka.actor.ActorRef
import dbtarzan.gui.util.JFXUtil
import scalafx.scene.control.SplitPane
import dbtarzan.db.ConnectionBuilder

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
        val optData = config.connect(selectedDatabase)      
        val oldDbActor = optData.map(data => ConnectionBuilder.build(data, guiActor))
        oldDbActor.foreach(dbActor => use(selectedDatabase, dbActor))
      })
}

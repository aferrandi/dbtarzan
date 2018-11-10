package dbtarzan.gui.actor

import akka.actor.Actor
import dbtarzan.gui.{ TDatabases, TLogs, TDatabaseList}
import dbtarzan.messages._
import scalafx.application.Platform

/* Receives messages from the other actors (DatabaseWorker and ConfigWorker) and thread-safely updates the GUIf */
class GUIWorker(databases : TDatabases, logs : TLogs, dbList : TDatabaseList) extends Actor {
  private var log = new Logger(self)  
  def receive = {
        case rsp: TWithTableId => Platform.runLater { databases.handleMessage(rsp) }
        case rsp: TWithDatabaseId => Platform.runLater { databases.handleMessage(rsp) }
        case msg: TLogMessage => Platform.runLater { logs.addLogMessage(msg) }
        case msg: DatabaseIds => Platform.runLater { 
            println("Delivery databases"+msg)
            dbList.setDatabaseIds(msg) 
        }
        case err: ErrorDatabaseAlreadyOpen => Platform.runLater { 
            databases.showDatabase(err.databaseId)
            log.warning("Database "+err.databaseId.databaseName+" already open")
        }
	}
}
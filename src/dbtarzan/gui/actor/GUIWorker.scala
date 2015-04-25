package dbtarzan.gui.actor

import akka.actor.Actor
import dbtarzan.gui.{ TDatabases, TLogs}
import dbtarzan.messages._
import scalafx.application.Platform

/*
    Receives messages from the other actors (DatabaseWorker and ConfigWorker) and thread-safely updates the GUIf 
*/
class GUIWorker(databases : TDatabases, logs : TLogs) extends Actor {
  def receive = {
    case rsp : ResponseRows =>  Platform.runLater { databases.addRows(rsp) }

    case rsp: ResponseTables => Platform.runLater { databases.addTables(rsp) }

    case rsp : ResponseColumns => Platform.runLater { databases.addColumns(rsp) }

    case rsp: ResponseForeignKeys => Platform.runLater { databases.addForeignKeys(rsp) }	
	
    case rsp: ResponseColumnsFollow => Platform.runLater { databases.addColumnsFollow(rsp) }

    case rsp: ResponseDatabase => Platform.runLater { databases.addDatabase(rsp) } 

    case rsp: ResponseCloseDatabase => Platform.runLater { databases.removeDatabase(rsp) } 

    case rsp: ResponseCloseTables => Platform.runLater { databases.removeTables(rsp) }

    case msg : TLogMessage => Platform.runLater { logs.addLogMessage(msg) }

    case err : ErrorDatabaseAlreadyOpen => Platform.runLater { 
                databases.showDatabase(err.databaseName)
                logs.addLogMessage(Warning("Database "+err.databaseName+" already open"))
            }
	}

}
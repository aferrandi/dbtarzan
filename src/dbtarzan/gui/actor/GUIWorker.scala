package dbtarzan.gui.actor

import akka.actor.Actor
import dbtarzan.gui.{ TDatabases, TLogs, TDatabaseList}
import dbtarzan.messages._
import scalafx.application.Platform

/*
    Receives messages from the other actors (DatabaseWorker and ConfigWorker) and thread-safely updates the GUIf 
*/
class GUIWorker(databases : TDatabases, logs : TLogs, dbList : TDatabaseList) extends Actor {
  private var log = new Logger(self)  
  def receive = {
        case rsp: ResponseRows => Platform.runLater { databases.addRows(rsp) }

        case rsp: ResponseTables => Platform.runLater { databases.addTables(rsp) }

        case rsp: ResponseColumns => Platform.runLater { databases.addColumns(rsp) }

        case rsp: ResponseForeignKeys => Platform.runLater { databases.addForeignKeys(rsp) }	
        
        case rsp: ResponsePrimaryKeys => Platform.runLater { databases.addPrimaryKeys(rsp) }	

        case rsp: ResponseColumnsFollow => Platform.runLater { databases.addColumnsFollow(rsp) }

        case rsp: ResponseDatabase => Platform.runLater { databases.addDatabase(rsp) } 

        case rsp: ResponseCloseDatabase => Platform.runLater { databases.removeDatabase(rsp) } 

        case rsp: ResponseCloseTables => Platform.runLater { databases.removeTables(rsp) }

        case msg: RequestRemovalTabsAfter => Platform.runLater { databases.requestRemovalTabsAfter(msg) }

        case msg: RequestRemovalTabsBefore => Platform.runLater { databases.requestRemovalTabsBefore(msg) }

        case msg: RequestRemovalAllTabs => Platform.runLater { databases.requestRemovalAllTabs(msg) }

        case msg: TLogMessage => Platform.runLater { logs.addLogMessage(msg) }

        case msg: CopySelectionToClipboard => Platform.runLater { databases.copySelectionToClipboard(msg) }

        case msg: CopySQLToClipboard => Platform.runLater { databases.copySQLToClipboard(msg) }

        case msg: CheckAllTableRows => Platform.runLater { databases.checkAllTableRows(msg) }

        case msg: CheckNoTableRows => Platform.runLater { databases.checkNoTableRows(msg) }

        case msg: SwitchRowDetails => Platform.runLater { databases.switchRowDetails(msg) }

        case msg: DatabaseNames => Platform.runLater { 
            println("Delivery databases"+msg)
            dbList.setDatabases(msg) 
        }

        case err: ErrorDatabaseAlreadyOpen => Platform.runLater { 
            databases.showDatabase(err.databaseName)
            log.warning("Database "+err.databaseName+" already open")
        }
	}
}
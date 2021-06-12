package dbtarzan.gui.actor

import akka.actor.Actor
import scalafx.application.Platform
import dbtarzan.gui.{TDatabaseList, TDatabases, TLogs, TGlobal}
import dbtarzan.messages._
import dbtarzan.localization.Localization

/* Receives messages from the other actors (DatabaseWorker and ConfigWorker) and thread-safely updates the GUIf */
class GUIActor(
                 databases : TDatabases,
                 logs : TLogs,
                 dbList : TDatabaseList,
                 main: TGlobal,
                 localization : Localization
   ) extends Actor {
  private var log = new Logger(self)  
  def receive = {
        case rsp: TWithQueryId => Platform.runLater { databases.handleQueryIdMessage(rsp) }
        case rsp: TWithDatabaseId => Platform.runLater { databases.handleDatabaseIdMessage(rsp) }
        case rsp: TWithTableId => Platform.runLater { databases.handleTableIdMessage(rsp) }
        case rsp: ResponseTestConnection => Platform.runLater { main.handleTestConnectionResponse(rsp) }
        case rsp: ResponseSchemaExtraction => Platform.runLater { main.handleSchemaExtractionResponse(rsp) }
        case msg: TLogMessage => Platform.runLater { logs.addLogMessage(msg) }
        case msg: DatabaseIds => Platform.runLater { 
            println("Delivery databases "+msg)
            dbList.setDatabaseIds(msg) 
        }
        case err: ErrorDatabaseAlreadyOpen => Platform.runLater { 
            databases.showDatabase(err.databaseId)
            log.warning(localization.databaseAlreadyOpen(err.databaseId.databaseName))
        }
	}
}
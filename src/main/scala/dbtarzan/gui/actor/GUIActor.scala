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
  private val log = new Logger(self)

  def runLater[R](op: => R): Unit = {
    Platform.runLater {
      try { op } catch { case e : Exception => log.error("UI", e) }
    }
  }

  def receive = {
        case rsp: TWithQueryId => runLater { databases.handleQueryIdMessage(rsp) }
        case rsp: TWithDatabaseId => runLater { databases.handleDatabaseIdMessage(rsp) }
        case rsp: TWithTableId => runLater { databases.handleTableIdMessage(rsp) }
        case rsp: ResponseTestConnection => runLater { main.handleTestConnectionResponse(rsp) }
        case rsp: ResponseSchemaExtraction => runLater { main.handleSchemaExtractionResponse(rsp) }
        case msg: TLogMessage => runLater { logs.addLogMessage(msg) }
        case msg: DatabaseIds => runLater { dbList.setDatabaseIds(msg) }
        case err: ErrorDatabaseAlreadyOpen => runLater {
            databases.showDatabase(err.databaseId)
            log.warning(localization.databaseAlreadyOpen(err.databaseId.databaseName))
        }
	}
}
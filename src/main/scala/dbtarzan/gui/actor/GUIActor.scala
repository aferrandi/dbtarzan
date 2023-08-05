package dbtarzan.gui.actor

import org.apache.pekko.actor.Actor
import dbtarzan.gui.interfaces.{TDatabaseList, TDatabases, TGlobal, TLogs}
import scalafx.application.Platform
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

  def receive: PartialFunction[Any,Unit] = {
        case rsp: TWithQueryId => runLater { databases.handleQueryIdMessage(rsp) }
        case rsp: TWithDatabaseId => runLater { databases.handleDatabaseIdMessage(rsp) }
        case rsp: TWithTableId => runLater { databases.handleTableIdMessage(rsp) }
        case rsp: ResponseTestConnection => runLater { main.handleTestConnectionResponse(rsp) }
        case rsp: ResponseSchemaExtraction => runLater { main.handleSchemaExtractionResponse(rsp) }
        case msg: TLogMessage => runLater { logs.addLogMessage(msg) }
        case msg: DatabaseIds => runLater { dbList.setDatabaseIds(msg) }
        case err: ErrorDatabaseAlreadyOpen => runLater {
            databases.showDatabase(err.databaseId)
            log.warning(localization.databaseAlreadyOpen(DatabaseIdUtil.databaseIdText(err.databaseId)))
        }
	}
}
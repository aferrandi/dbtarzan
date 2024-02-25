package dbtarzan.gui.main

import dbtarzan.db.{DatabaseId, DatabaseInfo}
import dbtarzan.gui.interfaces.{TControlBuilder, TDatabaseList}
import dbtarzan.gui.util.{FilterText, JFXUtil}
import dbtarzan.localization.Localization
import dbtarzan.log.actor.Logger
import dbtarzan.messages.{DatabaseIdUtil, DatabaseIds, DatabaseInfos, QueryDatabasesByPattern, QueryTablesByPattern, ResponseDatabasesByPattern, TWithDatabases}
import org.apache.pekko.actor.ActorRef
import scalafx.collections.ObservableBuffer
import scalafx.scene.Parent
import scalafx.scene.control.*
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.BorderPane
import scalafx.scene.paint.Color


/*	The list of database to choose from*/
class DatabaseListWithFilter(connectionsActor: ActorRef, log: Logger, localization : Localization) extends TControlBuilder with TDatabaseList {
  private val databaseList = new DatabaseList(log, localization)
  private val filterText = new FilterText(connectionsActor ! QueryDatabasesByPattern(_), localization)
  private val pane = new BorderPane {
    top = filterText.control
    center = databaseList.control
  }
  def handleDatabasesData(msg: TWithDatabases) : Unit = msg match {
    case rsp: DatabaseInfos => setDatabaseInfos(rsp.infos)
    case rsp: ResponseDatabasesByPattern => databaseList.setDatabaseInfos (rsp.infos)
  }

  def setDatabaseInfos(databaseInfos : List[DatabaseInfo]) : Unit = {
    filterText.clean()
    databaseList.setDatabaseInfos(databaseInfos)
  }

  def onDatabaseSelected(use : DatabaseInfo => Unit) : Unit =
    databaseList.onDatabaseSelected(use)
  def onForeignKeyToFile(use : DatabaseInfo => Unit) : Unit =
    databaseList.onForeignKeyToFile(use)
  def control : Parent = pane
}
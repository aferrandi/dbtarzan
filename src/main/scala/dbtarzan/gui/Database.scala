package dbtarzan.gui

import org.apache.pekko.actor.ActorRef
import dbtarzan.db.{DatabaseId, TableId, JobId}
import dbtarzan.gui.database.{DatabaseButtonBar, TableListWIthFilter, TableTabs}
import dbtarzan.gui.foreignkeys.{VirtualForeignKeysEditor, VirtualForeignKeysEditorStarter}
import dbtarzan.gui.interfaces.TControlBuilder
import dbtarzan.gui.jobs.Jobs
import dbtarzan.gui.util.{FilterText, JFXUtil}
import dbtarzan.localization.Localization
import dbtarzan.log.actor.Logger
import dbtarzan.messages.*
import scalafx.Includes.*
import scalafx.event.ActionEvent
import scalafx.scene.Parent
import scalafx.scene.control.*
import scalafx.scene.layout.{BorderPane, FlowPane, VBox}
import scalafx.stage.Stage


/* A panel containing all the tabs related to a database */
class Database (dbActor : ActorRef, guiActor : ActorRef, databaseId : DatabaseId, localization : Localization, tableIds: List[TableId], log: Logger) extends TControlBuilder {
  private val tableListWithSearch = new TableListWIthFilter(dbActor, databaseId, tableIds, localization)
  private val jobs = new Jobs(dbActor, guiActor, localization, log)
  private var virtualForeignKeyEditor : Option[VirtualForeignKeysEditor] = Option.empty
  tableListWithSearch.onTableSelected(tableId => createJobFromTableId(tableId))

  private def createJobFromTableId(tableId: TableId): Unit = {
    val jobId = jobs.createJobWith(tableId)
    dbActor ! QueryColumns(TableInJobId(tableId, jobId))
  }

  private val pane = new SplitPane {
    private val tableListWithTitle = new BorderPane {
      top = new VBox() {
        children = List(new Label(localization.tables), DatabaseButtonBar.buildButtonBar(dbActor, databaseId, localization))
      }
      center = tableListWithSearch.control
    }
    items.addAll(tableListWithTitle, jobs.control)
    dividerPositions = 0.20
    SplitPane.setResizableWithParent(tableListWithTitle, value = false)
  }

  def control : Parent = pane

  private def stage() : Stage = 
    new Stage(pane.scene().window().asInstanceOf[javafx.stage.Stage])

  def handleQueryIdMessage(msg: TWithQueryId) : Unit =
    jobs.handleQueryIdMessage(msg)

  def handleDatabaseIdMessage(msg: TWithDatabaseId) : Unit = msg match {
    case tables : ResponseTablesByPattern => tableListWithSearch.addTableNames(tables.tabeIds)
    case virtualKeys: ResponseVirtualForeignKeys =>  openVirtualForeignKeysEditor(virtualKeys)
    case msg => log.error(localization.errorDatabaseMessage(msg))
  }

  def handleJobIdMessage(msg: TWithJobId) : Unit =
    jobs.handleJobIdMessage(msg)


  private def openVirtualForeignKeysEditor(virtualKeys: ResponseVirtualForeignKeys): Unit = {
    virtualForeignKeyEditor = Some(VirtualForeignKeysEditorStarter.openVirtualForeignKeysEditor(
      stage(),
      dbActor,
      databaseId,
      tableIds,
      localization,
      log
    ))
    virtualForeignKeyEditor.foreach(_.handleForeignKeys(virtualKeys.keys))
  }

  def handleTableIdMessage(msg: TWithTableId) : Unit = msg match {
    case columns : ResponseColumnsForForeignKeys => virtualForeignKeyEditor.foreach(_.handleColumns(columns.tableId, columns.columns))
    case _ => jobs.handleTableIdMessage(msg)
  }

  def getId : DatabaseId = databaseId

  def currentTableId: Option[QueryId] =  jobs.currentTableId

  def currentJobId: Option[JobId] = jobs.currentJobId
}
package dbtarzan.gui

import org.apache.pekko.actor.ActorRef
import dbtarzan.db.{DatabaseId, TableId}
import dbtarzan.gui.foreignkeys.{AdditionalForeignKeysEditor, AdditionalForeignKeysEditorStarter}
import dbtarzan.gui.interfaces.TControlBuilder
import dbtarzan.gui.util.{FilterText, JFXUtil}
import dbtarzan.localization.Localization
import dbtarzan.messages._
import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.scene.Parent
import scalafx.scene.control._
import scalafx.scene.layout.{BorderPane, FlowPane}
import scalafx.stage.Stage

/* A panel containing all the tabs related to a database */
class Database (dbActor : ActorRef, guiActor : ActorRef, databaseId : DatabaseId, localization : Localization, tableIds: List[TableId]) extends TControlBuilder {
  private val log = new Logger(guiActor)
  private val tableList = new TableList(tableIds)
  private val tableTabs = new TableTabs(dbActor, guiActor, localization)
  private var additionalForeignKeyEditor : Option[AdditionalForeignKeysEditor] = Option.empty
  tableList.onTableSelected(tableId => dbActor ! QueryColumns(tableId))
  private val filterText = new FilterText(dbActor ! QueryTablesByPattern(databaseId, _), localization)
  private val pane = new SplitPane {
    private val tableListWithTitle = new BorderPane {
      top = new FlowPane {
        children = List(buildMenu(), new Label(localization.tables))
      }
      center = new BorderPane {
        top = filterText.control
        center = tableList.control
      }
    }
    items.addAll(tableListWithTitle, tableTabs.control)
    dividerPositions = 0.20
    SplitPane.setResizableWithParent(tableListWithTitle, value = false)
  }

  private def buildMenu() = new MenuBar {
    menus = List(
      new Menu(JFXUtil.threeLines) {
        items = List(
          new MenuItem(localization.connectionReset) {
            onAction = {
              (_: ActionEvent) => dbActor ! QueryReset(databaseId)
            }
          },
          new MenuItem(localization.openAdditionalForeignKeys) {
            onAction = {
              (_: ActionEvent) => {
                additionalForeignKeyEditor = Some(AdditionalForeignKeysEditorStarter.openAdditionalForeignKeysEditor(
                  stage(),                 
                  dbActor, 
                  guiActor,
                  databaseId,
                  tableIds,
                  localization
                  ))
              }
            }
          }
        )
      }
    )
    stylesheets += "orderByMenuBar.css"
  }

  def control : Parent = pane

  private def stage() : Stage = 
    new Stage(pane.scene().window().asInstanceOf[javafx.stage.Stage])

  def handleQueryIdMessage(msg: TWithQueryId) : Unit = 
    tableTabs.handleQueryIdMessage(msg)

  def handleDatabaseIdMessage(msg: TWithDatabaseId) : Unit = msg match {
    case tables : ResponseTablesByPattern => tableList.addTableNames(tables.tabeIds)
    case tables : ResponseCloseTables => tableTabs.removeTables(tables.ids)
    case _: RequestRemovalAllTabs => tableTabs.requestRemovalAllTabs()
    case additionalKeys: ResponseAdditionalForeignKeys =>  additionalForeignKeyEditor.foreach(_.handleForeignKeys(additionalKeys.keys))
    case _ => log.error(localization.errorDatabaseMessage(msg))
  }  

  def handleTableIdMessage(msg: TWithTableId) : Unit = msg match {
    case columns : ResponseColumns => tableTabs.addColumns(columns)
    case columns : ResponseColumnsFollow => tableTabs.addColumnsFollow(columns)
    case columns : ResponseColumnsForForeignKeys => additionalForeignKeyEditor.foreach(_.handleColumns(columns.tableId, columns.columns))
    case _ => log.error(localization.errorTableMessage(msg))
  }  

  def getId : DatabaseId = databaseId

  def currentTableId : Option[QueryId] =  tableTabs.currentTableId  
}
package dbtarzan.gui

import scalafx.stage.Stage
import scalafx.scene.control.{Label, Menu, MenuBar, MenuItem, SplitPane, TextField}
import scalafx.scene.layout.{BorderPane, FlowPane}
import scalafx.scene.Parent
import scalafx.Includes._
import akka.actor.ActorRef
import scalafx.event.ActionEvent
import scalafx.geometry.Insets
import dbtarzan.gui.foreignkeys.{AdditionalForeignKeysEditor, AdditionalForeignKeysEditorStarter}
import dbtarzan.messages._
import dbtarzan.gui.util.JFXUtil
import dbtarzan.db.{DatabaseId, TableId, TableNames}
import dbtarzan.localization.Localization

/* A panel containing all the tabs related to a database */
class Database (dbActor : ActorRef, guiActor : ActorRef, databaseId : DatabaseId, localization : Localization, tableNames: TableNames) extends TControlBuilder {
  private val log = new Logger(guiActor)
  private val tableList = new TableList(tableNames)
  private val tableTabs = new TableTabs(dbActor, guiActor, databaseId, localization)  
  private var additionalForeignKeyEditor : Option[AdditionalForeignKeysEditor] = Option.empty
  tableList.onTableSelected(tableName => dbActor ! QueryColumns(TableId(databaseId, tableName)))
  private val filterText = new TextField() { 
    promptText = localization.filter
    margin = Insets(0,0,3,0)
    text.onChange { (value , oldValue, newValue) => {
        val optValue = Option(newValue)
        optValue.foreach({ dbActor ! QueryTablesByPattern(databaseId, _)  })
      }}
  }
  private val pane = new SplitPane {
    private val tableListWithTitle = new BorderPane {
      top = new FlowPane {
        children = List(buildMenu(), new Label(localization.tables))
      }
      center = new BorderPane {
        top = filterText
        center = tableList.control
      }
    }
    items.addAll(tableListWithTitle, tableTabs.control)
    dividerPositions = 0.20
    SplitPane.setResizableWithParent(tableListWithTitle, false)
  }

	private def buildMenu() = new MenuBar {
		menus = List(
		  new Menu(JFXUtil.threeLines) {
		    items = List(
		      new MenuItem(localization.connectionReset) {
		        onAction = {
		          e: ActionEvent => dbActor ! QueryReset(databaseId)
		        }
		      },
		      new MenuItem(localization.openAdditionalForeignKeys) {
		        onAction = {
		          e: ActionEvent => {
                additionalForeignKeyEditor = Some(AdditionalForeignKeysEditorStarter.openAdditionalForeignKeysEditor(
                  stage(),                 
                  dbActor, 
                  guiActor,
                  databaseId,
                  tableList.tableNames,
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
    new Stage(pane.scene.window().asInstanceOf[javafx.stage.Stage])

  def handleQueryIdMessage(msg: TWithQueryId) : Unit = 
    tableTabs.handleQueryIdMessage(msg)

  def handleDatabaseIdMessage(msg: TWithDatabaseId) : Unit = msg match {
    case tables : ResponseTablesByPattern => tableList.addTableNames(tables.names)
    case tables : ResponseCloseTables => tableTabs.removeTables(tables.ids)
    case columns : ResponseColumnsForForeignKeys => additionalForeignKeyEditor.foreach(_.handleColumns(columns.tableName, columns.columns)) 
    case _: RequestRemovalAllTabs => tableTabs.requestRemovalAllTabs()
    case additionalKeys: ResponseAdditionalForeignKeys =>  additionalForeignKeyEditor.foreach(_.handleForeignKeys(additionalKeys.keys))
    case _ => log.error(localization.errorDatabaseMessage(msg))
  }  

  def handleTableIdMessage(msg: TWithTableId) : Unit = msg match {
    case columns : ResponseColumns => tableTabs.addColumns(columns)
    case columns : ResponseColumnsFollow => tableTabs.addColumnsFollow(columns)
    case _ => log.error(localization.errorTableMessage(msg))
  }  

  def getId : DatabaseId = databaseId

  def currentTableId : Option[QueryId] =  tableTabs.currentTableId  
}
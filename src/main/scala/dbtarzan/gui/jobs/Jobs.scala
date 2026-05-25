package dbtarzan.gui.jobs;

import dbtarzan.gui.interfaces.TControlBuilder;
import dbtarzan.db.*
import dbtarzan.db.foreignkeys.ForeignKeyMapper
import dbtarzan.gui.BrowsingTable
import dbtarzan.gui.interfaces.TControlBuilder
import dbtarzan.gui.tabletabs.{TTableWithTab, TableStructureText, TableTabsMap, TabsToClose}
import dbtarzan.localization.Localization
import dbtarzan.log.actor.Logger
import dbtarzan.messages.*
import org.apache.pekko.actor.ActorRef
import scalafx.Includes.*
import scalafx.scene.Parent
import scalafx.scene.control.{Tab, TabPane, Tooltip}

public class Jobs(dbActor : ActorRef, guiActor : ActorRef, localization : Localization, log: Logger) extends TControlBuilder {
    private val tabs = new TabPane {
        side = Side.Left
    }
    private val jobs = new JobsMap()

    private var nextJobId: Int = 0 

    def currentJobId : Option[QueryId] = {
        val currentTab = tabs.selectionModel().selectedItem()
        jobs.jobIdForTab(currentTab)
    }

    def control : Parent = tabs

    def handleTableIdMessage(msg: TWithTableId): Unit = msg match {
        case columns: ResponseColumns => tableTabs.addColumns(columns)
        case columns: ResponseColumnsFollow => tableTabs.addColumnsFollow(columns)
        case columns: ResponseColumnsForForeignKeys => virtualForeignKeyEditor.foreach(_.handleColumns(columns.tableId, columns.columns))
        case _ => log.error(localization.errorTableMessage(msg))
    }


    private def buildJob(dbTable : DBTableStructure, browsingTable :  BrowsingTable) = new Tab() {
        text = TableStructureText.buildTabText(dbTable)
        content = browsingTable.control
        tooltip.value = Tooltip("")
    }

    
    private def requestRemovalThisTab(jobId : JobId) : Unit =
            tables.withQueryId(queryId, table => removeTabs(List(table.tab)))


    private def createTabWith(queryId: QueryId, structure : DBTableStructure, doWith : TTableWithTab[BrowsingTable] => Unit): Unit = {
        tables.withQueryIdForce(queryId, doWith, buildBrowsingTable(queryId, structure))
        closeOriginalTabIfAny(queryId)
    }

}


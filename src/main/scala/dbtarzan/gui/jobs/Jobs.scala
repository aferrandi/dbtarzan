package dbtarzan.gui.jobs;

import dbtarzan.gui.interfaces.TControlBuilder
import dbtarzan.db.*
import dbtarzan.db.foreignkeys.ForeignKeyMapper
import dbtarzan.gui.BrowsingTable
import dbtarzan.gui.database.TableTabs
import dbtarzan.gui.interfaces.TControlBuilder
import dbtarzan.gui.tabletabs.{TTableWithTab, TableStructureText, TableTabsMap, TabsToClose}
import dbtarzan.localization.Localization
import dbtarzan.log.actor.Logger
import dbtarzan.messages.{ JobId, QueryId, TWithTableId, TWithQueryId }
import jdk.javadoc.internal.doclets.formats.html.markup.HtmlStyles.tableTabs
import org.apache.pekko.actor.ActorRef
import scalafx.scene.control.{Tab, TabPane, Tooltip}
import scalafx.scene.Parent
import scalafx.geometry.Side

class Jobs(dbActor : ActorRef, guiActor : ActorRef, localization : Localization, log: Logger) extends TControlBuilder {
    private val tabs = new TabPane {
        side = Side.Left
    }
    private val jobs = new JobsMap()

    private var nextJobId: JobId = JobId(0)

    def currentJobId : Option[JobId] = {
        val currentTab = tabs.selectionModel().selectedItem()
        jobs.jobIdForTab(currentTab)
    }

    def currentTableId : Option[QueryId] = {
        val job = currentJobId.flatMap(jobId => jobs.jobWithJobId(jobId))
        job.flatMap(j => j.currentTableId)
    }


    def control : Parent = tabs

    def handleTableIdMessage(msg: TWithTableId): Unit =
        jobs.jobWithJobId(msg.tableId.jobId).foreach(job =>
            job.handleTableIdMessage(msg)
        )

    def handleQueryIdMessage(msg: TWithQueryId) : Unit =
        jobs.jobWithJobId(msg.queryId.tableId.jobId).foreach(job =>
          job.handleQueryIdMessage(msg)
        )

    private def buildJobTab(tableId: TableId, job: TableTabs) = new Tab() {
        text = s"Job ${job.jobId} from $tableId"
        content = job.control
        tooltip.value = Tooltip("")
    }
    
    def requestRemovalThisJob(jobId : JobId) : Unit = {
        val jobWithTab = jobs.jobWithTabForJobId(jobId)
        jobWithTab.
    }

    def createJobWith(tableId: TableId): JobId = {
        nextJobId = JobId.increment(nextJobId)
        val jobId = nextJobId
        val job = new TableTabs(jobId, dbActor, guiActor, localization, log)
        val tab = buildJobTab(tableId, job)
        tabs += tab
        tabs.selectionModel().select(tab)
        jobs.addJob(job, tab)
        jobId
    }
}


package dbtarzan.gui.jobs;

import dbtarzan.gui.interfaces.TControlBuilder
import dbtarzan.db.*
import dbtarzan.db.foreignkeys.ForeignKeyMapper
import dbtarzan.gui.BrowsingTable
import dbtarzan.gui.database.Job
import dbtarzan.gui.interfaces.TControlBuilder
import dbtarzan.gui.tabletabs.{TTableWithTab, TableStructureText, TableTabsMap, TabsToClose}
import dbtarzan.localization.Localization
import dbtarzan.log.actor.Logger
import dbtarzan.messages.{ QueryId, TWithTableId, TWithQueryId, TWithJobId, ResponseCloseTables }
import jdk.javadoc.internal.doclets.formats.html.markup.HtmlStyles.tableTabs
import org.apache.pekko.actor.ActorRef
import scalafx.scene.control.{Tab, TabPane, Tooltip, Label}
import scalafx.scene.Parent
import scalafx.geometry.Side
import dbtarzan.messages.RequestRemovalAllTabs
import scalafx.event.Event
import scalafx.Includes.*
import dbtarzan.log.actor.Logger

class Jobs(dbActor : ActorRef, guiActor : ActorRef, localization : Localization, log: Logger) extends TControlBuilder {
    private val jobsMap = new JobsMap()
    private var nextJobId: JobId = JobId(0)
    private val jobsTabs = new JobsTabs()

    def currentJobId : Option[JobId] =
        jobsMap.jobIdForTab(jobsTabs.currentTab())

    def currentTableId : Option[QueryId] = {
        val job = currentJobId.flatMap(jobId => jobsMap.jobWithJobId(jobId))
        job.flatMap(j => j.currentTableId)
    }

    def control : Parent = jobsTabs.control

    def handleTableIdMessage(msg: TWithTableId): Unit =
        jobsMap.jobWithJobId(msg.tableId.jobId).foreach(job =>
            job.handleTableIdMessage(msg)
        )

    def handleQueryIdMessage(msg: TWithQueryId) : Unit =
        jobsMap.jobWithJobId(msg.queryId.tableId.jobId).foreach(job =>
          job.handleQueryIdMessage(msg)
        )


    def handleJobIdMessage(msg: TWithJobId) : Unit = msg match {
        case tables: ResponseCloseTables => withJob(tables.jobId.jobId, job => removeTables(job, tables.ids))
        case tables: RequestRemovalAllTabs => withJob(tables.jobId.jobId, _.requestRemovalAllTabs())
        case _ => log.error(localization.errorJobMessage(msg))
    }

    private def removeTables(job: Job,  tableIds : List[QueryId]): Unit = {
        job.removeTables(tableIds)
        if(job.isEmpty)
            removeJob(job.jobId)
    }

    private def withJob(jobId: JobId, doWith: Job => Unit): Unit =
        jobsMap.jobWithJobId(jobId).foreach(job => doWith(job))


    private def buildJobTab(tableId: TableId, job: Job) = new Tab() {
        text = s"Job ${job.jobId}"
        content = job.control
        tooltip.value = Tooltip(f"Job from ${tableId.tableName}")
        onCloseRequest = (ev: Event) => {
            guiActor ! RequestRemovalAllTabs(JobInDatabaseId(job.jobId, tableId.databaseId))
        }
    }

    def createJobWith(tableId: TableId): JobId = {
        nextJobId = JobId.increment(nextJobId)
        val jobId = nextJobId
        val job = new Job(jobId, dbActor, guiActor, localization, log)
        val tab = buildJobTab(tableId, job)
        jobsTabs.addTab(tab)
        jobsMap.addJob(job, tab)
        jobId
    }

    private def removeJob(jobId: JobId): Unit = {
        println(s"Closing job $jobId")
        jobsMap.tabWithJobId(jobId).foreach(tab =>
            jobsTabs.removeTab(tab)
        )
        jobsMap.removeJob(jobId)
    }
}






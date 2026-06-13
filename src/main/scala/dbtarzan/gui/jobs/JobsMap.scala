package dbtarzan.gui.jobs

import dbtarzan.gui.database.TableTabs
import dbtarzan.messages.QueryId
import dbtarzan.db.JobId

import scalafx.scene.control.Tab
import scala.collection.mutable

case class JobWithTab(job : TableTabs, tab : Tab)

class JobsMap {
  private val mapTable = mutable.HashMap.empty[JobId, JobWithTab]

  def jobIdForTab(tab : Tab) : Option[JobId] =
    mapTable.values.find(_.tab == tab).map(_.job.jobId)

  def jobWithJobId(jobId: JobId): Option[TableTabs] =
    mapTable.get(jobId).map(_.job)

  def jobWithTabForJobId(jobId: JobId): Option[JobWithTab] =
    mapTable.get(jobId)

  def addJob(tableTabs: TableTabs, tab: scalafx.scene.control.Tab): Unit =
    mapTable += tableTabs.jobId -> JobWithTab(tableTabs, tab)
}

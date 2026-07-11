package dbtarzan.gui.jobs

import dbtarzan.gui.database.Job
import dbtarzan.messages.QueryId
import dbtarzan.db.JobId

import scalafx.scene.control.Tab
import scala.collection.mutable

case class JobWithTab(job : Job, tab : Tab)

class JobsMap {
  private val mapTable = mutable.HashMap.empty[JobId, JobWithTab]

  def jobIdForTab(tab : Tab) : Option[JobId] =
    mapTable.values.find(_.tab == tab).map(_.job.jobId)

  def jobWithJobId(jobId: JobId): Option[Job] =
    mapTable.get(jobId).map(_.job)

  def tabWithJobId(jobId: JobId): Option[Tab] =
    mapTable.get(jobId).map(_.tab)

  def jobWithTabForJobId(jobId: JobId): Option[JobWithTab] =
    mapTable.get(jobId)

  def addJob(job: Job, tab: scalafx.scene.control.Tab): Unit =
    mapTable += job.jobId -> JobWithTab(job, tab)

  def removeJob(jobId: JobId): Unit =
    mapTable.remove(jobId)
}

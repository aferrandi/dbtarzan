package dbtarzan.db.util

import scala.concurrent.duration.Duration

/* To check that the time of a query does not exceed a special threshold */
class ExecutionTime(threshold : Duration, queryPurpose: String) {
  private val maxEndTime = System.currentTimeMillis + threshold.toMillis

  def isOver: Boolean = System.currentTimeMillis  > maxEndTime

  def close(): Unit = {
    if (isOver)
      throw new Exception(s"timeout ${queryPurpose} (over  ${threshold.toSeconds} seconds)")
  }
}
package dbtarzan.db

opaque type JobId = Int

object JobId {
  def apply(value: Int): JobId = value
}

class jobIdGenerator() {
  private var nextJobIdValue: Int = 0

  def nextJobId(): JobId = {
    val jobId = JobId(nextJobIdValue)
    nextJobIdValue += 1
    jobId
  }
}

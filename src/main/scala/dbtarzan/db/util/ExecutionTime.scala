package dbtarzan.db.util

/* To check that the time of a query does not exceed a special threshold */
class ExecutionTime(thresholdInMilliseconds : Long) {
	val startTime = System.currentTimeMillis

    def isOver() : Boolean = System.currentTimeMillis - startTime > thresholdInMilliseconds
}
package dbtarzan.db.util

import org.scalatest.flatspec.AnyFlatSpec

import scala.concurrent.duration._
import scala.language.postfixOps

class ExecutionTimeTest extends AnyFlatSpec {
  "execution time" should "be over the given duration" in {
    val executionTime = new ExecutionTime(100 milliseconds)
    assert(executionTime.isOver === false)
    Thread.sleep(101)
    assert(executionTime.isOver === true)
  }
}

package dbtarzan.gui.table

import dbtarzan.db.Row
import org.scalatest.flatspec.AnyFlatSpec

class CheckedRowsBufferTest extends AnyFlatSpec {
  "a new buffer" should "be empty" in {
    val buffer = new CheckedRowsBuffer()
    assert(buffer.rows.length === 0)
  }

  "a buffer with a row being added" should "contain that row" in {
    val buffer = new CheckedRowsBuffer()
    buffer.add(Row(List("a", "b")))
    assert(buffer.rows.length === 1)
    assert(buffer.rows.head.values === List("a", "b"))
  }

  "a buffer with a row being added and then removed" should "be empty" in {
    val buffer = new CheckedRowsBuffer()
    val row = Row(List("a", "b"))
    buffer.add(row)
    buffer.remove(row)
    assert(buffer.rows.length === 0)
  }

  "a buffer with a row being removed but never added" should "be empty" in {
    val buffer = new CheckedRowsBuffer()
    val row = Row(List("a", "b"))
    buffer.remove(row)
    assert(buffer.rows.length === 0)
  }
}

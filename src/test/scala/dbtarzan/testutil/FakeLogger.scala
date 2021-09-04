package dbtarzan.testutil

import dbtarzan.messages.TLogger

class FakeLogger extends TLogger {
  override def debug(text: String): Unit = {}

  override def info(text: String): Unit = {}

  override def warning(text: String): Unit = {}

  override def error(text: String, ex: Exception): Unit = {}

  override def error(text: String): Unit = {}
}

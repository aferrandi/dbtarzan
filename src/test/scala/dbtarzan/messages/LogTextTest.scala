
package dbtarzan.messages

import java.time.LocalDateTime
import org.scalatest.flatspec.AnyFlatSpec


class LogTextTest extends AnyFlatSpec {
  "An error message with exception" should "be parseable" in {
    val error = Error(LocalDateTime.of(2020,1, 1, 10, 30), "error", Some(new Exception("ex")))
    val text = LogText.extractLogMessage(error)
  	assert("error:ex" === text)
  }

  "An error message without exception" should "be parseable" in {
    val error = Error(LocalDateTime.of(2020,1, 1, 10, 30), "error", None)
    val text = LogText.extractLogMessage(error)
  	assert("error" === text)
  }

  "A warning message" should "be parseable" in {
    val warning = Warning(LocalDateTime.of(2020,1, 1, 10, 30), "warning")
    val text = LogText.extractLogMessage(warning)
    assert("warning" === text)
  }

  "An info message" should "be parseable" in {
    val info = Info(LocalDateTime.of(2020,1, 1, 10, 30), "info")
    val text = LogText.extractLogMessage(info)
    assert("info" === text)
  }


  "An error message" should "have prefix E" in {
    val error = Error(LocalDateTime.of(2020,1, 1, 10, 30), "error", Some(new Exception("ex")))
    val prefix = LogText.extractLogPrefix(error)
    assert("E" === prefix)
  }

  "A warning message" should "have prefix W" in {
    val warning = Warning(LocalDateTime.of(2020,1, 1, 10, 30), "warning")
    val prefix = LogText.extractLogPrefix(warning)
    assert("W" === prefix)
  }


  "An info message" should "have prefix I" in {
    val info = Info(LocalDateTime.of(2020,1, 1, 10, 30), "info")
    val prefix = LogText.extractLogPrefix(info)
    assert("I" === prefix)
  }
}
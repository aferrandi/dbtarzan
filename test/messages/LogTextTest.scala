
package dbtarzan.messages

import org.scalatest.FlatSpec
import java.time.LocalDateTime


class LogTextTest extends FlatSpec {
  "An error message with exception" should "be parseable" in {
    val error = Error(LocalDateTime.of(2020,1, 1, 10, 30), "error", Some(new Exception("ex")))
    var text = LogText.extractLogMessage(error)
  	assert("error:ex" === text)
  }

  "An error message without exception" should "be parseable" in {
    val error = Error(LocalDateTime.of(2020,1, 1, 10, 30), "error", None)
    var text = LogText.extractLogMessage(error)
  	assert("error" === text)
  }

  "A warning message" should "be parseable" in {
    val warning = Warning(LocalDateTime.of(2020,1, 1, 10, 30), "warning")
    var text = LogText.extractLogMessage(warning)
    assert("warning" === text)
  }

  "An info message" should "be parseable" in {
    val info = Info(LocalDateTime.of(2020,1, 1, 10, 30), "info")
    var text = LogText.extractLogMessage(info)
    assert("info" === text)
  }


  "An error message" should "have prefix E" in {
    val error = Error(LocalDateTime.of(2020,1, 1, 10, 30), "error", Some(new Exception("ex")))
    var prefix = LogText.extractLogPrefix(error)
    assert("E" === prefix)
  }

  "A warning message" should "have prefix W" in {
    val warning = Warning(LocalDateTime.of(2020,1, 1, 10, 30), "warning")
    var prefix = LogText.extractLogPrefix(warning)
    assert("W" === prefix)
  }


  "An info message" should "have prefix I" in {
    val info = Info(LocalDateTime.of(2020,1, 1, 10, 30), "info")
    var prefix = LogText.extractLogPrefix(info)
    assert("I" === prefix)
  }
}
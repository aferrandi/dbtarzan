package dbtarzan.messages

import org.scalatest.flatspec.AnyFlatSpec
class ExceptionTextTest extends AnyFlatSpec {
  "An error message from en exception" should "contain the exception text" in {
    val text = ExceptionText.extractWholeExceptionText(Exception("message1", Exception("message2")))
    assert(text.contains("message1 at:"))
    assert(text.contains("ExceptionTextTest"))
    assert(text.contains("message2 at:"))
  }
}
package dbtarzan.types

import dbtarzan.types.Binaries.Binary
import org.scalatest.flatspec.AnyFlatSpec


class BinariesTest extends AnyFlatSpec {
  "A binary with Ascii values" should "be the same as the word described by the values" in {
    assert(Binary(Array[Byte](72, 97, 108, 108, 111)).asString === "Hallo")
  }

  "A binary converted to bytes" should "return the same values that initialized them" in {
    assert(Binary(Array[Byte](72, 97, 108, 108, 111)).asBytes === Array[Byte](72, 97, 108, 108, 111))
  }

}
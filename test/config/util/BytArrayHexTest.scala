package dbtarzan.config.util

import org.scalatest.FlatSpec
import spray.json._


class BytArrayHexTest extends FlatSpec {
  "converting to hex and back" should "give the original value" in {
  	val original = "F263575E7B00A977A8E9A37E08B9C215FEB9BFB2F992B2B8F11E"
  	val bytes = BytArrayHex.fromHex(original)
  	println(bytes)
  	val hex = BytArrayHex.toHex(bytes)
  	assert(hex === original)
  }
}
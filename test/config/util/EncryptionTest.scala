package dbtarzan.config.util

import org.scalatest.FlatSpec
import spray.json._


class EncyrptionTest extends FlatSpec {
  "decrypting the encripted text" should "give the original value" in {
    val encryption = new Encryption("abcdabcdabcdabcd", "1234567890123456")
    val original = "zorbaIlGreco";
    val encrypted = encryption.encrypt(original)
    val decrypted = encryption.decrypt(encrypted)
  	assert(decrypted === original)
  }
}
package dbtarzan.config

import org.scalatest.FlatSpec

class PasswordEncryptionTest extends FlatSpec {
  "decrypting the encrypted password" should "give the original value" in {
  	val passwordPlain = "amp1V30NtnMEyaIRciBh"
    val passwordDecrypted = PasswordEncryption.decrypt(PasswordEncryption.encrypt(passwordPlain ))
  	assert(passwordDecrypted === passwordPlain)
  }
}
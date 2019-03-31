package dbtarzan.config

import org.scalatest.FlatSpec

class PasswordEncryptionTest extends FlatSpec {
  "decrypting the encrypted password" should "give the original value" in {
  	val passwordPlain = Password("amp1V30NtnMEyaIRciBh")
    val passwordEncryption = new PasswordEncryption(PasswordEncryption.defaultEncryptionKey)
    val passwordDecrypted = passwordEncryption.decrypt(passwordEncryption.encrypt(passwordPlain ))
  	assert(passwordDecrypted === passwordPlain)
  }
}
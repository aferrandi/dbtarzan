package dbtarzan.config.password

import org.scalatest.FlatSpec

class EncryptionVerificationTest extends FlatSpec {
  "the encryption key" should "verify against its own verification key" in {
    val encryptionKey = EncryptionKey("amp1V30NtnMEyaIR")  
    val verificationKey = EncryptionVerification.toVerification(encryptionKey)
  	assert(EncryptionVerification.verify(encryptionKey, verificationKey) == true)
  }

  "the encryption key" should "not verify against the wrong verification key" in {
    val encryptionKey = EncryptionKey("amp1V30NtnMEyaIR")  
    val verificationKey = EncryptionVerification.toVerification(encryptionKey)
    val wrongVerificationKey = VerificationKey(Password(verificationKey.password.key.reverse)) 
  	assert(EncryptionVerification.verify(encryptionKey, wrongVerificationKey) == false)
  }

  "the encryption key" should "not verify against a verification key of wrong length" in {
    val encryptionKey = EncryptionKey("amp1V30NtnMEyaIR")  
    val wrongVerificationKey = VerificationKey(Password("aaaaaa")) 
  	assert(EncryptionVerification.verify(encryptionKey, wrongVerificationKey) == false)
  }

  "a wrong size encryption key" should "throw an exception when its verification key gets built" in {
    val encryptionKey = EncryptionKey("amp1V30")  
    intercept[java.security.InvalidKeyException] {
       EncryptionVerification.toVerification(encryptionKey)
    }
  }
}
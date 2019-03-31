package dbtarzan.config

case class Password(key : String)

case class EncryptionKey(key : String)

case class VerificationKey(password: Password)

object EncryptionVerification {
    val alwaysTheSame = Password("7ODu6l6eU5NgiZp7")
    def toVerification(encryptionKey : EncryptionKey) : VerificationKey = {
        val res = new PasswordEncryption(encryptionKey).encrypt(alwaysTheSame)
        VerificationKey(res)
    }

    def verify(encryptionKey : EncryptionKey, verificationKey : VerificationKey) : Boolean = {
        val res = new PasswordEncryption(encryptionKey).decrypt(verificationKey.password)
        alwaysTheSame == res
    }
}
package dbtarzan.config.password

case class Password(key : String)

case class EncryptionKey(key : String)

case class VerificationKey(password: Password)

/* the verification key is used to check that the encryption key is correct, before trying to open a database with a password decrypted with that key.
   The veriication key is stored with the global configuration
 */
object EncryptionVerification {
    /* if the encryption key encrypted with the verification key matches this invariant, then it is correct */
    val alwaysTheSame: Password = Password("7ODu6l6eU5NgiZp7")
    /* AES allows only encyrption keys of that size */
    val possibleEncryptionKeyLength: List[Int] = List(16, 24, 32)

    def toVerification(encryptionKey : EncryptionKey) : VerificationKey = {
        val res = new PasswordEncryption(encryptionKey).encrypt(alwaysTheSame)
        VerificationKey(res)
    }

    def verify(encryptionKey : EncryptionKey, verificationKey : VerificationKey) : Boolean = {
        try
            val res = new PasswordEncryption(encryptionKey).decrypt(verificationKey.password)
            alwaysTheSame == res
        catch
            case _ : Exception => false
    }

    /*For AES, NIST selected three members of the Rijndael family, each with a block size of 128 bits, but three different key lengths: 128, 192 and 256 bits. */
    def isEncryptionKeyOfValidSize(encryptionKey : EncryptionKey) : Boolean =
        possibleEncryptionKeyLength.contains(encryptionKey.key.length) 
}
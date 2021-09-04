package dbtarzan.config.password

import dbtarzan.config.util.{ Encryption, ByteArrayHex }

/* to encrypt and decrypt the database passwords: "1gCDuAntiQiFPHIT" */
class PasswordEncryption(key : EncryptionKey) {
	val encryption = new Encryption(key.key, "eJSUpCT9VNo5AbF6")

	def encrypt(plainPassword : Password) : Password =  Password(ByteArrayHex.toHex(encryption.encrypt(plainPassword.key)))

	def decrypt(cipherPassword : Password) : Password = Password(encryption.decrypt(ByteArrayHex.fromHex(cipherPassword.key)))
}

object PasswordEncryption {
	val defaultEncryptionKey: EncryptionKey = EncryptionKey("1gCDuAntiQiFPHIT")
}

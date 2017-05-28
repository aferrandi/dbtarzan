package dbtarzan.config

import dbtarzan.config.util.{ Encryption, ByteArrayHex }

object PasswordEncryption
{
	val encryption = new Encryption("1gCDuAntiQiFPHIT", "eJSUpCT9VNo5AbF6")

	def encrypt(plainPassword : String) : String =  ByteArrayHex.toHex(encryption.encrypt(plainPassword))

	def decrypt(cipherPassword : String) : String = encryption.decrypt(ByteArrayHex.fromHex(cipherPassword))

}

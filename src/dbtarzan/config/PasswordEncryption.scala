package dbtarzan.config

import dbtarzan.config.util.{ Encryption, ByteArrayHex }

object PasswordEncryption
{
	val encryption = new Encryption("1gCDuAntiQiFPHIiTEEE", "AjbGyXxV")

	def encrypt(plainPassword : String) : String =  ByteArrayHex.toHex(encryption.encrypt(plainPassword))

	def decrypt(cipherPassword : String) : String = encryption.decrypt(ByteArrayHex.fromHex(cipherPassword))

}
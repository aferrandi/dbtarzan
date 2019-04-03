package dbtarzan.config.util

import javax.crypto.{Cipher}
import javax.crypto.spec.{SecretKeySpec, IvParameterSpec}


/* to encrypt and decrypt the passwords included in the database JDBC definitions.
	the key can't be over 16 characters, otherwise you get an error in windows
 */
class Encryption(key : String, initVector : String) {
	val method = "AES/CBC/PKCS5PADDING"
	val utf8 = "UTF-8"
   	val iv = new IvParameterSpec(initVector.getBytes(utf8));
	println("key:"+key)
    val keySpec =new SecretKeySpec(key.getBytes(utf8), "AES");

	/* returns the JDBC configuration for a database */
	def encrypt(plainText : String) : Array[Byte] =  {
		val cipher = Cipher.getInstance(method)
		cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);
		val encryptedText = cipher.doFinal(plainText.getBytes(utf8))
		encryptedText
	}

	def decrypt(encryptedText : Array[Byte]) : String = {
		val cipher = Cipher.getInstance(method);
		cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);
		val plainText = new String(cipher.doFinal(encryptedText), utf8);
		plainText
	}
}
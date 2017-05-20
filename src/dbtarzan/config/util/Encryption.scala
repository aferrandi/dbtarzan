package dbtarzan.config.util

import javax.crypto.{Cipher, SecretKeyFactory}
import javax.crypto.spec.{SecretKeySpec, PBEKeySpec, IvParameterSpec, PBEParameterSpec}


/* to encrypt and decrypt the passwords included in the database JDBC definitions */
class Encryption(key : String, salt : String) {
	val pbeKeySpec = new PBEKeySpec(key.toCharArray());
	val secretKeyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndTripleDES");
	val secretKey = secretKeyFactory.generateSecret(pbeKeySpec);


	/* returns the JDBC configuration for a database */
	def encrypt(plainText : String) : Array[Byte] =  {
		val pbeParameterSpec = new PBEParameterSpec(salt.getBytes("UTF-8"), 100);
		val cipher = Cipher.getInstance("PBEWithMD5AndTripleDES");
		cipher.init(Cipher.ENCRYPT_MODE, secretKey, pbeParameterSpec);
		val cipherText = cipher.doFinal(plainText.getBytes("UTF-8"))
		cipherText
	}

	def decrypt(cipherText : Array[Byte]) : String = {
		val pbeParameterSpec = new PBEParameterSpec(salt.getBytes("UTF-8"), 100);
		val cipher = Cipher.getInstance("PBEWithMD5AndTripleDES");
		cipher.init(Cipher.DECRYPT_MODE, secretKey, pbeParameterSpec);
		val plainText = new String(cipher.doFinal(cipherText), "UTF-8");
		plainText
	}
}
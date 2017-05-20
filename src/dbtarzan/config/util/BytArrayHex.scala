package dbtarzan.config.util

import java.math.BigInteger

object BytArrayHex {
	def toHex(bytes : Array[Byte]) : String =
		bytes.map("%02X" format _).mkString

	def fromHex(hex : String) : Array[Byte] = 
		hex.sliding(2, 2).toArray.map(Integer.parseInt(_, 16).toByte)
}
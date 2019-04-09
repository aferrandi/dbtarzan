package dbtarzan.db

import java.sql.{ DriverManager, Connection}
import dbtarzan.config.connections.ConnectionData
import dbtarzan.config.{ PasswordEncryption, EncryptionKey, Password }

class DriverManagerWithEncryption(key : EncryptionKey) extends ConnectionProvider {
	private val passwordEncryption = new PasswordEncryption(key)
	def getConnection(data : ConnectionData) : Connection = 
		if(data.passwordEncrypted.getOrElse(false))
			DriverManager.getConnection(data.url, data.user, passwordEncryption.decrypt(data.password).key)
		else	
			DriverManager.getConnection(data.url, data.user, data.password.key)
}
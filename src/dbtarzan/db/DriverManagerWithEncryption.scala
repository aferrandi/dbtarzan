package dbtarzan.db

import java.sql.{ DriverManager, Connection}
import dbtarzan.config.{ConnectionData, PasswordEncryption}

object DriverManagerWithEncryption extends ConnectionProvider
{
	def getConnection(data : ConnectionData) : Connection = 
		if(data.passwordEncrypted.getOrElse(false))
			DriverManager.getConnection(data.url, data.user, PasswordEncryption.decrypt(data.password))
		else	
			DriverManager.getConnection(data.url, data.user, data.password)
}
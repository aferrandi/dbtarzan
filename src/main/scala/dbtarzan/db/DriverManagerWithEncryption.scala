package dbtarzan.db

import java.sql.{Connection, DriverManager}
import dbtarzan.config.connections.ConnectionData
import dbtarzan.config.password.{EncryptionKey, Password, PasswordEncryption}

class DriverManagerWithEncryption(key : EncryptionKey) extends ConnectionProvider {
  private val passwordEncryption = new PasswordEncryption(key)
  def getConnection(data : ConnectionData, loginPassword: Option[Password]) : Connection =
    loginPassword match {
      case Some(p) => getConnectionFromPassword(data, p)
      case None => withoutLoginPassword(data)
    }

  private def getConnectionFromPassword(data: ConnectionData, p: Password): Connection =
    DriverManager.getConnection(data.url, data.user, p.key)

  private def withoutLoginPassword(data: ConnectionData): Connection = data.password match
    case Some(storedPassword) =>  withValidStoredPassword(data, storedPassword)
    case None => throw new Exception(s"No password found for database ${data.name}")

  private def withValidStoredPassword(data: ConnectionData, storedPassword: Password) = {
      getConnectionFromPassword(data, passwordEncryption.decrypt(storedPassword))
  }
}
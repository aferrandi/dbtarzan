package dbtarzan.config.connections

import dbtarzan.config.password.{ EncryptionKey, PasswordEncryption, Password }
import java.nio.file.Path

case class EncryptionKeyChange(
  originalEncryptionKey : Option[EncryptionKey],
  newEncryptionKey : Option[EncryptionKey]
)

class ConnectionDataPasswordChanger(change : EncryptionKeyChange) {
    private val originalEncryptionKey = change.originalEncryptionKey.getOrElse(PasswordEncryption.defaultEncryptionKey)
    private val decrypter = new PasswordEncryption(originalEncryptionKey)
    private val newEncryptionKey = change.newEncryptionKey.getOrElse(PasswordEncryption.defaultEncryptionKey)
    private val encrypter = new PasswordEncryption(newEncryptionKey)

    def updateDatas(configPath : Path) : Unit = {
        val connectionDatas = ConnectionDataReader.read(configPath)
        val connectionDatasUpdated = connectionDatas.map(updateData)
        ConnectionDataWriter.write(configPath, connectionDatasUpdated)
    }

    private def updateData(original : ConnectionData) = 
        ConnectionData(
            original.jar, 
            original.name, 
            original.driver, 
            original.url,
            original.schema,
            original.user,
            original.password.map(password => reencryptPassword(password)),
            original.instances,
            original.identifierDelimiters,
            original.maxRows,
            original.queryTimeoutInSeconds,
            original.maxFieldSize,
            original.maxInClauseCount,
            original.catalog
            )

    private def reencryptPassword(password : Password) : Password = {
        val toEncrypt = decrypter.decrypt(password)
        encrypter.encrypt(toEncrypt)
    }

}
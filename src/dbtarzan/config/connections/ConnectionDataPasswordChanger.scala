package dbtarzan.config.connections

import dbtarzan.config.{ EncryptionKey, PasswordEncryption, Password }
import java.nio.file.Path

case class EncryptionKeyChange(
  originalEncryptionKey : Option[EncryptionKey],
  newEncryptionKey : Option[EncryptionKey]
)

class ConnectionDataPasswordChanger(change : EncryptionKeyChange) {
    private val originalEncryptionKey = change.originalEncryptionKey.getOrElse(PasswordEncryption.defaultEncryptionKey)
    private val decrypter = new PasswordEncryption(originalEncryptionKey)
    private val newEncryptionKey = change.newEncryptionKey.getOrElse(PasswordEncryption.defaultEncryptionKey);
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
            reencryptPassword(original.password, original.passwordEncrypted.getOrElse(false)),
            Some(true),
            original.instances,
            original.identifierDelimiters,
            original.maxRows,
            original.catalog
            )

    private def reencryptPassword(password : Password, passwordEncrypted : Boolean) : Password = {
        val toEncrypt = if(passwordEncrypted)
                            decrypter.decrypt(password)
                        else 
                            password
        encrypter.encrypt(toEncrypt)
    }

}
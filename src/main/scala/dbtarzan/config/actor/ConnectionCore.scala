package dbtarzan.config.actor

import dbtarzan.config.connections.ConnectionData
import dbtarzan.config.password.{EncryptionKey, Password}
import dbtarzan.db.{DriverManagerWithEncryption, DriverSpec, RegisterDriver, SchemaNames}
import dbtarzan.db.basicmetadata.MetadataSchemasLoader
import dbtarzan.localization.Localization
import dbtarzan.log.actor.Logger
import dbtarzan.messages.{ResponseSchemaExtraction, ResponseTestConnection}

class ConnectionCore(registerDriver : RegisterDriver, log: Logger, localization : Localization) {
  def extractSchemas(data: ConnectionData, encryptionKey: EncryptionKey, loginPassword: Option[Password]): ResponseSchemaExtraction = {
    try {
      registerDriver.registerDriverIfNeeded(DriverSpec(data.jar, data.driver))
      try {
        val connection = new DriverManagerWithEncryption(encryptionKey).getConnection(data, loginPassword)
        val schemas = new MetadataSchemasLoader(connection.getMetaData, log).schemasNames()
        connection.close()
        ResponseSchemaExtraction(data, Some(SchemaNames(schemas)), None)
      } catch {
        case e: Throwable =>
          ResponseSchemaExtraction(data, None, Some(new Exception(localization.errorConnectingToDatabase(data.name), e)))
      }
    } catch {
      case e: Throwable =>
        ResponseSchemaExtraction(data, None, Some(new Exception(localization.errorRegisteringDriver(data.name), e)))
    }
  }

  def testConnection(data: ConnectionData, encryptionKey: EncryptionKey, loginPassword: Option[Password]): ResponseTestConnection = {
    try {
      registerDriver.registerDriverIfNeeded(DriverSpec(data.jar, data.driver))
      try {
        val connection = new DriverManagerWithEncryption(encryptionKey).getConnection(data, loginPassword)
        connection.close()
        ResponseTestConnection(data, None)
      } catch {
        case e: Throwable =>
          ResponseTestConnection(data, Some(new Exception(localization.errorConnectingToDatabase(data.name), e)))
      }
    } catch {
      case e: Throwable =>
        ResponseTestConnection(data, Some(new Exception(localization.errorRegisteringDriver(data.name), e)))
    }
  }
}

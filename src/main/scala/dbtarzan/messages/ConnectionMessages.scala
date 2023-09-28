package dbtarzan.messages

import dbtarzan.config.connections.ConnectionData
import dbtarzan.config.password.{EncryptionKey, Password}
import dbtarzan.db.{DatabaseId, LoginPasswords}


case class QueryClose(databaseId : DatabaseId) 

case class QueryDatabase(databaseId : DatabaseId, encryptionKey : EncryptionKey, loginPasswords: LoginPasswords)

case class CopyToFile(databaseId : DatabaseId, encryptionKey : EncryptionKey, loginPasswords: LoginPasswords)

case class ConnectionDatas(datas : List[ConnectionData])

case class TestConnection(data : ConnectionData, encryptionKey : EncryptionKey, loginPassword: Option[Password])

case class ExtractSchemas(data : ConnectionData, encryptionKey : EncryptionKey, loginPassword: Option[Password])


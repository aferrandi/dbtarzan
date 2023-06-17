package dbtarzan.messages

import dbtarzan.config.connections.ConnectionData
import dbtarzan.config.password.EncryptionKey
import dbtarzan.db.DatabaseId


case class QueryClose(databaseId : DatabaseId) 

case class QueryDatabase(databaseId : DatabaseId, encryptionKey : EncryptionKey)

case class CopyToFile(databaseId : DatabaseId, encryptionKey : EncryptionKey)

case class ConnectionDatas(datas : List[ConnectionData])

case class TestConnection(data : ConnectionData, encryptionKey : EncryptionKey)

case class ExtractSchemas(data : ConnectionData, encryptionKey : EncryptionKey)


package dbtarzan.messages

import dbtarzan.db.DatabaseId
import dbtarzan.config.connections.ConnectionData
import dbtarzan.config.password.EncryptionKey


case class QueryClose(databaseId : DatabaseId) 

case class QueryDatabase(databaseId : DatabaseId, encryptionKey : EncryptionKey)

case class CopyToFile(databaseId : DatabaseId, encryptionKey : EncryptionKey)

case class ConnectionDatas(datas : List[ConnectionData])

case class TestConnection(data : ConnectionData)


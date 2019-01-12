package dbtarzan.messages

import dbtarzan.db.DatabaseId
import dbtarzan.config.connections.ConnectionData


case class QueryClose(databaseId : DatabaseId) 

case class QueryDatabase(databaseId : DatabaseId)

case class CopyToFile(databaseId : DatabaseId)

case class ConnectionDatas(datas : List[ConnectionData])

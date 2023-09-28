package dbtarzan.config.connections

import dbtarzan.db.SimpleDatabaseId


/* the database configuration file content as a map databaseName => database JDBC configuration */
class ConnectionsDataMap(val connectionDatas : List[ConnectionData]) {
	private val connectionDatasByName: Map[String, List[ConnectionData]] = connectionDatas.groupBy(data => data.name)

	/* returns the JDBC configuration for a database */
	def connectionDataFor(simpleDatabaseId: SimpleDatabaseId) : ConnectionData = {
    val name = simpleDatabaseId.databaseName
    connectionDatasByName.get(name).map(datasPerName =>
      if (datasPerName.size == 1)
        datasPerName.head
      else
        throw new Exception("Multiple connections with the name " + name)
    ).getOrElse(throw new Exception("No connection with the name " + name))
  }
}
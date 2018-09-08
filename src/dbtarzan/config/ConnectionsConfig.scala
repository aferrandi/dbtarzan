package dbtarzan.config


/* the database configuration file content as a map databaseName => database JDBC configuration */
class ConnectionsConfig(connectionDatas : List[ConnectionData]) {
	val connectionDatasByName = connectionDatas.groupBy(data => data.name)

	/* returns the JDBC configuration for a database */
	def connect(name : String) : ConnectionData = 
		connectionDatasByName.get(name).map(datasPerName => 
			if(datasPerName.size == 1) 
				datasPerName.head
			else
				throw new Exception("Multiple connections with the name "+name)
		).getOrElse( throw new Exception("No connection with the name "+name))

	/* all the database names */
	def connections() : List[String] = connectionDatasByName.keys.toList
}
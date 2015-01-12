package dbtarzan.config

import scala.util.{Try, Success, Failure}

class Config(connectionDatas : List[ConnectionData]) {
	val connectionDatasByName = connectionDatas.groupBy(data => data.name)

	def connect(name : String) : ConnectionData = 
		connectionDatasByName.get(name).map(datasPerName => 
			if(datasPerName.size == 1) 
				datasPerName.head
			else
				throw new Exception("Multiple connections with the name "+name)
		).getOrElse( throw new Exception("No connection with the name "+name))

	def connections() = connectionDatasByName.keys.toList
}
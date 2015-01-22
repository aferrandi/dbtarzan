package dbtarzan.db.actor

import java.sql.{Connection, ResultSet, DriverManager}
import scala.collection.mutable.ListBuffer
import akka.actor.Actor
import dbtarzan.config.ConnectionData
import dbtarzan.db.util.ResourceManagement.using
import akka.actor.ActorRef
import dbtarzan.db._
import dbtarzan.messages._

/**
	The actor that reads data from the database
*/
class DatabaseWorker(data : ConnectionData, guiActor : ActorRef) extends Actor {
	val connection = DriverManager.getConnection(data.url, data.user, data.password)
	def databaseName = data.name
	val foreignKeyLoader =  new ForeignKeyLoader(connection, data.schema)
	val queryLoader = new QueryLoader(connection)
	def columnNames(tableName : String, useResult : Fields => Unit) : Unit = {
		var meta = connection.getMetaData()
		using(meta.getColumns(null, data.schema.orNull, tableName, "%")) { rs =>
			val list = new ListBuffer[Field]()			
			while(rs.next) {
				var fieldName = rs.getString("COLUMN_NAME")
				toType(rs.getInt("DATA_TYPE")).map(fieldType => list += Field(fieldName, fieldType))
			}
			println("Columns loaded")
			useResult(Fields(list.toList))
		}
	}

	def tableNames(useResult : TableNames => Unit) : Unit = {
		var meta = connection.getMetaData()
		using(meta.getTables(null, data.schema.orNull, "%", Array("TABLE"))) { rs =>
			val list = new ListBuffer[String]()
			while(rs.next) {
				list += rs.getString("TABLE_NAME")			
			}
			useResult(TableNames(list.toList))
		}
	}


	private def toType(sqlType : Int) : Option[FieldType] = 
		sqlType match {
			case java.sql.Types.CHAR => Some(FieldType.STRING)
			case java.sql.Types.INTEGER => Some(FieldType.INT)
			case java.sql.Types.FLOAT | java.sql.Types.DOUBLE => Some(FieldType.FLOAT)	
			case _ => Some(FieldType.STRING)
		}



	private def handleErr[R](r: => R): Unit = 
	    try { r } catch {
	      case e : Exception => guiActor ! Error(e)
	    }
	  
	override def postStop(): Unit = {
		println("connection stop")
		connection.close()
	}

  def receive = {
	    case qry : QueryRows => handleErr(
	    		queryLoader.query(qry, rows => guiActor ! ResponseRows(qry.id, rows))
	    	)
	    case qry: QueryTables => handleErr(
	    		tableNames(names => guiActor ! ResponseTables(qry.id, names))
			)
	    case qry : QueryColumns => handleErr( 
	    		columnNames(qry.tableName, columns => guiActor ! ResponseColumns(qry.id, qry.tableName, columns))
	    	)
	    case qry : QueryColumnsFollow => handleErr(
	    		columnNames(qry.tableName, columns => guiActor ! ResponseColumnsFollow(qry.id, qry.tableName, qry.follow, columns))
	    	)		
	    case qry: QueryForeignKeys => handleErr(
	    		foreignKeyLoader.foreignKeys(qry.id.tableName, keys => guiActor ! ResponseForeignKeys(qry.id, keys))
	    	)    	
    
  }
}
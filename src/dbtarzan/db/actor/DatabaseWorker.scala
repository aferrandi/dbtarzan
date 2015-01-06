package dbtarzan.db.actor

import java.sql.{Connection, ResultSet, DriverManager}
import scala.collection.mutable.ListBuffer
import scala.collection.immutable.Vector
import akka.actor.Actor
import dbtarzan.config.ConnectionData
import dbtarzan.db.util.ResourceManagement.using
import akka.actor.ActorRef
import dbtarzan.db._
import dbtarzan.messages._

class DatabaseWorker(data : ConnectionData, guiActor : ActorRef) extends Actor {
	val connection = DriverManager.getConnection(data.url, data.user, data.password)
	def databaseName = data.name
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

	def foreignKeys(qry : QueryForeignKeys) : ForeignKeys = 
		new ForeignKeyLoader(connection).foreignKeys(qry.id.tableName, data.schema)


	private def toType(sqlType : Int) : Option[FieldType] = 
		sqlType match {
			case java.sql.Types.CHAR => Some(FieldType.STRING)
			case java.sql.Types.INTEGER => Some(FieldType.INT)
			case java.sql.Types.FLOAT | java.sql.Types.DOUBLE => Some(FieldType.FLOAT)	
			case _ => Some(FieldType.STRING)
		}


	def query(qry : QueryRows, use : Rows => Unit) : Unit = {
		println("SQL:"+qry.sql)
  		using(connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) { statement => 
	  		val rs = statement.executeQuery(qry.sql)
	  		val meta = rs.getMetaData()
	  		val columnCount = meta.getColumnCount()
	  		println("Column count:"+columnCount)
	  		println("Rows to read :"+qry.maxRows)
	  		var rows = Vector.empty[Row]
	  		var i = 0
	  		while(rs.next() && i < qry.maxRows) {
	  			rows = rows :+ nextRow(rs, columnCount)
	  			if(rows.length >= 20) {
	  				use(Rows(rows.toList))
	  				rows = Vector.empty[Row]
	  			}
	  			i += 1
	  		}
	  		if(!rows.isEmpty) 
	  			use(Rows(rows.toList))
	  		println("Query terminated")
		}
	}

	private def handleErr[R](r: => R): Unit = 
	    try { r } catch {
	      case e : Exception => guiActor ! Error(e)
	    }
	  

	private def nextRow(rs : ResultSet, columnCount : Int) : Row = 
		Row(Range(1, columnCount+1).map(i => rs.getString(i)).toList)

  def receive = {
	    case qry : QueryRows => handleErr(
	    		query(qry, rows => guiActor ! ResponseRows(qry.id, rows))
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
	    	guiActor ! ResponseForeignKeys(qry.id, foreignKeys(qry))
	    	)    	
    
  }
}
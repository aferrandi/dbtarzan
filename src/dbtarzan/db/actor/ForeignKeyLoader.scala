package dbtarzan.db.actor

import java.sql.{Connection, ResultSet}
import scala.collection.mutable.ListBuffer
import dbtarzan.db.{ ForeignKey, ForeignKeys, FieldsOnTable }
import dbtarzan.db.util.ResourceManagement.using

class ForeignKeyLoader(connection : java.sql.Connection, schema: Option[String]) {
	case class ForeignKeyKey(name: String, fromTable : String, toTable : String)
	case class ForeignKeyPart(key : ForeignKeyKey, fromField : String, toField : String)

	private def rsToForeignPart(rs : ResultSet) : ForeignKeyPart = 
		ForeignKeyPart(
				ForeignKeyKey(
					rs.getString("FK_NAME"), 
					rs.getString("FKTABLE_NAME"), 
					rs.getString("PKTABLE_NAME")
				), 
				rs.getString("FKCOLUMN_NAME"), 				
				rs.getString("PKCOLUMN_NAME")
			)

	private def foreignPartsToForeignKeys(list : List[ForeignKeyPart]) : List[ForeignKey] = {
		val mapByKey = list.groupBy(part => part.key)
		mapByKey .toList.map({case (key, listOfKey) => 
			foreignKeyPartToForeignKey(key,  
				listOfKey.map(_.fromField).toList, 
				listOfKey.map(_.toField).toList
			)
		})
	}

	private def foreignKeyPartToForeignKey(key : ForeignKeyKey, from : List[String], to : List[String]) = 
		ForeignKey(key.name, 
			FieldsOnTable(key.fromTable, from),
			FieldsOnTable(key.toTable, to)
			)


	private def rsToForeignKeys(rs : ResultSet) : List[ForeignKey] = {
		val list = new ListBuffer[ForeignKeyPart]()
		while(rs.next) 
			list += rsToForeignPart(rs)			
		foreignPartsToForeignKeys(list.toList)
	}

	private def turnForeignKey(key : ForeignKey) =
		ForeignKey(key.name, key.to, key.from)

	/**
		All the foreign keys from the table and TO the table (used in reverse order)
	*/
	def foreignKeys(tableName : String, useResult : ForeignKeys => Unit) : Unit = {
		var meta = connection.getMetaData()
		using(meta.getImportedKeys(null, schema.orNull, tableName)) { rs =>
			val keysImported = rsToForeignKeys(rs) 
			using(meta.getExportedKeys(null, schema.orNull, tableName)) { rs =>
				val keysExported = rsToForeignKeys(rs).map(turnForeignKey(_)) 
				println("keysImported:"+keysImported+"\nkeysExported:"+keysExported)
				val keys = keysImported ++ keysExported
				useResult(ForeignKeys(keys))
			} 
		}
	}
}
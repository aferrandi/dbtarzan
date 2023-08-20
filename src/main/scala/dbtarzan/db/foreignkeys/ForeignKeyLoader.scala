package dbtarzan.db.foreignkeys

import dbtarzan.db.util.ExceptionToText
import dbtarzan.db.util.ResourceManagement.using
import dbtarzan.db._

import dbtarzan.messages.{DatabaseIdUtil, TLogger}

import java.sql.{ResultSet, SQLException}
import scala.collection.mutable.ListBuffer

/**
  The part of the database actor that reads the foreign keys
  schema is the database schema (in case of Oracle and SQL server)
*/
class ForeignKeyLoader(connection : java.sql.Connection, databaseId: DatabaseId, simpleDatabaseId: SimpleDatabaseId, definition: DBDefinition, log: TLogger) {
  /* the foreign key between two tables, has a name */
  case class ForeignKeyKey(name: String, fromTable : TableId, toTable : TableId)
  /* a column of the foreign key */
  case class ForeignKeyColumn(key : ForeignKeyKey, fromField : String, toField : String)

  implicit def tableIdOrdering[A <: TableId]: Ordering[A] = (x: A, y: A) => {
    val tableNameOrdering = x.tableName.compareTo(y.tableName)
    if (tableNameOrdering != 0)
      tableNameOrdering
    else
      DatabaseIdUtil.databaseIdText(x.databaseId).compareTo(DatabaseIdUtil.databaseIdText(y.databaseId))
  }

  /* extract the foreign key from the result set */
  private def rsToForeignColumn(rs : ResultSet) = 
    ForeignKeyColumn(
        ForeignKeyKey(
          rs.getString("FK_NAME"), 
          TableId(databaseId, simpleDatabaseId, rs.getString("FKTABLE_NAME")),
          TableId(databaseId, simpleDatabaseId, rs.getString("PKTABLE_NAME"))
        ), 
        rs.getString("FKCOLUMN_NAME"), 				
        rs.getString("PKCOLUMN_NAME")
      )

  /* converts the foreign columns of a foreign key to the foreign key itself */
  private def foreignColumnsToForeignKeys(list : List[ForeignKeyColumn]) : List[ForeignKey] = {
    val mapByKey = list.groupBy(column => column.key)
    mapByKey.toList.map({case (key, listOfKey) => 
      foreignKeyColumnsToForeignKey(key,  
        listOfKey.map(_.fromField).toList, 
        listOfKey.map(_.toField).toList
      )
    })
  }

  private def foreignKeyColumnsToForeignKey(key : ForeignKeyKey, from : List[String], to : List[String]) =
    ForeignKey(key.name, 
      FieldsOnTable(key.fromTable, from),
      FieldsOnTable(key.toTable, to),
      ForeignKeyDirection.STRAIGHT
      )


  /* reads the columns of foreign keys from the database and then builds the actual foreign keys from them */
  private def rsToForeignKeys(rs : ResultSet) : List[ForeignKey] = {
    val list = new ListBuffer[ForeignKeyColumn]()
    while(rs.next) 
      list += rsToForeignColumn(rs)			
    foreignColumnsToForeignKeys(list.toList)
  }

  private def turnForeignKey(key : ForeignKey) =
    ForeignKey(key.name, key.to, key.from, ForeignKeyDirection.turn(key.direction))

  /**
    All the foreign keys from the table and TO the table (used in reverse order)
  */
  def foreignKeys(tableId : TableId) : ForeignKeys = try 
      val meta = connection.getMetaData
      using(meta.getImportedKeys(definition.catalog.orNull, definition.schemaId.map(_.schema.schema).orNull, tableId.tableName)) { rs =>
        val keysImported = rsToForeignKeys(rs) 
        using(meta.getExportedKeys(definition.catalog.orNull, definition.schemaId.map(_.schema.schema).orNull, tableId.tableName)) { rs =>
          val keysExported = rsToForeignKeys(rs).map(turnForeignKey)
          log.debug("keysImported:"+keysImported+"\nkeysExported:"+keysExported)
          val keys = keysImported ++ keysExported
          val keysSorted = keys.sortBy(key => (key.to.table, key.name) )
          ForeignKeys(keysSorted)
        } 
      }
    catch 
      case se : SQLException  => throw new Exception("Reading the foreign keys of the "+tableId.tableName +" table got "+ExceptionToText.sqlExceptionText(se), se)
      case ex : Throwable => throw new Exception("Reading the foreign keys of the "+tableId.tableName +" table got", ex)
}
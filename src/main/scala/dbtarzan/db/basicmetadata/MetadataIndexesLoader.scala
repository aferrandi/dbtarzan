package dbtarzan.db.basicmetadata

import dbtarzan.db.util.ResourceManagement.using
import dbtarzan.db.util.{ExceptionToText, ResultSetReader}
import dbtarzan.db.{DBDefinition, Index, IndexField, Indexes, OrderByDirection}
import dbtarzan.messages.TLogger

import java.sql.{DatabaseMetaData, ResultSet, SQLException}

class MetadataIndexesLoader(definition: DBDefinition, meta : DatabaseMetaData, log: TLogger) {
  private case class IndexFieldWithPosition(ordinalPosition: Short, field: IndexField)
  private case class IndexLine(name: String, fieldWithPosition: IndexFieldWithPosition)

  def indexes(tableName : String) : Indexes = try {
    using(meta.getIndexInfo(definition.catalog.orNull, definition.schemaId.map(_.schema.schema).orNull, tableName, false, true)) { rs =>
      val lines = readIndexLines(rs)
      Indexes(indexLinesToIndexes(lines))
    }
  } catch {
    case se : SQLException  => throw new Exception("Reading the columns of the "+tableName +" table got "+ExceptionToText.sqlExceptionText(se), se)
    case ex : Throwable => throw new Exception("Reading the columns of the "+tableName +" table got", ex)
  }

  private def indexLinesToIndexes(lines: List[IndexLine]) : List[Index] = {
    val stringToLines = lines
      .filter(index => Option(index.name).isDefined)
      .groupBy(_.name).map({
        case  (indexName, fields)  => Index(indexName, fields.sortBy(_.fieldWithPosition.ordinalPosition).map(_.fieldWithPosition.field))
      })
    stringToLines.toList
  }

  private def readIndexLines(rs : ResultSet) : List[IndexLine] =
    ResultSetReader.readRS(rs, r => {
      val indexName = r.getString("INDEX_NAME")
      val fieldName = r.getString("COLUMN_NAME")
      val directionText = r.getString("ASC_OR_DESC")
      val ordinalPosition = r.getShort("ORDINAL_POSITION")
      IndexLine(indexName, IndexFieldWithPosition(ordinalPosition, IndexField(fieldName, toDirection(directionText))))
    })

  /* converts the database column type to a DBTarzan internal type */
  private def toDirection(ascOrDesk: String) : Option[OrderByDirection] =
    ascOrDesk match {
      case "D" => Some(OrderByDirection.DESC)
      case "A" => Some(OrderByDirection.ASC)
      case _ => None
    }
}

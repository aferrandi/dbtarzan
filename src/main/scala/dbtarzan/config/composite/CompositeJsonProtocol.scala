package dbtarzan.config.composite

import dbtarzan.db.{Composite, CompositeId, DatabaseId}
import spray.json._

object CompositeIdJsonProtocol extends DefaultJsonProtocol {
  implicit val compositeIdFormat = jsonFormat(CompositeId,
    "compositeName"
  )
}

object DatabaseIdJsonProtocol extends DefaultJsonProtocol {
  implicit val databaseIdFormat = jsonFormat(DatabaseId,
    "databaseName"
  )
}

object CompositeJsonProtocol extends DefaultJsonProtocol {
  import CompositeIdJsonProtocol._
  import DatabaseIdJsonProtocol._
  implicit val compositeFormat = jsonFormat(Composite,
  	"compositeId",
    "databaseIds",
  	)
}

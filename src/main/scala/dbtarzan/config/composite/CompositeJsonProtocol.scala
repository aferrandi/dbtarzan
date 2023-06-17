package dbtarzan.config.composite

import dbtarzan.db.{Composite, CompositeId, SimpleDatabaseId}
import spray.json._

object CompositeIdJsonProtocol extends DefaultJsonProtocol {
  implicit val compositeIdFormat: RootJsonFormat[CompositeId] = jsonFormat(CompositeId,
    "compositeName"
  )
}

object SimpleDatabaseIdJsonProtocol extends DefaultJsonProtocol {
  implicit val simpleDatabaseIdFormat: RootJsonFormat[SimpleDatabaseId] = jsonFormat(SimpleDatabaseId,
    "databaseName"
  )
}

object CompositeJsonProtocol extends DefaultJsonProtocol {
  import CompositeIdJsonProtocol._
  import SimpleDatabaseIdJsonProtocol._
  implicit val compositeFormat: RootJsonFormat[Composite] = jsonFormat(Composite,
  	"compositeId",
    "databaseIds",
  	)
}

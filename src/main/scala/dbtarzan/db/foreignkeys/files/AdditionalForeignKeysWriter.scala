package dbtarzan.db.foreignkeys.files

import dbtarzan.db.AdditionalForeignKey
import grapple.json.{*, given}
object AdditionalForeignKeysWriter {
  def toText(list: List[AdditionalForeignKey]): String = {
    Json.toPrettyPrint(Json.toJson(list))
  }
}

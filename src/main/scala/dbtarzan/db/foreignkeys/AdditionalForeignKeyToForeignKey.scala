package dbtarzan.db.foreignkeys

import dbtarzan.db.{AdditionalForeignKey, ForeignKey, ForeignKeyDirection, ForeignKeys}

object AdditionalForeignKeyToForeignKey {
    def toForeignKeys(keys: List[AdditionalForeignKey]) : Map[String, ForeignKeys] = {
        val keysStraight = keys.map(k => ForeignKey(k.name+"_straight", k.from, k.to, ForeignKeyDirection.STRAIGHT))
        val keysTurned = keys.map(k => ForeignKey(k.name+"_turned", k.to, k.from, ForeignKeyDirection.TURNED))
        (keysStraight ++ keysTurned).groupBy(_.from.table).mapValues(ForeignKeys).toMap
    }
}

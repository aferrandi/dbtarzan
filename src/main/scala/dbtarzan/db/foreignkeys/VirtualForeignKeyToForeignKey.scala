package dbtarzan.db.foreignkeys

import dbtarzan.db.{VirtualalForeignKey, ForeignKey, ForeignKeyDirection, ForeignKeys, TableId}

object VirtualForeignKeyToForeignKey {
    def toForeignKeys(keys: List[VirtualalForeignKey]) : Map[TableId, ForeignKeys] = {
        val keysStraight = keys.map(k => ForeignKey(k.name+"_straight", k.from, k.to, ForeignKeyDirection.STRAIGHT))
        val keysTurned = keys.map(k => ForeignKey(k.name+"_turned", k.to, k.from, ForeignKeyDirection.TURNED))
        (keysStraight ++ keysTurned).groupBy(_.from.table).view.mapValues(ks => ForeignKeys.apply(ks)).toMap
    }
}

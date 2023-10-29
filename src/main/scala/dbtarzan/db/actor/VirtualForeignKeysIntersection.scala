package dbtarzan.db.actor

import dbtarzan.db._
import dbtarzan.messages.DatabaseIdUtil

/* to check if virtual foreign keys match existing foreign keys */
object VirtualForeignKeysIntersection {
  private def equalsIgnoreCase(a: FieldsOnTable, b: FieldsOnTable) : Boolean =
    a.table.tableName.equalsIgnoreCase(b.table.tableName) &&
    a.table.databaseId.origin.isLeft == b.table.databaseId.origin.isLeft &&
    DatabaseIdUtil.databaseIdText(a.table.databaseId).equalsIgnoreCase(DatabaseIdUtil.databaseIdText(b.table.databaseId)) &&
    a.fields.size == b.fields.size &&
    a.fields.sorted.zip(b.fields.sorted).forall({case (aa, bb) => aa.equalsIgnoreCase(bb)})


  private def intersectionOneTable(virtualKey :VirtualalForeignKey, foreignKeysForTable : List[ForeignKey] ) : Boolean =
    foreignKeysForTable.exists(k =>
      (equalsIgnoreCase(k.from, virtualKey.from) && equalsIgnoreCase(k.to, virtualKey.to)) ||
      (equalsIgnoreCase(k.from, virtualKey.to) && equalsIgnoreCase(k.to, virtualKey.from))
      )

  private def extractForeignKeysForTable(foreignKeysByTable: scala.collection.Map[TableId, ForeignKeys], table : TableId) : List[ForeignKey] =
    foreignKeysByTable.get(table).map(_.keys).getOrElse(List.empty)

  def intersection(foreignKeysByTable: scala.collection.Map[TableId, ForeignKeys], virtualKeys :List[VirtualalForeignKey]) : List[String] =
       virtualKeys.filter(ak => {
          val foreignKeysFrom = extractForeignKeysForTable(foreignKeysByTable, ak.from.table)
          val foreignKeysTo = extractForeignKeysForTable(foreignKeysByTable, ak.to.table)
          intersectionOneTable(ak, foreignKeysFrom) || intersectionOneTable(ak, foreignKeysTo)
        }).map(_.name)
}
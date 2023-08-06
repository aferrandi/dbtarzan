package dbtarzan.db

abstract sealed class FieldType
/* the fields types that are normally used in a foreign key */
object FieldType {
  object STRING extends FieldType
  object INT extends FieldType
  object FLOAT extends FieldType
}


abstract sealed class OrderByDirection
/* ascending or descending in the queries order by */
object OrderByDirection {
  object ASC extends OrderByDirection
  object DESC extends OrderByDirection

  def directions(): List[OrderByDirection] = List(OrderByDirection.ASC, OrderByDirection.DESC)
}


abstract sealed class ForeignKeyDirection
/* tells if a foreign key is straight (owned by the "from" table) or turned (originally owned by the "to" table) */
object ForeignKeyDirection {
  object STRAIGHT extends ForeignKeyDirection
  object TURNED extends ForeignKeyDirection
  def turn(direction : ForeignKeyDirection) : ForeignKeyDirection =
    direction match {
      case ForeignKeyDirection.STRAIGHT => ForeignKeyDirection.TURNED
      case ForeignKeyDirection.TURNED => ForeignKeyDirection.STRAIGHT
    }
}


 
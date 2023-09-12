package dbtarzan.db

/* the fields types that are normally used in a foreign key */
enum FieldType {
  case STRING, INT, FLOAT, BINARY
}


/* ascending or descending in the queries order by */
enum OrderByDirection {
  case ASC, DESC
}


/* tells if a foreign key is straight (owned by the "from" table) or turned (originally owned by the "to" table) */
enum ForeignKeyDirection {
  case STRAIGHT, TURNED
}
object ForeignKeyDirection {
  def turn(direction : ForeignKeyDirection) : ForeignKeyDirection =
    direction match {
      case ForeignKeyDirection.STRAIGHT => ForeignKeyDirection.TURNED
      case ForeignKeyDirection.TURNED => ForeignKeyDirection.STRAIGHT
    }
}


 
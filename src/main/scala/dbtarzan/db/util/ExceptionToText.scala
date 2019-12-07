package dbtarzan.db.util

import java.sql.SQLException

object ExceptionToText {
    def sqlExceptionText(se : SQLException) : String = 
              "SQL exception "+se.getMessage()+" with state "+se.getSQLState()+" and error code "+se.getErrorCode() 
}
package dbtarzan.db

import java.sql.Connection
import dbtarzan.config.ConnectionData

trait ConnectionProvider {
    def getConnection(data : ConnectionData) : Connection
}
package dbtarzan.db

import java.sql.Connection
import dbtarzan.config.connections.ConnectionData
import dbtarzan.config.password.Password

trait ConnectionProvider {
    def getConnection(data : ConnectionData, loginPassword: Option[Password]) : Connection
}
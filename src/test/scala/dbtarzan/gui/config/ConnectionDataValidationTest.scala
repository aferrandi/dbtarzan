package dbtarzan.gui.config

import dbtarzan.config.connections.ConnectionData
import dbtarzan.config.password.Password
import dbtarzan.db.{IdentifierDelimiters, SchemaName}
import dbtarzan.gui.config.connections.ConnectionDataValidation
import org.scalatest.flatspec.AnyFlatSpec

class ConnectionDataValidationTest extends AnyFlatSpec {
  "correct connection data" should "give no error" in {
    val errors = ConnectionDataValidation.validate(
      ConnectionData(
        "/testdbs/sqllite/sqlite-jdbc-3.8.11.2.jar",
        "chinook",
        "org.sqlite.JDBC",
        "jdbc:sqlite:/home/andrea/prj/dbtarzan/testdbs/sqllite/Chinook_Sqlite.sqlite",
        Some(SchemaName("chinook")),
        "root",
        Some(Password("pwd")),
        None,
        Some(IdentifierDelimiters('[', ']')),
        Some(300),
        Some(20),
        Some(1000),
        Some(1000),
        None
      )
    )
    assert(errors.length === 0)
  }

  "connection data with empty fields " should "give error" in {
    val errors = ConnectionDataValidation.validate(
      ConnectionData(
        "",
        "",
        "",
        "",
        None,
        "",
        Some(Password("")),
        None,
        None,
        None,
        None,
        None,
        None,
        None
      )
    )
    assert(List("Empty name", "Name must be an identifier", "Empty url", "Url must be in URL form", "Empty driver", "Empty jar") === errors)
  }

  "connection data with fields with spaces" should "give error" in {
    val errors = ConnectionDataValidation.validate(
      ConnectionData(
        "with spaces",
        "point.in.the middle",
        "org.sqlite.JDBC",
        "url with spaces",
        None,
        "",
        Some(Password("")),
        None,
        None,
        None,
        None,
        Some (100),
        Some(1000),
        None
      )
    )
    assert(List("Name must be an identifier", "Url cannot contain spaces", "Url must be in URL form", "Jar cannot contain spaces", "Max field size should be over 200") === errors)
  }

}

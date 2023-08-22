package dbtarzan.db.foreignkeys

import dbtarzan.db.foreignkeys.files.{AdditionalForeignKeysReader, AdditionalForeignKeysWriter, ForeignKeysFile}
import grapple.json.{*, given}
import org.scalatest.flatspec.AnyFlatSpec

class AdditionalForeignKeyReaderTest extends AnyFlatSpec {
  "Additional foreign keys" should "be parseable" in {
    val jsonIn = """
      [{
      "name": "customerkeys",
      "from": {
        "table": {
          "databaseId": {
            "origin": {
              "compositeName": "chinookNorthwind"
            }
          },
          "simpleDatabaseId": {
            "databaseName": "Northwind"
          },
          "tableName": "Customers"
        },
        "fields": ["CustomerID"]
      },
      "to": {
        "table": {
          "databaseId": {
            "origin": {
              "compositeName": "chinookNorthwind"
            }
          },
          "simpleDatabaseId": {
            "databaseName": "chinook"
          },
          "tableName": "Customer"
        },
        "fields": ["CustomerId"]
      }
    }]
        """
    val keys = AdditionalForeignKeysReader.parseText(jsonIn)
    val jsonOut = AdditionalForeignKeysWriter.toText(keys)
    assert(jsonIn.replaceAll("\\s+", "") === jsonOut.replaceAll("\\s+", ""))
  }
}

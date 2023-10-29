package dbtarzan.db.foreignkeys

import dbtarzan.db.foreignkeys.files.{VirtualForeignKeysReader, VirtualForeignKeysWriter, ForeignKeysFile}
import grapple.json.{*, given}
import org.scalatest.flatspec.AnyFlatSpec

class VirtualalForeignKeyReaderTest extends AnyFlatSpec {
  "virtual foreign keys" should "be parseable" in {
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
    val keys = VirtualForeignKeysReader.parseText(jsonIn)
    val jsonOut = VirtualForeignKeysWriter.toText(keys)
    assert(jsonIn.replaceAll("\\s+", "") === jsonOut.replaceAll("\\s+", ""))
  }
}

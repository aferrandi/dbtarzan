package dbtarzan.db.foreignkeys

import dbtarzan.localization.Languages
import dbtarzan.config.password.{Password, VerificationKey}
import dbtarzan.db.{DatabaseId, SimpleDatabaseId}
import dbtarzan.db.foreignkeys.files.{ForeignKeysFile, ForeignKeysReader, ForeignKeysWriter}
import dbtarzan.testutil.TestDatabaseIds
import org.scalatest.flatspec.AnyFlatSpec
import grapple.json.{*, given}

class ForeignKeyReaderTest extends AnyFlatSpec {
  "Foreign keys" should "be parseable" in {
    val jsonIn = """
      [
        {
          "table": "AIRLINES",
          "keys": {
            "keys": []
          }
        },
        {
          "table": "CITIES",
          "keys": {
            "keys": [
              {
                "name": "COUNTRIES_FK",
                "from": {
                  "table": "CITIES",
                  "fields": [
                    "COUNTRY_ISO_CODE"
                  ]
                },
                "to": {
                  "table": "COUNTRIES",
                  "fields": [
                    "COUNTRY_ISO_CODE"
                  ]
                },
                "direction": "STRAIGHT"
              }
            ]
          }
        }]
        """
    val reader = ForeignKeysReader(TestDatabaseIds.databaseId, TestDatabaseIds.simpleDatabaseId)
    val keys = reader.parseText(jsonIn)
    val jsonOut = ForeignKeysWriter.toText(keys)
    assert(jsonIn.replaceAll("\\s+", "") === jsonOut.replaceAll("\\s+", ""))
  }
}

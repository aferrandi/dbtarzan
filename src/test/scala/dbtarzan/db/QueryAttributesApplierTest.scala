package dbtarzan.db

import dbtarzan.testutil.TestDatabaseIds
import org.scalatest.flatspec.AnyFlatSpec

class QueryAttributesApplierTest extends AnyFlatSpec {

  "applying both delimiters and schema" should "give a table with delimeters and schema" in {
    val applier = QueryAttributesApplier.from(QueryAttributes(Some(IdentifierDelimitersValues.squareBrackets), DBDefinition(Some(SchemaId(TestDatabaseIds.databaseId, TestDatabaseIds.simpleDatabaseId, SchemaName("TST"))), None), None, None))
    assert("[TST].[TBL]" === applier.applySchemaAndDelimiters("TBL"))
  }

  "applying only delimiters" should "give a table with only delimeters" in {
    val applier = QueryAttributesApplier.from(QueryAttributes(Some(IdentifierDelimitersValues.squareBrackets), DBDefinition(None, None), None, None))
    assert("[TBL]" === applier.applySchemaAndDelimiters("TBL"))
  }

  "applying only schmea" should "give a table with only schema" in {
    val applier = QueryAttributesApplier.from(QueryAttributes(None, DBDefinition(Some(SchemaId(TestDatabaseIds.databaseId, TestDatabaseIds.simpleDatabaseId, SchemaName("TST"))), None), None, None))
    assert("TST.TBL" === applier.applySchemaAndDelimiters("TBL"))
  }

  "applying none" should "not change the table" in {
    val applier = QueryAttributesApplier.from(QueryAttributes(None, DBDefinition(None, None), None, None))
    assert("TBL" === applier.applySchemaAndDelimiters("TBL"))
  }

}
package dbtarzan.db

import org.scalatest.FlatSpec

class QueryAttributesApplierTest extends FlatSpec {
  "applying both delimiters and schema" should "give a table with delimeters and schema" in {
	val applier = QueryAttributesApplier.from(QueryAttributes(Some(IdentifierDelimitersValues.squareBrackets), DBDefinition(Some(Schema("TST")), None), None))
	assert("[TST].[TBL]" === applier.applySchemaAndDelimiters("TBL"))
  }

  "applying only delimiters" should "give a table with only delimeters" in {
	val applier = QueryAttributesApplier.from(QueryAttributes(Some(IdentifierDelimitersValues.squareBrackets), DBDefinition(None, None), None))
	assert("[TBL]" === applier.applySchemaAndDelimiters("TBL"))
  }

  "applying only schmea" should "give a table with only schema" in {
	val applier = QueryAttributesApplier.from(QueryAttributes(None, DBDefinition(Some(Schema("TST")), None), None))
	assert("TST.TBL" === applier.applySchemaAndDelimiters("TBL"))
  }

  "applying none" should "not change the table" in {
	val applier = QueryAttributesApplier.from(QueryAttributes(None, DBDefinition(None, None), None))
	assert("TBL" === applier.applySchemaAndDelimiters("TBL"))
  }

}
package dbtarzan.db

import org.scalatest.FlatSpec

class QueryAttributesApplierTest extends FlatSpec {
  "applying both delimiters and schema" should "give a table with delimeters and schema" in {
	val applier = QueryAttributesApplier.from(QueryAttributes(Some(IdentifierDelimiters('[', ']')), Some("TST")))
	assert("[TST].[TBL]" === applier.applyBoth("TBL"))
  }

  "applying only delimiters" should "give a table with only delimeters" in {
	val applier = QueryAttributesApplier.from(QueryAttributes(Some(IdentifierDelimiters('[', ']')), None))
	assert("[TBL]" === applier.applyBoth("TBL"))
  }

  "applying only schmea" should "give a table with only schema" in {
	val applier = QueryAttributesApplier.from(QueryAttributes(None, Some("TST")))
	assert("TST.TBL" === applier.applyBoth("TBL"))
  }

  "applying none" should "not change the table" in {
	val applier = QueryAttributesApplier.from(QueryAttributes(None, None))
	assert("TBL" === applier.applyBoth("TBL"))
  }

}
package dbtarzan.gui.util

import org.scalatest.flatspec.AnyFlatSpec


class ValidationTest extends AnyFlatSpec {
  "a jdbc URL" should "should validate" in {
  	assert(Validation.isValidJdbcURL("jdbc:sqlite:/home/andrea/prj/dbtarzan/testdbs/sqllite/Chinook_Sqlite.sqlite") === true)
  }

  "a correct http URL" should "should not validate" in {
  	assert(Validation.isValidJdbcURL("http://www.linux.org") === false)
  }

  "a correct https URL with port" should "should not validate" in {
  	assert(Validation.isValidJdbcURL("https://www.linux.org:9000") === false)
  }

  "a correct ftp URL" should "should not validate" in {
  	assert(Validation.isValidJdbcURL("ftp://linux.org") === false)
  }

  "a simple word" should "should not validate" in {
  	assert(Validation.isValidJdbcURL("localhost") === false)
  }

  "a word" should "not contain whitespace" in {
  	assert(Validation.containsWhitespace("table") === false)
  }

  "a phrase" should "contain whitespace" in {
  	assert(Validation.containsWhitespace("not validate") === true)
  }
  
  "a phrase with tab" should "contain whitespace" in {
  	assert(Validation.containsWhitespace("not\tvalidate") === true)
  }

  "the number 20" should "be more than 10" in {
    assert(Validation.isMoreThanOrNone(Some(20), 10) === true)
  }

  "the number 20" should "not be more than 30" in {
    assert(Validation.isMoreThanOrNone(Some(20), 30) === false)
  }

  "an empty number" should "be more than 10" in {
    assert(Validation.isMoreThanOrNone(None, 10) === true)
  }

  "a word with only digit and letters" should "should validate" in {
    assert(Validation.isIdentifier("Dog12Cat12") === true)
  }

  "a word with space" should "should not validate" in {
    assert(Validation.isIdentifier("Dog12 Cat12") === false)
  }

  "a word with underscore or dash" should "should validate" in {
    assert(Validation.isIdentifier("Dog12_Cat-12-") === true)
  }

  "a word with characters that are not of an identifier" should "should not validate" in {
    assert(Validation.isIdentifier("D?a3") === false)
  }


  "<NEW>" should "should not validate" in {
    assert(Validation.isIdentifier("<NEW>") === false)
  }
}
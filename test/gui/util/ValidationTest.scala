package dbtarzan.gui.util

import org.scalatest.FlatSpec


class ValidationTest extends FlatSpec {
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
}
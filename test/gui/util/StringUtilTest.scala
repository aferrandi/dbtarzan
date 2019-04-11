package dbtarzan.gui.util

import org.scalatest.FlatSpec


class StringUtilTest extends FlatSpec {
  "shortening a string" should "should give a string of the desired length with 3 dots at the end" in {
  	assert(StringUtil.shortenIfTooLong("Zarathustra", 5) === "Za...")
  }

  "shortening a string already shorter than the desired length" should "should return the same string" in {
  	assert(StringUtil.shortenIfTooLong("Zarathustra", 20) === "Zarathustra")
  }

  "shortening a string already of the desired length" should "should return the same string" in {
  	assert(StringUtil.shortenIfTooLong("Zarathustra", 11) === "Zarathustra")
  }

  "shortening a string one character longer than the desired length" should "should give a string of the desired length with 3 dots at the end" in {
  	assert(StringUtil.shortenIfTooLong("Zarathustra", 10) === "Zarathu...")
  }

  "shortening a string with desired length 3" should "should not give a string with dots" in {
  	assert(StringUtil.shortenIfTooLong("Zarathustra", 3) === "Zar")
  }
}
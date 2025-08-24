package dbtarzan.gui.table

import scala.util.Random

import dbtarzan.db._
import org.scalatest.flatspec.AnyFlatSpec


class TableColumnsMaxSizesTest extends AnyFlatSpec {
  "maxLength" should "contain the maximum lengths of rows and header" in {
    val sizes = new TableColumnsMaxSizes(List(
        Field("id", FieldType.STRING, "", Some(10)), 
        Field("verylong", FieldType.STRING, "", Some(1000)),
        Field("muchlongerthanthat", FieldType.STRING, "", Some(10000)),
        Field("shorter", FieldType.STRING, "", Some(30))
    ), new Random(32321))
    sizes.addRows(List(
        Row(List("1", "a", "b", "0123456789")),
        Row(List("2", "cdd", "sdsd", "0123456789")),
        Row(List("3", "a", "b", "0123456789")),
        Row(List("23", "a", "b", "01234"))
    ))
    assert(sizes.maxLengths === List(2, 8, 18, 10))
  }
   "maxLength for zero lines" should "not give an excepion"  in {
    val sizes = new TableColumnsMaxSizes(List(
        Field("id", FieldType.STRING, "", Some(10)), 
        Field("verylong", FieldType.STRING, "", Some(1000))
        ), new Random(32321))
    sizes.addRows(List.empty)
   }
}
package dbtarzan.gui.table

import org.scalatest.flatspec.AnyFlatSpec

class TableColumnsFitterTest extends AnyFlatSpec {
  "logistic of 10" should "be around 10" in {
    assert(TableColumnsFitter.logistic(10) === 10.686513575978815)
  }

  "logistic of 20" should "be around 20" in {
    assert(TableColumnsFitter.logistic(20) === 21.24628288301699)
  }

  "logistic of 50" should "be around 50" in {
    assert(TableColumnsFitter.logistic(50) === 46.843696553792995)
  }
  
  "logistic of 100" should "be around 50" in {
    assert(TableColumnsFitter.logistic(100) === 49.97731033621018)
  }
}
package dbtarzan.gui.table

import scalafx.scene.control.{TableView, TableColumn}
import scala.util.Random
import scalafx.Includes._

import dbtarzan.gui.util.JFXUtil
import dbtarzan.db._

object TableColumnsFitter {
    /* a logistic (sigmoid) function, almost linear, returns max 50 */ 
    def logistic(x : Double) : Double =
       50.0 / (1.0 + 10.0 * scala.math.exp(-.1 * x))
}

class TableColumnsFitter[S](table : TableView[S], columns : List[Field]) {
    private val maxSizes = new TableColumnsMaxSizes(columns, new Random())
    private val charSize = JFXUtil.averageCharacterSize()

    private def resizeColumn(column : TableColumn[S, _], size : Int) : Unit = {
        val x = TableColumnsFitter.logistic((size+2)) 
        column.prefWidth = x * charSize
    }

    def addRows(rows : List[Row]) : Unit = {
      val columns =  table.columns.drop(1)
      maxSizes.addRows(rows)
      val newSizes = maxSizes.maxLengths
      // println("newSizes "+newSizes)
      columns.zip(newSizes).foreach({
          case (column, size) => resizeColumn(column, size)
          }) 
    }
}
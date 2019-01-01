package dbtarzan.gui.table

import scalafx.scene.control.TableView
import dbtarzan.gui.util.JFXUtil
import dbtarzan.db._
import scalafx.Includes._

object TableColumnsFitter {
    /* a logistic (sigmoid) function, almost linear, returns max 50 */ 
    def logistic(x : Double) : Double =
       50.0 / (1.0 + 10.0 * scala.math.exp(-.1 * x))
}

class TableColumnsFitter(table : TableView[_], columns : List[Field]) {
    val maxSizes = new TableColumnsMaxSizes(columns)
    val charSize = JFXUtil.averageCharacterSize()

    def addRows(rows : List[Row]) : Unit = {
      val columns =  table.columns.drop(1)
      println("columns "+columns)
      maxSizes.addRows(rows)
      val newSizes = maxSizes.maxLengths
      println("newSizes "+newSizes)
      columns.zip(newSizes).foreach({case (column, size) => {
           val x = TableColumnsFitter.logistic((size+2)) 
           println("Resize "+size+","+charSize+"=> "+x)
           column.prefWidth = x * charSize
           }}) 

    }


}
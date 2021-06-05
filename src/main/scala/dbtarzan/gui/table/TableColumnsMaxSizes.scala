package dbtarzan.gui.table

import dbtarzan.db._
import scala.util.Random


class TableColumnsMaxSizes(columns : List[Field], rnd : Random) {
    private var lengths = rowLengths(columns.map(_.name))

    def addRows(rows : List[Row]) : Unit = {   
        val randomRowsValues = randomRows(rows).map(_.values)
        val maxNewRows = maxRows(randomRowsValues)
        lengths = max2Rows(lengths, maxNewRows)
    }

    def maxLengths : List[Int] = lengths

    private def randomRows(rows : List[Row]) : List[Row] = {   
        val finalSize = Math.min(rows.size, Math.max(rows.size / 10, 10)) 
        val arrRows = rows.toArray
        val indexes = List.fill(finalSize)(rnd.nextInt(arrRows.length))
        indexes.map(i => arrRows(i))
    }

    private def rowLengths(row : List[String]) : List[Int] = {
        def nullableLength(s : String) : Int = if(s != null) s.length else 0
        row.map(nullableLength)
    }
    private def max2Rows(a : List[Int], b: List[Int]) : List[Int] = 
        (a, b).zipped.map(Math.max)
    
    private def maxRows(rows : List[List[String]]) : List[Int] = {
        val rowsSizes = rows.map(rowLengths)
        if(!rowsSizes.isEmpty)
            rowsSizes.tail.foldLeft(rowsSizes.head)(max2Rows)
        else
            lengths
    }
}

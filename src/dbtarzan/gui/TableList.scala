package dbtarzan.gui

import scalafx.collections.ObservableBuffer 
import scalafx.scene.control.ListView
import dbtarzan.db.TableNames
import dbtarzan.gui.util.JFXUtil
import scalafx.scene.Parent

/* The list of tables to choose from */
class TableList(names : TableNames) extends TControlBuilder {
  private val buffer = ObservableBuffer(names.tableNames)
  private val list = new ListView[String](buffer)

  def addTableNames(names : TableNames) : Unit = {
    buffer.clear()
    buffer ++= names.tableNames
  }

  def onTableSelected(useTable : String => Unit) : Unit = {
      JFXUtil.onAction(list, { (selectedTable : String, _) =>
        println("Selected "+selectedTable)      
        useTable(selectedTable)
        })
    }
  def control : Parent = list

  def tableNames = TableNames(buffer.toList)
}


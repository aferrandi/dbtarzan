package dbtarzan.gui

import scalafx.collections.ObservableBuffer 
import scalafx.scene.control.{ ListView, ListCell, Tooltip}
import scalafx.Includes._
import dbtarzan.messages.Error
/**
  The list of tables to choose from
*/
class ErrorList extends TErrors {
  val buffer = ObservableBuffer.empty[Error]
  val list = new ListView[Error](buffer) {
      cellFactory = { _ => buildCell() }
    }   
  
  /**
    need to show only the "to table" as cell text. And a tooltip for each cell
  */
  private def buildCell() = new ListCell[Error] {
          item.onChange { (_, _, _) => 
            Option(item.value).foreach(err => {
              tooltip.value = Tooltip(err.ex.toString)
              text.value = err.ex.getMessage
            })
          }
        }       

  def addError(err : Error) : Unit = 
    buffer += err
}


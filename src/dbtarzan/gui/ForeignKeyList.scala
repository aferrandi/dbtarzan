package dbtarzan.gui

import scalafx.scene.control.{ ListView, ListCell, Tooltip}
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.VBox
import scalafx.scene.Parent
import scalafx.collections.ObservableBuffer 
import scalafx.Includes._
import dbtarzan.db.{ForeignKey, FieldsOnTable, Field, ForeignKeys, ForeignKeyDirection}
import dbtarzan.gui.util.JFXUtil

/**	foreign keys list */
class ForeignKeyList() extends TControlBuilder {
	private val buffer = ObservableBuffer.empty[ForeignKey]
	private val list = new ListView[ForeignKey](buffer) {
	    cellFactory = { _ => buildCell() }
	  }		
	
	/** need to show only the "to table" as cell text. And a tooltip for each cell	*/
	private def buildCell() = new ListCell[ForeignKey] {
	        item.onChange { (_, _, _) => 
	          Option(item.value).foreach(key => {
		          tooltip.value = Tooltip(buildTooltip(key))
		          text.value = buildText(key)
	      	  })
	        }} 	      
	  
	def addForeignKeys(foreignKeys : ForeignKeys) : Unit = {
		println("foreignKeys "+foreignKeys)
		buffer ++= foreignKeys.keys
	}
	

	/** the tooltip show the whole foreign key */
	private def buildTooltip(key : ForeignKey) = {
		def buildSide(fields : FieldsOnTable) = fields.table + fields.fields.mkString("(", ",", ")")
		key.name + 
		"\n- "+ buildSide(key.from)+
		"\n- "+ buildSide(key.to)
	}

	/** the tooltip show the whole foreign key */
	private def buildText(key : ForeignKey) = {
		def directionText(direction : ForeignKeyDirection) = direction match {
			case ForeignKeyDirection.STRAIGHT => ">"
			case ForeignKeyDirection.TURNED => "<"
			case _  => "<ERROR>"
		}
		directionText(key.direction) + " " + key.to.table
	}
	
	/* foreign key double-clicked. handled by BrowsingTable that has knowledge of tables too */
  	def onForeignKeySelected(useKey : ForeignKey => Unit) : Unit =
	     JFXUtil.onAction(list, { selectedKey : ForeignKey =>
	        println("Selected "+selectedKey)      
	        useKey(selectedKey)
	      })
	def control : Parent = list
 }


package dbtarzan.gui

import scalafx.scene.control.{ ListView, ListCell, Tooltip}
import scalafx.scene.Parent
import scalafx.collections.ObservableBuffer 
import dbtarzan.db.{ForeignKey, FieldsOnTable, ForeignKeys, ForeignKeyDirection}
import dbtarzan.gui.util.JFXUtil

/* if the table has 2 or more foreign keys to the same table, we want to give more information to the user, so that he can understand which one to use */ 
case class ForeignKeyWithSharingCheck(key: ForeignKey, sharesToTable : Boolean)

/**	foreign keys list */
class ForeignKeyList() extends TControlBuilder {
	private val buffer = ObservableBuffer.empty[ForeignKeyWithSharingCheck]
	private val list = new ListView[ForeignKeyWithSharingCheck](buffer) {
	    cellFactory = { _ => buildCell() }
	  }		
	
	/** need to show only the "to table" as cell text. And a tooltip for each cell	*/
	private def buildCell() = new ListCell[ForeignKeyWithSharingCheck] {
	        item.onChange { (_, _, _) => 
	          Option(item.value).foreach(key => {
		          tooltip.value = Tooltip(buildTooltip(key.key))
		          text.value = buildText(key)
	      	  })
	        }} 	      
	  
	def addForeignKeys(newForeignKeys : ForeignKeys) : Unit = {
		def moreThanOneItem(l : List[_]) = l.length > 1
		println("newForeignKeys "+newForeignKeys)
		val allForeignKeys = buffer.toList.map(_.key) ++ newForeignKeys.keys
		val groupedByToTableInsensitive = allForeignKeys.groupBy(_.to.table.toUpperCase()).values
		val withSharingCheck = groupedByToTableInsensitive.flatMap(ks => ks.map(ForeignKeyWithSharingCheck(_, moreThanOneItem(ks))))
		JFXUtil.bufferSet(buffer, withSharingCheck)
	}
	
	private def fieldsToText(fields: List[String]) : String = fields.mkString("(", ",", ")")

	/** the tooltip show the whole foreign key */
	private def buildTooltip(key : ForeignKey) = {
		def buildSide(fields : FieldsOnTable) = fields.table + fieldsToText(fields.fields)
		key.name + 
		"\n- "+ buildSide(key.from)+
		"\n- "+ buildSide(key.to)
	}

	/** the text shows the table name, the direction ("<"" if the foreign key is straight, ">"" if it is turned) 
	 * if there is more than one foreign key with the same "to" table, also the foreign key fields are displayed */
	private def buildText(key : ForeignKeyWithSharingCheck) = {
		def directionText(direction : ForeignKeyDirection) = direction match {
			case ForeignKeyDirection.STRAIGHT => ">"
			case ForeignKeyDirection.TURNED => "<"
			case _  => "<ERROR>"
		}
		def fieldsIfSharesTable() = Some(key).filter(_.sharesToTable).map(k => fieldsToText(k.key.from.fields)) 
		directionText(key.key.direction) + " " + key.key.to.table + fieldsIfSharesTable().map(t => " "+t).getOrElse("")
	}
	
	/* foreign key double-clicked. handled by BrowsingTable that has knowledge of tables too */
  	def onForeignKeySelected(useKey : (ForeignKey, Boolean)  => Unit) : Unit =
	     JFXUtil.onAction(list, { (selectedKey : ForeignKeyWithSharingCheck, ctrlDown) =>
	        println("Selected "+selectedKey)      
	        useKey(selectedKey.key, ctrlDown)
	      })

	def control : Parent = list
 }


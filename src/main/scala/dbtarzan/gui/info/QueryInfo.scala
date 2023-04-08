package dbtarzan.gui.info

import dbtarzan.db.QuerySql
import dbtarzan.gui.interfaces.TControlBuilder
import dbtarzan.localization.Localization
import scalafx.scene.Parent
import scalafx.scene.control.TextArea


/** The read only text box showing the query sql, so that it can be seen anc copied */
class QueryInfo(sql : QuerySql, localization : Localization) extends TControlBuilder {
	private val textBox = new TextArea {
		text = sql.sql
    editable = false
	}

  def control : Parent = textBox
}

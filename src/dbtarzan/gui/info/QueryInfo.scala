package dbtarzan.gui.info

import scalafx.scene.control.TextArea
import scalafx.scene.Parent
import scalafx.Includes._

import dbtarzan.db.{ QuerySql }
import dbtarzan.localization.Localization
import dbtarzan.gui.TControlBuilder


/** The GUI table control showing the content of a database table in a GUI table*/
class QueryInfo(sql : QuerySql, localization : Localization) extends TControlBuilder {
	val textBox = new TextArea {
		text = sql.sql
    editable = false
	}

  def control : Parent = textBox
}

package dbtarzan.gui.tabletabs

import dbtarzan.db.DBTableStructure

/*
  Normally it shows the name of the table.

  If the tab is derived from a foreign key, it is in the form:
    [table] < [origin table]
    where origin table is the table on the other side of the foreign key

  If a filter (where) has been applied, a star (*) character is shown at the end of the text
*/
object TableStructureText {
  def buildTabText(structure : DBTableStructure) : String = {
    val description = structure.description
    description.name + description.origin.map("<"+_).getOrElse("") + starForFilter(structure)
  }

  private def starForFilter(structure : DBTableStructure): String =
    if(DBTableStructure.hasFilter(structure))
      " *"
    else
      ""
}

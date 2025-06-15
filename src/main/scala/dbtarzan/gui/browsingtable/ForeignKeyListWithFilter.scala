package dbtarzan.gui.browsingtable

import dbtarzan.db.{ForeignKey, ForeignKeys}
import dbtarzan.gui.interfaces.TControlBuilder
import dbtarzan.gui.util.FilterText
import dbtarzan.localization.Localization
import dbtarzan.log.actor.Logger
import dbtarzan.messages.*
import org.apache.pekko.actor.ActorRef
import scalafx.scene.Parent
import scalafx.scene.layout.BorderPane


/*	The list of database to choose from*/
class ForeignKeyListWithFilter(queryId : QueryId, dbActor: ActorRef, log: Logger, localization : Localization) extends TControlBuilder {
  private val foreignKeyList = new ForeignKeyList(localization, log)
  private val filterText = new FilterText(dbActor ! QueryForeignKeysByPattern(queryId, _), localization)
  private val pane = new BorderPane {
    top = filterText.control
    center = foreignKeyList.control
  }

  def addForeignKeys(newForeignKeys: ForeignKeys): Unit =
    foreignKeyList.addForeignKeys(newForeignKeys)

  def setForeignKeysByPattern(foreignKeysByPattern: ForeignKeys): Unit =
    foreignKeyList.setForeignKeysByPattern(foreignKeysByPattern)

  def onForeignKeySelected(useKey : (ForeignKey, Boolean)  => Unit) : Unit =
    foreignKeyList.onForeignKeySelected(useKey)

  def control : Parent = pane
}
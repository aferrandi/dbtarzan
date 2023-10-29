package dbtarzan.gui.database

import dbtarzan.db.DatabaseId
import dbtarzan.gui.util.JFXUtil
import dbtarzan.localization.Localization
import dbtarzan.messages.*
import org.apache.pekko.actor.ActorRef
import scalafx.Includes.*
import scalafx.event.ActionEvent
import scalafx.scene.Node
import scalafx.scene.control.Button
import scalafx.scene.image.ImageView
import scalafx.scene.layout.HBox

/* the code to build the tabs menu and the related key combinations */ 
object DatabaseButtonBar {
    private def button(text: String, icon: String, ev : ActionEvent => Unit) : Button =
      new Button(text) {
          stylesheets += "flatButton.css"
          onAction = ev
          graphic = new ImageView(JFXUtil.loadIcon(s"${icon}.png"))
      }

    def buildButtonBar(dbActor: ActorRef, databaseId: DatabaseId, localization : Localization): HBox = new HBox() {
      children = List(
        button(localization.connectionReset, "refresh", (ev: ActionEvent) => dbActor ! QueryReset(databaseId)),
        button(localization.openAdditionalForeignKeys, "deleteThis", (_: ActionEvent) => dbActor ! RequestAdditionalForeignKeys(databaseId))
      )
      spacing = 1
    }
}
package dbtarzan.gui.browsingtable

import dbtarzan.gui.browsingtable.TableButtonBar.button
import dbtarzan.gui.util.JFXUtil
import dbtarzan.localization.Localization
import dbtarzan.messages.*
import org.apache.pekko.actor.ActorRef
import scalafx.Includes.*
import scalafx.event.ActionEvent
import scalafx.scene.Node
import scalafx.scene.control.{Button, ButtonBar}
import scalafx.scene.image.ImageView
import scalafx.scene.input.{KeyCode, KeyCodeCombination, KeyCombination, KeyEvent}
import scalafx.scene.layout.HBox

/* the code to build the tabs menu and the related key combinations */ 
object TableButtonBar {
    val CLOSE_THIS_TAB = new KeyCodeCombination(KeyCode.Delete, KeyCombination.ControlDown)
    val CLOSE_TAB_BEFORE_KEY = new KeyCodeCombination(KeyCode.B, KeyCombination.ControlDown, KeyCombination.ShiftDown)
    val CLOSE_TAB_AFTER_KEY = new KeyCodeCombination(KeyCode.F, KeyCombination.ControlDown, KeyCombination.ShiftDown)
    val CHECK_ALL_KEY = new KeyCodeCombination(KeyCode.A, KeyCombination.ControlDown, KeyCombination.ShiftDown)
    val CHECK_NONE_KEY = new KeyCodeCombination(KeyCode.N, KeyCombination.ControlDown, KeyCombination.ShiftDown)
    val ROW_DETAILS_KEY = new KeyCodeCombination(KeyCode.R, KeyCombination.ControlDown)

    private def button(text: String, code: KeyCodeCombination, icon: String, ev : ActionEvent => Unit) : Button =
      new Button(text) {
        onAction = ev
        stylesheets += "flatButton.css"
        tooltip = code.displayText
        graphic = new ImageView(JFXUtil.loadIcon(s"${icon}.png"))
      }

    private def button(text: String, icon: String, ev : ActionEvent => Unit) : Button =
      new Button(text) {
          stylesheets += "flatButton.css"
          onAction = ev
          graphic = new ImageView(JFXUtil.loadIcon(s"${icon}.png"))
      }

    def buildButtonBar(guiActor: ActorRef, queryId : QueryId, localization : Localization): HBox = new HBox() {
      children = List(
        button(localization.closeThisTab, CLOSE_THIS_TAB, "deleteThis", (_: ActionEvent) => guiActor ! RequestRemovalThisTab(queryId)),
        button(localization.closeTabsBeforeThis, CLOSE_TAB_BEFORE_KEY, "deleteBefore", (_: ActionEvent) => guiActor ! RequestRemovalTabsBefore(queryId)),
        button(localization.closeTabsAfterThis, CLOSE_TAB_AFTER_KEY, "deleteAfter", (_: ActionEvent) => guiActor ! RequestRemovalTabsAfter(queryId)),
        button(localization.closeAllTabs, "deleteAll", (_: ActionEvent) => guiActor ! RequestRemovalAllTabs(queryId.tableId.databaseId)),
        button(localization.checkAll, CHECK_ALL_KEY, "checkAll", (_: ActionEvent) => guiActor ! CheckAllTableRows(queryId)),
        button(localization.uncheckAll, CHECK_NONE_KEY, "checkNone", (_: ActionEvent) => guiActor ! CheckNoTableRows(queryId)),
        button(localization.rowDetails, ROW_DETAILS_KEY, "details", (_: ActionEvent) => guiActor ! SwitchRowDetails(queryId)),
        button(localization.refresh, ROW_DETAILS_KEY, "refresh", (ev: ActionEvent) => guiActor ! ReloadQuery(queryId, false))
      )
      spacing = 5
    }
    /* to get the tableid is an expensive operation, therefore we use it as a closure */
    def handleKeyCombination(guiActor: ActorRef, ev: KeyEvent, tableId : () => Option[QueryId]) : Unit =
        if(ev.controlDown) {
            if(CLOSE_THIS_TAB.`match`(ev)) tableId().foreach(id => guiActor ! RequestRemovalThisTab(id))
            else if(CLOSE_TAB_BEFORE_KEY.`match`(ev)) tableId().foreach(id => guiActor ! RequestRemovalTabsBefore(id))
            else if(CLOSE_TAB_AFTER_KEY.`match`(ev)) tableId().foreach(id => guiActor ! RequestRemovalTabsAfter(id))
            else if(CHECK_ALL_KEY.`match`(ev)) tableId().foreach(id => guiActor ! CheckAllTableRows(id))
            else if(CHECK_NONE_KEY.`match`(ev)) tableId().foreach(id => guiActor ! CheckNoTableRows(id))
            else if(ROW_DETAILS_KEY.`match`(ev)) tableId().foreach(id => guiActor ! SwitchRowDetails(id))
        }
}
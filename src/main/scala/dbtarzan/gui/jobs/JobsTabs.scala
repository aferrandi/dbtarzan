package dbtarzan.gui.jobs;

import dbtarzan.gui.interfaces.TControlBuilder
import dbtarzan.db.*
import dbtarzan.db.foreignkeys.ForeignKeyMapper
import dbtarzan.gui.BrowsingTable
import dbtarzan.gui.database.Job
import dbtarzan.gui.interfaces.TControlBuilder
import dbtarzan.gui.tabletabs.{TTableWithTab, TableStructureText, TableTabsMap, TabsToClose}
import dbtarzan.localization.Localization
import dbtarzan.log.actor.Logger
import dbtarzan.messages.{ QueryId, TWithTableId, TWithQueryId, TWithJobId, ResponseCloseTables }
import jdk.javadoc.internal.doclets.formats.html.markup.HtmlStyles.tableTabs
import org.apache.pekko.actor.ActorRef
import scalafx.scene.control.{Tab, TabPane, Tooltip, Label}
import scalafx.scene.Parent
import scalafx.geometry.Side
import dbtarzan.messages.RequestRemovalAllTabs
import scalafx.event.Event
import scalafx.Includes.*

class JobsTabs() extends TControlBuilder {
    private val tabs = new TabPane {
        side = Side.Left
        rotateGraphic = false
        visible = false
    }
    tabs += spacingTab()

    private def spacingTab() = new Tab {
        closable = false
        style = "-fx-pref-width: 30px; -fx-background-color: transparent;"
    }

    def addTab(tab: Tab): Unit = {
        tabs.visible = true
        tabs += tab
        tabs.selectionModel().select(tab)
    }

    def removeTab(tab: Tab) : Unit =
        tabs.tabs -= tab

    def currentTab(): Tab = tabs.selectionModel().selectedItem()

    def control : Parent = tabs

}




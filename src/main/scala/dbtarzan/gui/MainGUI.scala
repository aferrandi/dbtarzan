package dbtarzan.gui

import akka.actor.ActorRef
import dbtarzan.config.password.{EncryptionKey, VerificationKey}
import dbtarzan.db.DatabaseId
import dbtarzan.gui.browsingtable.TableMenu
import dbtarzan.gui.util.JFXUtil
import dbtarzan.localization.Localization
import dbtarzan.types.ConfigPath
import scalafx.Includes._
import scalafx.application.JFXApp.PrimaryStage
import scalafx.geometry.Orientation
import scalafx.scene.Scene
import scalafx.scene.control.SplitPane
import scalafx.scene.image.Image
import scalafx.scene.input.KeyEvent
import scalafx.scene.layout.BorderPane
import scalafx.stage.Screen

/* the main GUI of dbtarzan. database list on the left, menu on the top, the rest in the middle.
	the actors are still not been created when calling the constructor, therefore they are passed as functions.
 */
class MainGUI(
	configPaths: ConfigPath, 
	localization: Localization,
	verificationKey: Option[VerificationKey],
	version: String,
	closeApp : () => Unit)
{
  case class PostInitData(guiActor: ActorRef, connectionsActor: ActorRef)

  private var postInitData: Option[PostInitData] = None
	/* the database tabs on the middle-right side */
	val databaseTabs = new DatabaseTabs(localization)
	/* the log/error list on the bottom */
	val logList = new LogList(localization)
	/* the database/connection list on the left side */
	val databaseList = new DatabaseList(localization)

  val global = new Global()

	/* how big is the screen */
	private val screenBounds = Screen.primary.visualBounds
	/* the gui */
  private val encryptionKeyExtractor = new EncryptionKeyExtractor(verificationKey,  localization)

  private val mainGUIMenu = new MainGUIMenu(configPaths, localization, encryptionKeyExtractor, global)

  private val stage = buildStage()

	stage.scene().onKeyReleased = (ev: KeyEvent) => { handleShortcut(ev) }

  def postInit(guiActor: ActorRef, connectionsActor: ActorRef) : Unit = {
		this.postInitData = Some(PostInitData(guiActor, connectionsActor))

		databaseTabs.postInit(guiActor, connectionsActor)
    mainGUIMenu.postInit(stage, guiActor, connectionsActor)
  } 

	private def withExtractedEncryptionKey(use : EncryptionKey => Unit) : Unit = 
		encryptionKeyExtractor.extractEncryptionKey(stage).foreach(use)

	def onDatabaseSelected(use : (DatabaseId, EncryptionKey) => Unit) : Unit = 
		databaseList.onDatabaseSelected(databaseId => {
      if(!databaseTabs.showDatabase(databaseId))
        withExtractedEncryptionKey(encryptionKey => use(databaseId, encryptionKey))
      }
    )

	def onForeignKeyToFile(use : (DatabaseId, EncryptionKey) => Unit) : Unit =
		databaseList.onForeignKeyToFile(databaseId => withExtractedEncryptionKey(encryptionKey => use(databaseId, encryptionKey)))

	private def buildStage() : PrimaryStage = new PrimaryStage {
	    title = "DbTarzan "+version
	    icons.add(appIcon())
	    scene = new Scene(screenBounds.width / 2, screenBounds.height / 2 ) {
	        root = buildMainView()
	        onCloseRequest = handle { 
	        	databaseTabs.sendCloseToAllOpen()
	        	closeApp() 
	        }
		  }
	}

	private def handleShortcut(ev : KeyEvent) : Unit = 
		postInitData match {
			case Some(pa) => TableMenu.handleKeyCombination(pa.guiActor, ev, () => databaseTabs.currentTableId)
			case None => println("MainGUI: guiActor not defined")
		}

	private def buildMainView() = new BorderPane {
		top = mainGUIMenu.buildMenu()
		center = mainSplitPane()
	}

  private def buildDatabaseSplitPane() = new SplitPane {
    val databaseListWithTitle: BorderPane = JFXUtil.withTitle(databaseList.control, localization.databases)
    items.addAll(databaseListWithTitle, databaseTabs.control)
		dividerPositions = 0.2
		SplitPane.setResizableWithParent(databaseListWithTitle, value = false)
	}

	private def mainSplitPane() = new SplitPane {
		orientation() =  Orientation.Vertical
		items.addAll(buildDatabaseSplitPane(), logList.control)
		dividerPositions = 0.85
		SplitPane.setResizableWithParent(logList.control, value = false)
	}
  
	private def appIcon() = new Image(getClass.getResourceAsStream("monkey-face-cartoon.png"))
}
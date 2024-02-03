package dbtarzan.gui

import dbtarzan.config.password.{EncryptionKey, VerificationKey}
import dbtarzan.db.{DatabaseId, DatabaseInfo, LoginPasswords, SimpleDatabaseId}
import dbtarzan.gui.browsingtable.TableButtonBar
import dbtarzan.gui.log.LogList
import dbtarzan.gui.login.PasswordDialog
import dbtarzan.gui.util.JFXUtil
import dbtarzan.localization.Localization
import dbtarzan.log.actor.Logger
import dbtarzan.messages.DatabaseIdUtil
import dbtarzan.types.ConfigPath

import org.apache.pekko.actor.ActorRef
import scalafx.Includes.*
import scalafx.application.JFXApp3
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
  version: String)
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

  private var closeApp: Option[() => Unit] = None

  stage.scene().onKeyReleased = (ev: KeyEvent) => { handleShortcut(ev) }

  def postInit(guiActor: ActorRef, connectionsActor: ActorRef, log: Logger) : Unit = {
    this.postInitData = Some(PostInitData(guiActor, connectionsActor))

    databaseTabs.postInit(guiActor, connectionsActor, log)
    mainGUIMenu.postInit(stage, guiActor, connectionsActor, log)
  } 

  private def withExtractedEncryptionKey(use : EncryptionKey => Unit) : Unit =
    encryptionKeyExtractor.extractEncryptionKey(stage).foreach(use)

  def onDatabaseSelected(use : (DatabaseInfo, EncryptionKey, LoginPasswords) => Unit) : Unit =
    databaseList.onDatabaseSelected(databaseInfo => {
      if(!databaseTabs.showDatabase(DatabaseIdUtil.databaseIdFromInfo(databaseInfo))) {
        loginPasswordsFromDialogIfNeeded(databaseInfo).foreach(loginPasswords =>
          withExtractedEncryptionKey(encryptionKey => use(databaseInfo, encryptionKey, loginPasswords))
        )
      }}
    )



  private def loginPasswordsFromDialogIfNeeded(databaseInfo: DatabaseInfo): Option[LoginPasswords] = {
    val databasesThatNeedPasswords = DatabaseIdUtil.extractSimpleDatabasesThatNeedLoginPassword(databaseInfo)
    if (databasesThatNeedPasswords.nonEmpty)
      PasswordDialog.show(localization, databasesThatNeedPasswords)
    else
      Some(LoginPasswords(Map.empty))
  }
  def onForeignKeyToFile(use : (DatabaseInfo, EncryptionKey, LoginPasswords) => Unit) : Unit =
    databaseList.onForeignKeyToFile(databaseInfo =>
      loginPasswordsFromDialogIfNeeded(databaseInfo).foreach(loginPasswords =>
        withExtractedEncryptionKey(encryptionKey => use(databaseInfo, encryptionKey, loginPasswords))
      )
    )

  private def buildStage() : JFXApp3.PrimaryStage = new JFXApp3.PrimaryStage {
      title = "DbTarzan "+version
      icons.add(appIcon())
      scene = new Scene(screenBounds.width / 2, screenBounds.height / 2 ) {
          root = buildMainView()
          onCloseRequest = _ => {
            databaseTabs.sendCloseToAllOpen()
            closeApp.foreach(_.apply())
          }
      }
  }

  private def handleShortcut(ev : KeyEvent) : Unit =
    postInitData match {
      case Some(pa) => TableButtonBar.handleKeyCombination(pa.guiActor, ev, () => databaseTabs.currentTableId)
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

  def onCloseApp(closeApp: () => Unit ): Unit = { this.closeApp = Some(closeApp) }


  private def appIcon() = new Image(getClass.getResourceAsStream("monkey-face-cartoon.png"))
}
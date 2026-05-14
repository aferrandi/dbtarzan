package dbtarzan.gui.config.global

import scalafx.stage.{Stage, StageStyle, WindowEvent}
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.Scene
import scalafx.Includes.*
import org.apache.pekko.actor.ActorRef
import scalafx.scene.layout.Region
import dbtarzan.config.connections.{ConnectionDataPasswordChanger, EncryptionKeyChange}
import dbtarzan.config.global.{GlobalData, GlobalDataReader, GlobalDataWriter}
import dbtarzan.localization.Localization
import dbtarzan.types.ConfigPath
import dbtarzan.log.actor.Logger
import scalafx.scene.control.PopupControl.UsePrefSize

/* to start the connection editor. It handles all the cancel/closing/save events */
object GlobalEditorStarter
{
    def openGlobalEditor(parentStage : Stage, configPaths: ConfigPath, localization : Localization, guiActor : ActorRef, log: Logger) : Unit = {
        val globalStage = new Stage {
            title = localization.editGlobalSettings
            width = 500
            height = 300
            scene = new Scene {
                private val originalData = GlobalDataReader.read(configPaths.globalConfigPath)
                
                def onSave(dataToSave: GlobalData, change : EncryptionKeyChange) : Unit = {
                    if(dataToSave.encryptionData != originalData.encryptionData)
                        new ConnectionDataPasswordChanger(change).updateDatas(configPaths.connectionsConfigPath)
                    GlobalDataWriter.write(configPaths.globalConfigPath, dataToSave)
                    new Alert(AlertType.Information) { 
                        headerText= localization.editGlobalSettings
                        contentText= localization.globalChangesAfterRestart
                        dialogPane().minHeight_=(UsePrefSize)
                    }.showAndWait()
                    window().hide()
                }

                def onCancel() : Unit = 
                    window().hide()

                val editor = new GlobalEditor(originalData, localization, guiActor, log)
                editor.onSave((dataToSave, change) => onSave(dataToSave, change))
                editor.onCancel(() => onCancel())
                onCloseRequest = (event : WindowEvent) => { 
                    event.consume()
                    editor.cancelIfPossible(() => onCancel()) 
                }
                root = editor.control
            }
        }
        globalStage.initOwner(parentStage)    
        globalStage.initStyle(StageStyle.Utility)
        globalStage.show()
    }
}
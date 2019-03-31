package dbtarzan.gui.config.global

import scalafx.stage.{ Stage, StageStyle, WindowEvent }
import scalafx.scene.Scene
import scalafx.Includes._
import java.nio.file.Path

import dbtarzan.config.global.{ GlobalDataReader, GlobalDataWriter, GlobalData }
import dbtarzan.localization.Localization

/* to start the connection editor. It handles all the cancel/closing/save events */
object GlobalEditorStarter
{
    private def rebuildConnectionPasswords(change : EncryptionKeyChange) : Unit = {

    }

    def openGlobalEditor(parentStage : Stage, configPath: Path, localization : Localization) : Unit = {
        println("open global editor")  
        val globalStage = new Stage {
            title = localization.editGlobalSettings
            width = 500
            height = 300
            scene = new Scene {
                val originalData = GlobalDataReader.read(configPath)
                def onSave(dataToSave: GlobalData, change : EncryptionKeyChange) : Unit = {
                    if(dataToSave.verificationKey != originalData.verificationKey)
                        rebuildConnectionPasswords(change)
                    GlobalDataWriter.write(configPath, dataToSave)
                    window().hide()
                }

                def onCancel() : Unit = 
                    window().hide()

                val editor = new GlobalEditor(originalData, localization)
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
        globalStage.initStyle(StageStyle.UTILITY)
        globalStage.show()
    }
}
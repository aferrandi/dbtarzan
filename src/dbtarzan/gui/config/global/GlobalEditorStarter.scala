package dbtarzan.gui.config.global

import scalafx.stage.{ Stage, StageStyle, WindowEvent }
import scalafx.scene.Scene
import scalafx.Includes._
import dbtarzan.config.global.{ GlobalDataReader, GlobalDataWriter, GlobalData }
import java.nio.file.Path

/* to start the connection editor. It handles all the cancel/closing/save events */
object GlobalEditorStarter
{
    def openGlobalEditor(parentStage : Stage, configPath: Path) : Unit = {
        println("open global editor")  
        val globalStage = new Stage {
            title = "Edit Global Settings"
            width = 800
            height = 600
            scene = new Scene {
                def onSave(dataToSave: GlobalData) : Unit = {
                    GlobalDataWriter.write(configPath, dataToSave)
                    window().hide()
                }

                def onCancel() : Unit = 
                window().hide()

                val editor = new GlobalEditor(GlobalDataReader.read(configPath))
                editor.onSave(onSave(_))
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
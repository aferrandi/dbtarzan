package dbtarzan.gui.config

import scalafx.stage.{ Stage, StageStyle, WindowEvent }
import scalafx.scene.Scene
import scalafx.Includes._
import dbtarzan.config.{ ConfigReader, ConfigWriter }

/* to start the connection editor. It handles all the cancel/closing/save events */
object ConnectionEditorStarter
{
 def openConnectionsEditor(parentStage : Stage) : Unit = {
    println("open connections editor")  
     val connectionStage = new Stage {
      title = "Edit Connections"
      width = 800
      height = 600
      scene = new Scene {
        val editor = new ConnectionEditor(ConfigReader.read("connections.config"))
        editor.onSave(connectionsToSave => {
            ConfigWriter.write("connections.config", connectionsToSave)
            window().hide()
          })
        editor.onCancel(() => {
            window().hide()
          })
        onCloseRequest = (event : WindowEvent) => { 
          event.consume()
          editor.cancelIfPossible(() => {
            window().hide()
          }) 
          }
        root = editor.control
      }
    }
    connectionStage.initOwner(parentStage)    
    connectionStage.initStyle(StageStyle.UTILITY)
    connectionStage.show()
  }

}
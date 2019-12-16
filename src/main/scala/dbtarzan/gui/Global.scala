package dbtarzan.gui
import dbtarzan.gui.config.connections.ConnectionEditor
import dbtarzan.messages.ResponseTestConnection

class Global extends TGlobal {
  private var connectionEditor: Option[ConnectionEditor] = None

  def setConnectionEditor(connectionEditor: ConnectionEditor) : Unit =
    this.connectionEditor = Some(connectionEditor)

  override def handleTestConnectionResponse(rsp: ResponseTestConnection): Unit =
    connectionEditor.foreach(e => e.testConnectionResult(rsp))
}

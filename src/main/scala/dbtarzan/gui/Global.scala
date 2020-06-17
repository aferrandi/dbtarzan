package dbtarzan.gui
import dbtarzan.gui.config.connections.ConnectionEditor
import dbtarzan.messages.{ResponseSchemaExtraction, ResponseTestConnection}

class Global extends TGlobal {
  private var connectionEditor: Option[ConnectionEditor] = None

  def setConnectionEditor(connectionEditor: ConnectionEditor) : Unit =
    this.connectionEditor = Some(connectionEditor)

  override def handleTestConnectionResponse(rsp: ResponseTestConnection): Unit =
    connectionEditor.foreach(e => e.testConnectionResult(rsp))

  override def handleSchemaExtractionResponse(rsp: ResponseSchemaExtraction): Unit =
    connectionEditor.foreach(e => e.schemaExtractionResult(rsp))

}

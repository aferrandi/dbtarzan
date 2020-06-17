package dbtarzan.gui

import dbtarzan.messages.{ResponseSchemaExtraction, ResponseTestConnection}

trait TGlobal {
  def handleSchemaExtractionResponse(rsp: ResponseSchemaExtraction): Unit

  def handleTestConnectionResponse(rsp: ResponseTestConnection) : Unit
}

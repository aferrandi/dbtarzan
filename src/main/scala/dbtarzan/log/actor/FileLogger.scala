package dbtarzan.log.actor

import dbtarzan.messages.{Debug, Error, ExceptionText, Info, TLogMessage, Warning}

import java.io.FileWriter

class FileLogger {
  val fw = new FileWriter("dbtarzan.log")

  def log(msg: TLogMessage): Unit = {
    fw.append(s"${toText(msg)}\n")
    fw.flush()
  }

  def toText(msg: TLogMessage): String = msg match {
    case Error(produced, text, Some(ex)) => s"ERROR ${produced} ${text}:${ExceptionText.extractMessageText(ex)}"
    case Error(produced, text, None) => s"ERROR ${produced} ${text}"
    case Warning(produced, text) => s"WARNING ${produced} ${text}"
    case Info(produced, text) => s"INFO ${produced} ${text}"
    case Debug(produced, text) => s"DEBUG ${produced} ${text}"
  }
}

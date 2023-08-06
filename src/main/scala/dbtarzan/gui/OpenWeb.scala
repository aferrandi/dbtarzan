package dbtarzan.gui

import dbtarzan.gui.Main.hostServices

object OpenWeb {
  def openWeb(url: String): Unit = openWebTry1(url)

  private def openWebTry1(url: String): Unit = try {
    val p = new ProcessBuilder("xdg-open", url).start()
    if (!p.isAlive)
      openWebTry2(url)
  }
  catch {
    case _: Throwable => openWebTry2(url)
  }

  private def openWebTry2(url: String): Unit =
    hostServices.showDocument(url)
}

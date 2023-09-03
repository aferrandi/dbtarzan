package dbtarzan.gui.orderby

import dbtarzan.db.OrderByDirection
import dbtarzan.gui.util.JFXUtil
import scalafx.scene.image.Image

object UpDownIcons {
  val upIcon: Image = JFXUtil.loadIcon("up.png")
  val downIcon: Image = JFXUtil.loadIcon("down.png")

  def iconFromDirection(direction: OrderByDirection): Image =
    direction match {
      case OrderByDirection.ASC => upIcon
      case OrderByDirection.DESC => downIcon
    }
}
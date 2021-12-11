package dbtarzan.gui.table.headings

import dbtarzan.gui.util.JFXUtil
import scalafx.scene.image.Image

import scala.collection.immutable.BitSet

object TableColumnsIcons {
  val PRIMARYKEY_ICON: Image = JFXUtil.loadIcon("primaryKey.png")
  val FOREIGNKEY_ICON: Image = JFXUtil.loadIcon("foreignKey.png")
  val BOTHKEYS_ICON: Image = JFXUtil.loadIcon("bothKeys.png")

  def bitsetToIcon(attributes: BitSet) : Option[Image] = {
    toIcon(attributes.sum)
  }

  private def toIcon(total : Int) : Option[Image] = total match {
    case TableColumnsStates.PRIMARYKEY_STATE => Some(TableColumnsIcons.PRIMARYKEY_ICON)
    case TableColumnsStates.FOREIGNKEY_STATE => Some(TableColumnsIcons.FOREIGNKEY_ICON)
    case TableColumnsStates.BOTHKEYS_STATE => Some(TableColumnsIcons.BOTHKEYS_ICON)
    case _ => None
  }
}

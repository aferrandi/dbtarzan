package dbtarzan.gui.util

import scalafx.beans.property.{ObjectProperty, StringProperty}
import scalafx.scene.control.{TableCell, TableColumn}
import scalafx.scene.image.{Image, ImageView}
import scalafx.Includes.*

object TableUtil {
  def buildTextTableColumn[T](title: String, extraText: TableColumn.CellDataFeatures[T, String] => String): TableColumn[T, String] = {
    new TableColumn[T, String] {
      text = title
      cellValueFactory = { x => new StringProperty(extraText(x)) }
      resizable = true
    }
  }

  def buildImageTableColumn[T](buildImage: TableColumn.CellDataFeatures[T, Image] => Image): TableColumn[T, Image] = {
    new TableColumn[T, Image] {
      cellValueFactory = { row => ObjectProperty(buildImage(row).delegate) }
      cellFactory = {
        (_: TableColumn[T, Image]) =>
          new TableCell[T, Image] {
            item.onChange {
              (_, _, newImage) => graphic = new ImageView(newImage)
            }
          }
      }
      maxWidth = 24
      minWidth = 24
    }
  }
}

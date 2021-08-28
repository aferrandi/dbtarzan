package dbtarzan.gui.util;

import scalafx.geometry.Side
import scalafx.scene.control.{ContextMenu, TextField}

import scala.collection.immutable.TreeSet

class AutoComplete(suggestions: List[String], maxSuggestions: Int) extends TextField {
  private val suggestionsSet =  TreeSet[String](suggestions.map(_.toLowerCase()): _*)
  private val sugestionsPopup = new ContextMenu()

  text.onChange { (_, _, newText) => showSuggestions(newText) }

  private def showSuggestions(newText: String): Unit = {
    if (newText.nonEmpty)
      showSuggestionsForText(newText)
    else
      sugestionsPopup.hide()
  }

  private def showSuggestionsForText(newText: String): Unit = {
    val (last, before) = lastWordAndBefore(newText)
    if(last.nonEmpty && Character.isAlphabetic(last.charAt(0))) {
      val lastLower = last.toLowerCase()
      val searchResult = suggestionsSet.range(lastLower, lastLower + Character.MAX_VALUE)
      if (searchResult.nonEmpty) {
        fillSuggestions(before, searchResult)
        if (!sugestionsPopup.isShowing) {
          val len = newText.length - 1
          val xInTextField = caretPosition(len)
          sugestionsPopup.show(AutoComplete.this, Side.Bottom, xInTextField, 0)
        }
      } else
        sugestionsPopup.hide()
    }
  }

  private def caretPosition(len: Int): Double =
    AutoComplete.super.skin.value.asInstanceOf[javafx.scene.control.skin.TextFieldSkin].getCharacterBounds(len).getMaxX

  private def lastWordAndBefore(newText: String): (String, String) = {
    val lastSpace = newText.lastIndexOf(' ')
    if(lastSpace >= 0)
      (newText.substring(lastSpace + 1), newText.substring(0, lastSpace +1))
    else
      (newText, "")
  }

  private def fillSuggestions(before: String, searchResult: Set[String]): Unit = {
      val count = Math.min(searchResult.size, maxSuggestions)
      val menuItems = searchResult.take(count).map(result =>
          JFXUtil.menuItem(result, () => {
            text = before + result
            this.positionCaret(text().length)
            sugestionsPopup.hide()
          })
      )
      JFXUtil.bufferSet(sugestionsPopup.items, menuItems.map(_.delegate))
  }
}
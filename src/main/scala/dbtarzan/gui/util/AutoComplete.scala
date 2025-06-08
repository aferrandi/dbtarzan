package dbtarzan.gui.util;

import scalafx.geometry.Side
import scalafx.scene.control.{ContextMenu, MenuItem, TextField}

import scala.collection.immutable.TreeSet

class AutoComplete(suggestions: List[String], maxSuggestions: Int) extends TextField {
  private val suggestionsSet =  TreeSet[String](suggestions.map(_.toLowerCase())*)
  private val suggestionsPopup = new ContextMenu()

  text.onChange { (_, _, newText) => showSuggestions(newText) }

  private def showSuggestions(newText: String): Unit = {
    if (newText.nonEmpty)
      showSuggestionsForText(newText)
    else
      suggestionsPopup.hide()
  }

  private def showSuggestionsForText(newText: String): Unit = {
    val (last, before) = lastWordAndBefore(newText)
    if(last.nonEmpty && Character.isAlphabetic(last.charAt(0))) {
      val lastLower = last.toLowerCase()
      val searchResult = suggestionsSet.range(lastLower, lastLower + Character.MAX_VALUE)
      if (searchResult.nonEmpty) {
        fillSuggestions(before, searchResult)
        if (!suggestionsPopup.isShowing)
          showSuggestionsPopupWithRightSize(newText)
      } else
        suggestionsPopup.hide()
    }
  }

  private def showSuggestionsPopupWithRightSize(newText: String): Unit = {
    val len = newText.length - 1
    val xInTextField = caretPosition(len)
    suggestionsPopup.show(AutoComplete.this, Side.Bottom, xInTextField, 0)
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
          singleSuggestionWordMenuItem(before, result)
      )
      JFXUtil.bufferSet(suggestionsPopup.items, menuItems.map(_.delegate))
  }

  private def singleSuggestionWordMenuItem(before: String, resultWord: String): MenuItem = {
    val suggestionOneWord = JFXUtil.menuItem(resultWord, () => {
      appendWordToTextBox(before, resultWord)
      suggestionsPopup.hide()
    })
    suggestionOneWord.mnemonicParsing = false
    suggestionOneWord
  }

  private def appendWordToTextBox(before: String, resultWord: String): Unit = {
    text = before + resultWord
    this.positionCaret(text().length)
  }
}
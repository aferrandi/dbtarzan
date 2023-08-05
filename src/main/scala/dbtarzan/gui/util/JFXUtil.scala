  package dbtarzan.gui.util

  import scalafx.scene.Node
  import scalafx.scene.control.{Alert, ButtonType, Label, ListView, MenuItem, TableView, TextField}
  import scalafx.scene.layout.BorderPane
  import scalafx.scene.image.Image
  import scalafx.scene.text.Text
  import scalafx.geometry.Insets
  import scalafx.scene.input.{Clipboard, ClipboardContent, KeyCode, KeyEvent, MouseButton, MouseEvent}
  import scalafx.Includes._
  import scalafx.event.ActionEvent
  import scalafx.scene.control.Alert.AlertType
  import scalafx.collections.ObservableBuffer
  import scalafx.scene.layout.Region

  object JFXUtil {
    def threeLines : String = "\u2630"

    def withTitle(graphic : Node, title : String): BorderPane = new BorderPane {
        top = buildTitle(title)
        center = graphic
    }

    def withLeftTitle(graphic : Node, title : String): BorderPane = new BorderPane {
        left = buildTitle(title)
        center = graphic
    }

    def buildTitle(title : String): Label = new Label(title) {
      margin = Insets(5)
    }

    private def focusedItem[T](list : ListView[T]) = list.focusModel().focusedItem()

    def onAction[T](list : ListView[T] , action : (T, Boolean) => Unit): Unit = {
      list.onMouseClicked = (ev: MouseEvent) =>  if(ev.clickCount == 2 && ev.button == MouseButton.Primary)
        action(focusedItem(list), ev.controlDown)
      list.onKeyPressed = (ev: KeyEvent) => if(ev.code == KeyCode.Enter)
        action(focusedItem(list), ev.controlDown)
    }

    private def focusedItem[T](table : TableView[T]) = table.focusModel().focusedItem()

    def onAction[T](table : TableView[T] , action : (T, Boolean) => Unit): Unit = {
      table.onMouseClicked = (ev: MouseEvent) =>  if(ev.clickCount == 2 && ev.button == MouseButton.Primary)
        action(focusedItem(table), ev.controlDown)
      table.onKeyPressed = (ev: KeyEvent) => if(ev.code == KeyCode.Enter)
        action(focusedItem(table), ev.controlDown)
    }

    def onContextMenu[T](menu : MenuItem, list : ListView[T] , action : T => Unit): Unit =
      menu.onAction = (_: ActionEvent) => action(focusedItem(list))

    def copyTextToClipboard(text : String) : Unit = {
      val content = new ClipboardContent()
      content.putString(text)
      Clipboard.systemClipboard.setContent(content)
    }

    def swapListBuffer[T](lb : ObservableBuffer[T], i : Int, j : Int) : Unit =	{
      val vi = lb(i)
      val vj = lb(j)
      lb.update(i, vj)
      lb.update(j, vi)
    }

    def bufferSet[T](b : ObservableBuffer[T], l: Iterable[T]) : ObservableBuffer[T] = {
      b.clear()
      b ++= l
    }

    def areYouSure(text : String, header: String): Boolean = new Alert(AlertType.Confirmation, text, ButtonType.Yes, ButtonType.No ) {
      headerText = header
    }.showAndWait() match {
      case Some(ButtonType.Yes) => true
      case _ => false
    }

    def showInfoAlert(header : String, info : String) : Unit =
      new Alert(AlertType.Information) {
        headerText= header
        contentText= info
      }.showAndWait()

    def showErrorAlert(header : String, error : String) : Unit =
      new Alert(AlertType.Error) {
        headerText= header
        contentText= error
        // show long text without truncating it
        dialogPane().minHeight_=(Region.USE_PREF_SIZE)
      }.showAndWait()


    def loadIcon(fileName: String) : Image =
      // println(this.getClass().getResource("").getPath())
        new Image(getClass.getResourceAsStream(fileName))


    def averageCharacterSize() : Double = {
      val s = "XXXXX"
      val text = new Text(s)
      text.getBoundsInLocal.getWidth / s.length;
    }

    def menuItem(text: String, action:() => Unit): MenuItem = new MenuItem(text) {
      this.onAction = {
        (_: ActionEvent) => { action()	}
      }
    }

    class NumTextField extends TextField {
      text = ""
      /* only digits allowed (or empty string) */
      text.onChange { (_, oldValue, newValue) => {
        if (!StringUtil.isAllDigits(newValue))
          text = oldValue
      }}
      def toOptInt: Option[Int] = StringUtil.emptyToNone(text()).map(_.toInt) // it can only be None or Int
      def fromOptInt(optInt: Option[Int]): Unit  = {
        text = StringUtil.noneToEmpty(optInt.map(_.toString))
      }
    }

    def numTextField(): NumTextField = new NumTextField()

      /* invisible controls don't use space in the layout */
    def changeControlsVisibility(visible : Boolean, nodes: javafx.scene.Node*): Unit = {
      nodes.foreach(node => {
        node.visible = visible
        node.managed = visible
      })
    }
   }
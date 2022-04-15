package UI.MenuBar

import UI.tabs.CustomTabs
import UI.{MainStage, UIStyle}
import logger.Logger
import scalafx.scene.control.Label
import scalafx.scene.layout.HBox
import scalafx.stage.Stage

object CustomMenuBar {


  def openMenu(menuName: String) = {
    MainStage.getCenterOfStage()
    menuName match {
      case "File" => FileMenu.spawnMenu()
      case _ =>
    }

  }


  def createMenu(menuName: String): HBox = {
    val box = new HBox()
    box.setStyle(UIStyle.customMenuUnselected)
    val label = new Label(menuName)
    label.setStyle(UIStyle.menuLabelStyle)
    //    val spacer = new HBox()
    //    //    spacer.setStyle(UIStyle.transparentObject)
    //    spacer.hgrow = Priority.Always
    //    box.hgrow = Priority.Always

    box.getChildren.addAll(label)

    box.onMouseClicked = (event => {
      openMenu(menuName)
    })

    box.onMouseEntered = event => {
      box.setStyle(UIStyle.customMenuHover)
      box.getChildren.get(0).asInstanceOf[javafx.scene.control.Label].setStyle(UIStyle.menuLabelStyleSelected)
    }
    box.onMouseExited = event => {
      box.setStyle(UIStyle.customMenuUnselected)
      box.getChildren.get(0).asInstanceOf[javafx.scene.control.Label].setStyle(UIStyle.menuLabelStyle)
    }

    box
  }


  val parent = new HBox()
  parent.setStyle(UIStyle.smallBottomBoarderNoPadding)

  val menus = Seq(
    createMenu("File"),
    createMenu("Logs"),
    createMenu("Combat Instances")
  )

  menus.foreach(menu => parent.getChildren.add(menu))

  def addToUI = parent


}

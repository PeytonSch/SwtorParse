package UI.objects

import UI.MainStage.mainStage
import UI.{MainStage, UIStyle}
import UI.tabs.{CustomTabs, Overview}
import logger.Logger
import scalafx.scene.control.Label
import scalafx.scene.layout.{HBox, Priority}
import scalafx.stage.{Stage, StageStyle}

object CustomMenuBar {

  val fileMenuStage = new Stage()
//  fileMenuStage.initStyle(StageStyle.Undecorated)
  fileMenuStage.setAlwaysOnTop(true)
  fileMenuStage.width = 600
  fileMenuStage.height = 600
  MainStage.getCenterOfStage()
//  fileMenuStage.setX(Overview.addToUI().getLayoutX)

  def openMenu(menuName: String) = {
    MainStage.getCenterOfStage()
    menuName match {
      case "File" => {
        // spawn the stage in the center of the screen
        val centerOfStage = MainStage.getCenterOfStage()
        val pos = (centerOfStage._1 - (fileMenuStage.getWidth / 2.0),centerOfStage._2 - (fileMenuStage.getHeight /2.0))
        Logger.highlight(s"${pos._1}, ${pos._2}")
        fileMenuStage.setX(pos._1)
        fileMenuStage.setY(pos._2)
        CustomTabs.addToUI.setOpacity(.20)
        fileMenuStage.show

    }
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

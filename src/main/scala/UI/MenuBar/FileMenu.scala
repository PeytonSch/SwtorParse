package UI.MenuBar

import UI.{MainStage, UIStyle}
import UI.tabs.CustomTabs
import logger.Logger
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.layout.VBox
import scalafx.stage.{Stage, StageStyle}

object FileMenu extends MenuItem {

  private val fileMenuStage = new Stage()
  fileMenuStage.initStyle(StageStyle.Undecorated)
  fileMenuStage.setAlwaysOnTop(true)
  fileMenuStage.width = 600
  fileMenuStage.height = 600
  val fileMenu = new VBox()
  fileMenu.setStyle(UIStyle.mainBackgroundObject)
  private val fileMenuScene = new Scene(fileMenu)
  fileMenuStage.setScene(fileMenuScene)

  private val closeButton = new Button {
    text = "Close"
    style = UIStyle.uiButtonStyle
  }

  closeButton.setOnAction(event => {
    closeMenu()
  })

  fileMenu.getChildren.addAll(closeButton)

  UIStyle.setHoverable(closeButton,UIStyle.uiButtonHoverStyle)




  override def spawnMenu(): Unit = {
    // spawn the stage in the center of the screen
    val centerOfStage = MainStage.getCenterOfStage()
    val pos = (centerOfStage._1 - (fileMenuStage.getWidth / 2.0), centerOfStage._2 - (fileMenuStage.getHeight / 2.0))
//    Logger.highlight(s"${pos._1}, ${pos._2}")
    fileMenuStage.setX(pos._1)
    fileMenuStage.setY(pos._2)
    CustomTabs.addToUI.setOpacity(.20)
    fileMenuStage.show
  }

  override def closeMenu(): Unit = {
    CustomTabs.addToUI.setOpacity(1)
    fileMenuStage.hide()
  }

}

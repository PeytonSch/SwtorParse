package UI.MenuBar

import UI.{MainStage, UIStyle}
import UI.tabs.CustomTabs
import logger.Logger
import scalafx.geometry.Pos
import scalafx.scene.Scene
import scalafx.scene.control.Label
import scalafx.scene.layout.VBox
import scalafx.scene.paint.Color
import scalafx.stage.{Stage, StageStyle}

object LoadingScreen {

  private val loadingScreenStage = new Stage()
  loadingScreenStage.initStyle(StageStyle.Transparent)
  loadingScreenStage.setAlwaysOnTop(true)
  loadingScreenStage.width = 800
  loadingScreenStage.height = 400
  val loadingScreen = new VBox()
  loadingScreen.setStyle(UIStyle.transparentObject)
  private val scene = new Scene(loadingScreen)
  scene.setFill(Color.Transparent)
  loadingScreenStage.setScene(scene)

  val loadingTextTop = new Label("Loading Combat")
  loadingTextTop.setStyle(UIStyle.loadingScreenMessage)
  loadingTextTop.alignment = Pos.BaselineCenter
  val loadingTextBottom = new Label("           Log . . . . ")
  loadingTextBottom.setStyle(UIStyle.loadingScreenMessage)
  loadingTextBottom.alignment = Pos.BaselineCenter

  loadingScreen.getChildren.addAll(loadingTextTop,loadingTextBottom)

  def startLoadingScreen() = {
    Logger.warn("Stared loading screen")
    val centerOfStage = MainStage.getCenterOfStage()
    val pos = (centerOfStage._1 - (loadingScreenStage.getWidth / 2.0), centerOfStage._2 - (loadingScreenStage.getHeight / 2.0))
//    Logger.highlight(s"${pos._1}, ${pos._2}")
    loadingScreenStage.setX(pos._1)
    loadingScreenStage.setY(pos._2)
    CustomTabs.addToUI.setOpacity(.10)
    loadingScreenStage.show
    // This forces the UI to refresh
    loadingScreenStage.getScene().getWindow().setWidth(loadingScreenStage.getScene().getWidth() + 0.001)
  }

  def closeLoadingScreen() = {
    Logger.warn("Closed loading screen")
    CustomTabs.addToUI.setOpacity(1)
    loadingScreenStage.hide
    // This forces the UI to refresh
    MainStage.mainStage.getScene().getWindow().setWidth(MainStage.mainStage.getScene().getWidth() - 0.001)
    loadingScreenStage.getScene().getWindow().setWidth(loadingScreenStage.getScene().getWidth() - 0.001)
  }

}

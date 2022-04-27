package UI.MenuBar

import java.io.File

import Controller.Controller
import UI.ElementLoader.{loadCombatInstanceMenu, refreshUI}
import UI.tabs.CustomTabs
import UI.{MainStage, UICodeConfig, UIStyle}
import Utils.Config.settings
import Utils.FileHelper
import logger.Logger
import parser.Parser
import scalafx.application.Platform
import scalafx.scene.Scene
import scalafx.scene.control.ScrollPane.ScrollBarPolicy
import scalafx.scene.control.{Button, Label, ScrollPane}
import scalafx.scene.layout.{HBox, VBox}
import scalafx.stage.{Stage, StageStyle}

import scala.collection.mutable.ListBuffer

object CombatInstanceMenu extends MenuItem {

  private val combatMenuStage = new Stage()
  combatMenuStage.initStyle(StageStyle.Undecorated)
  combatMenuStage.setAlwaysOnTop(true)
  combatMenuStage.width = 600
  combatMenuStage.height = 600
  val combatMenu = new VBox()
  combatMenu.setStyle(UIStyle.mainBackgroundObject)
  private val combatMenuScene = new Scene(combatMenu)
  combatMenuStage.setScene(combatMenuScene)

  private val closeButton = new Button {
    text = "Cancel"
    style = UIStyle.uiButtonStyle
  }

  closeButton.setOnAction(event => {
    closeMenu()
  })

  val combatMenuItems = new VBox()
  val combatInstanceScrollPane = new ScrollPane{
    hbarPolicy = ScrollBarPolicy.Never
    content = combatMenuItems
  }
  val combatMenuCancel = new HBox()

  combatMenuCancel.getChildren.addAll(UIStyle.createSpacer(),closeButton)
  combatMenu.getChildren.addAll(combatInstanceScrollPane,combatMenuCancel)

  UIStyle.setHoverable(closeButton,UIStyle.uiButtonHoverStyle)




  override def spawnMenu(): Unit = {
    combatMenuItems.getChildren.clear()
    val rows = for (combatInstance <- Controller.getAllCombatInstances()) yield {
      Logger.trace(s"Got combat instance: ${combatInstance}")
      (createRow(combatInstance.getNameFromActors))
    }
    rows.reverse.foreach(r => combatMenuItems.getChildren.add(r))

    // spawn the stage in the center of the screen
    val centerOfStage = MainStage.getCenterOfStage()
    val pos = (centerOfStage._1 - (combatMenuStage.getWidth / 2.0), centerOfStage._2 - (combatMenuStage.getHeight / 2.0))
//    Logger.highlight(s"${pos._1}, ${pos._2}")
    combatMenuStage.setX(pos._1)
    combatMenuStage.setY(pos._2)
    CustomTabs.addToUI.setOpacity(.20)
    combatMenuStage.show
  }

  override def closeMenu(): Unit = {
    CustomTabs.addToUI.setOpacity(1)
    combatMenuStage.hide()
  }

  def createRow(fileName: String, fileSize: String = "") = {

      // The basis of the row should be an hbox
      val base = new HBox()
      base.setStyle(UIStyle.insetBackgroundObject)
      base.setPrefWidth(UICodeConfig.existingTimerWidthHeight._1)

      // each row should have a label for the timer name
      val logName = new Label(fileName)
      logName.setStyle(UIStyle.mediumLightLabel)

      // each row should have the CD of the timer
      val logSize = new Label(fileSize)
      logSize.setStyle(UIStyle.mediumLightLabel)

      // Some Styling
      base.onMouseEntered = event => {
        base.setStyle(UIStyle.mainBackgroundObject)
        logName.setStyle(UIStyle.mediumLightLabel + UIStyle.textHoverLightBlue)
        logSize.setStyle(UIStyle.mediumLightLabel + UIStyle.textHoverLightBlue)
      }
      base.onMouseExited = event => {
        base.setStyle(UIStyle.insetBackgroundObject)
        logName.setStyle(UIStyle.mediumLightLabel)
        logSize.setStyle(UIStyle.mediumLightLabel)
      }

    base.onMouseClicked = event => {
      closeMenu()
      // set the current combat instance
      Controller
        .setCurrentCombatInstance(Controller.
          getCombatInstanceById(fileName))

      refreshUI()

      //once the UI is set, because we clicked on a past combat instance, set current combat to null
      Controller.endCombat()
    }

      base.getChildren.addAll(
        logName,UIStyle.createSpacer(),
        logSize
      )

    base
  }

}

package UI.MenuBar

import java.io.File

import Controller.Controller
import UI.ElementLoader.loadCombatInstanceMenu
import UI.tabs.CustomTabs
import UI.{MainStage, UICodeConfig, UIStyle}
import Utils.Config.settings
import Utils.FileHelper
import logger.Logger
import parser.Parser
import scalafx.application.Platform
import scalafx.scene.Scene
import scalafx.scene.control.ScrollPane.ScrollBarPolicy
import scalafx.scene.control.{Button, CheckBox, Label, ScrollPane}
import scalafx.scene.layout.{HBox, VBox}
import scalafx.stage.{Stage, StageStyle}

import scala.collection.mutable.ListBuffer

object LogMenu extends MenuItem {

  private val logMenuStage = new Stage()
  logMenuStage.initStyle(StageStyle.Undecorated)
  logMenuStage.setAlwaysOnTop(true)
  logMenuStage.width = 600
  logMenuStage.height = 600
  val logMenu = new VBox()
  logMenu.setStyle(UIStyle.mainBackgroundObject)
  private val logMenuScene = new Scene(logMenu)
  logMenuStage.setScene(logMenuScene)

  private val closeButton = new Button {
    text = "Cancel"
    style = UIStyle.uiButtonStyle
  }

  closeButton.setOnAction(event => {
    closeMenu()
  })

  val logMenuLogs = new VBox()
  val logMenuCancel = new HBox()
  val combatInstanceScrollPane = new ScrollPane{
    hbarPolicy = ScrollBarPolicy.Never
    content = logMenuLogs
  }

  logMenuCancel.getChildren.addAll(UIStyle.createSpacer(),closeButton)
  logMenu.getChildren.addAll(combatInstanceScrollPane,logMenuCancel)

  UIStyle.setHoverable(closeButton,UIStyle.uiButtonHoverStyle)




  override def spawnMenu(): Unit = {
    logMenuLogs.getChildren.clear()
    //    Logger.highlight(s"Configured Log Path: ${UICodeConfig.logPath}")
    val files: List[File] = FileHelper.getListOfFiles(UICodeConfig.logPath)
    var fileBuffer = new ListBuffer[MenuItem]()
    // TODO: Get this working on windows too
    var del: String = settings.get("pathDelimiter","")
//    var del = '\\'
    val rows = for (i <- 0 until files.length) yield {
      // TODO: On Windows we .split('\\') but on mac we need to split on /
      //      Logger.highlight(s"Creating menu item for file ${files(i).getAbsolutePath()} , splitting on ${del}")
      val row = files(i).getAbsolutePath().split(del).last
      val size = files(i).length() / 1000000

      (createRow(row,size.toString + " MB"))

    }

    rows.reverse.foreach(r => logMenuLogs.getChildren.add(r))

    // spawn the stage in the center of the screen
    val centerOfStage = MainStage.getCenterOfStage()
    val pos = (centerOfStage._1 - (logMenuStage.getWidth / 2.0), centerOfStage._2 - (logMenuStage.getHeight / 2.0))
//    Logger.highlight(s"${pos._1}, ${pos._2}")
    logMenuStage.setX(pos._1)
    logMenuStage.setY(pos._2)
    CustomTabs.addToUI.setOpacity(.20)
    logMenuStage.show
  }

  override def closeMenu(): Unit = {
    CustomTabs.addToUI.setOpacity(1)
    logMenuStage.hide()
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
      LoadingScreen.startLoadingScreen()
      // This forces the UI to refresh
      MainStage.mainStage.getScene().getWindow().setWidth(MainStage.mainStage.getScene().getWidth() + 0.001)
      Controller.resetController()
      Parser.resetParser()
      Platform.runLater({
        val path = s"${UICodeConfig.logPath}${fileName}"
        UICodeConfig.logFile = fileName
        Logger.info(s"Loading new log ${path}")
        Logger.debug("Begin Parsing")
        Controller.parseLatest(Parser.getNewLines(path))
        Logger.debug("End Parsing")

//        combatInstanceMenu.getItems.clear()

        loadCombatInstanceMenu()

        LoadingScreen.closeLoadingScreen()
      }
      )
    }

      base.getChildren.addAll(
        logName,UIStyle.createSpacer(),
        logSize
      )

    base
  }

}

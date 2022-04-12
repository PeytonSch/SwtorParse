package UI.overlays

import UI.{ElementLoader, Tiles}
import Utils.Config.settings
import javafx.event.EventHandler
import logger.Logger
import scalafx.event.ActionEvent
import scalafx.scene.control.{Button, CheckBox, Label}
import scalafx.scene.layout.{Background, BackgroundFill, CornerRadii, HBox}
import scalafx.Includes._
import scalafx.geometry.{Insets, Point2D}
import scalafx.scene.paint.Color
import scalafx.stage.Stage

import scala.collection.mutable


object OverlayUtils {

  val backgroundFill = new BackgroundFill(Color.web("#585858"), CornerRadii.Empty, Insets.Empty)
  //  val damageBackgroundFill = new BackgroundFill(Color.web("#FF908D"), CornerRadii.Empty, Insets.Empty)
  //  val healBackgroundFill = new BackgroundFill(Color.web("#FF908D"), CornerRadii.Empty, Insets.Empty)
  val backgroundFillArray = Array(backgroundFill)
  val background = new Background(backgroundFillArray)

  val overlayButtonStyle = "-fx-background-color: #2a2a2a;" +
    "-fx-text-fill: #ecec7a;" +
    "-fx-font-size: 12px;"


  def createMovableTop(): HBox = {
    val box = new HBox()
    val move = new Label("|↔↕|")

    box.getChildren.addAll(move)
    box
  }

  def createMovableTopWithToggles(actions:String): HBox = {
    val box = new HBox()
    val label = new Label("")
    label.setStyle(overlayButtonStyle)
    val players = new Button("Player")
    players.setStyle(overlayButtonStyle)
    if (actions == "dps") {
      players.onAction = (event: ActionEvent) =>  {
        ElementLoader.overlayDisplayModeDPS = "player"
        ElementLoader.refreshUI()
      }
    } else if (actions == "heal") {
      players.onAction = (event: ActionEvent) =>  {
        ElementLoader.overlayDisplayModeHPS = "player"
        ElementLoader.refreshUI()
      }
    }
    else if (actions == "dtps") {
      players.onAction = (event: ActionEvent) =>  {
        ElementLoader.overlayDisplayModeDtps = "player"
        ElementLoader.refreshUI()
      }
    }
    val boss = new Button("Boss")
    boss.setStyle(overlayButtonStyle)
    if (actions == "dps") {
      boss.onAction = (event: ActionEvent) =>  {
        ElementLoader.overlayDisplayModeDPS = "boss"
        ElementLoader.refreshUI()
      }
    } else if (actions == "heal") {
      boss.onAction = (event: ActionEvent) =>  {
        ElementLoader.overlayDisplayModeHPS = "boss"
        ElementLoader.refreshUI()
      }
    }
    else if (actions == "dtps") {
      boss.onAction = (event: ActionEvent) =>  {
        ElementLoader.overlayDisplayModeDtps = "boss"
        ElementLoader.refreshUI()
      }
    }
    val companion = new Button("Comp")
    companion.setStyle(overlayButtonStyle)
    if (actions == "dps") {
      companion.onAction = (event: ActionEvent) =>  {
        ElementLoader.overlayDisplayModeDPS = "comp"
        ElementLoader.refreshUI()
      }
    } else if (actions == "heal") {
      companion.onAction = (event: ActionEvent) =>  {
        ElementLoader.overlayDisplayModeHPS = "comp"
        ElementLoader.refreshUI()
      }
    }
    else if (actions == "dtps") {
      companion.onAction = (event: ActionEvent) =>  {
        ElementLoader.overlayDisplayModeDtps = "comp"
        ElementLoader.refreshUI()
      }
    }
    val all = new Button("All")
    all.setStyle(overlayButtonStyle)
    if (actions == "dps") {
      all.onAction = (event: ActionEvent) =>  {
        ElementLoader.overlayDisplayModeDPS = "all"
        Logger.highlight(s"Set dps display mode to ${ElementLoader.overlayDisplayModeDPS}")
        ElementLoader.refreshUI()
      }
    } else if (actions == "heal") {
      all.onAction = (event: ActionEvent) =>  {
        ElementLoader.overlayDisplayModeHPS = "all"
        ElementLoader.refreshUI()
      }
    }
    else if (actions == "dtps") {
      all.onAction = (event: ActionEvent) =>  {
        ElementLoader.overlayDisplayModeDtps = "all"
        ElementLoader.refreshUI()
      }
    }
    val move = new Label(" | ↔↕ | ")
    move.setStyle(overlayButtonStyle)

    box.getChildren.addAll(players,boss,companion,all,label,move)
    box.setBackground(Tiles.background)
    box
  }



  // ARRAY of Anchor Points so everything has its own point data stored
  private val anchors: mutable.ArrayBuffer[Point2D] = mutable.ArrayBuffer(null,null,null,null,null,null,null,null,null,null,null)
  private val previousLocations: mutable.ArrayBuffer[Point2D] = mutable.ArrayBuffer(null,null,null,null,null,null,null,null,null,null,null)


  def initMovableVBox(box:HBox, overlay: Stage, index:Int): Unit = {

    box.setOnMouseClicked(new EventHandler[javafx.scene.input.MouseEvent] {
      override def handle(event: javafx.scene.input.MouseEvent): Unit = {
        anchors(index) = new Point2D(event.screenX, event.screenY)
      }
    })

    box.setOnMouseDragged(new EventHandler[javafx.scene.input.MouseEvent] {
      override def handle(event: javafx.scene.input.MouseEvent): Unit = {
        if (anchors(index) != null && previousLocations(index) != null) {
          overlay.x = previousLocations(index).x + event.screenX - anchors(index).x
          overlay.y = previousLocations(index).y + event.screenY - anchors(index).y
        }
      }
    })

    box.setOnMouseReleased(new EventHandler[javafx.scene.input.MouseEvent] {
      override def handle(event: javafx.scene.input.MouseEvent): Unit = {
        previousLocations(index) = new Point2D(overlay.getX, overlay.getY)
        //        Logger.highlight(s"Released HBOX ${box.getId} at position ${overlay.getX},${overlay.getY}")
        settings.putDouble(box.getId+"_X",overlay.getX)
        settings.putDouble(box.getId+"_Y",overlay.getY)
      }
    })



    //    scene.setOnMouseDragged() = (event: MouseEvent) =>
    //      if (anchorPt != null && previousLocation != null) {
    //        personalDpsOverlay.x = previousLocation.x + event.screenX - anchorPt.x
    //        personalDpsOverlay.y = previousLocation.y + event.screenY - anchorPt.y
    //      }
    //
    //    scene.setOnMouseReleased() = (event: MouseEvent) => previousLocation = new Point2D(personalDpsOverlay.getX, personalDpsOverlay.getY)
    //
    //    personalDpsOverlay.onShown = (event: WindowEvent) => previousLocation = new Point2D(personalDpsOverlay.getX, personalDpsOverlay.getY)
  }


  /**
   * Checkbox actions for settings page
   */

    // TODO: Should this go in the overlay trait?

  def setCheckboxAction(c:CheckBox, overlay: Stage, settingName: String,posSetting: String): Unit = {
    c.onAction = (event: ActionEvent) => {
      if (c.selectedProperty().value == true) {
        overlay.setX(settings.getDouble(posSetting+"_X",500))
        overlay.setY(settings.getDouble(posSetting+"_Y",500))
        overlay.show()
        settings.putBoolean(settingName,true)
      }
      else {
        overlay.hide()
        settings.putBoolean(settingName,false)
      }
    }
  }


}

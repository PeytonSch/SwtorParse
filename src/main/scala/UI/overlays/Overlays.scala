package UI.overlays

import eu.hansolo.tilesfx.Tile.SkinType
import eu.hansolo.tilesfx.TileBuilder
import javafx.event.EventHandler
import logger.Logger
import scalafx.scene.Scene
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{Background, BackgroundFill, CornerRadii, StackPane, VBox}
import scalafx.stage.{Stage, StageStyle}
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.geometry.{Insets, Point2D, Pos}
import scalafx.scene.Scene
import scalafx.scene.input.MouseEvent
import scalafx.scene.paint.{Color, Paint}
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.Text
import scalafx.stage.{StageStyle, WindowEvent}

import scala.util.Random

object Overlays {

  val backgroundFill = new BackgroundFill(Color.web("#585858"), CornerRadii.Empty, Insets.Empty)
//  val damageBackgroundFill = new BackgroundFill(Color.web("#FF908D"), CornerRadii.Empty, Insets.Empty)
//  val healBackgroundFill = new BackgroundFill(Color.web("#FF908D"), CornerRadii.Empty, Insets.Empty)
  val backgroundFillArray = Array(backgroundFill)
  val background = new Background(backgroundFillArray)

  
  
  /**
   * Create Windows
   */

  /**
   * Personal Damage Overlay
   */

  val personalDamageOverlay = TileBuilder.create()
    .skinType(SkinType.DONUT_CHART)
    //    .prefSize(TILE_WIDTH, TILE_HEIGHT)
    .title("Damage Done")
    .textVisible(true)
    .sectionTextVisible(true)
    .build();
    
  val personalDamagePane = new VBox()
  personalDamagePane.setBackground(background)
  personalDamagePane.getChildren.add(personalDamageOverlay)
  personalDamagePane.setPrefSize(200,200)
  val personalDpsOverlay = new Stage()
  personalDpsOverlay.initStyle(StageStyle.Utility)
  val dpsOverlayScene = new Scene(personalDamagePane)
  personalDpsOverlay.setTitle("Damage Done")
  personalDpsOverlay.setAlwaysOnTop(true)


  /**
   * Personal Healing Overlay
   */

  val personalHealingOverlay = TileBuilder.create()
    .skinType(SkinType.DONUT_CHART)
    //    .prefSize(TILE_WIDTH, TILE_HEIGHT)
    .title("Healing Done")
    .textVisible(true)
    .sectionTextVisible(true)
    .build();

  val personalHealingPane = new VBox()
  personalHealingPane.setBackground(background)
  personalHealingPane.getChildren.add(personalHealingOverlay)
  personalHealingPane.setPrefSize(200,200)
  val personalHpsOverlay = new Stage()
  personalHpsOverlay.initStyle(StageStyle.Utility)
  val hpsOverlayScene = new Scene(personalHealingPane)
  personalHpsOverlay.setTitle("Healing Done")
  personalHpsOverlay.setAlwaysOnTop(true)


  /**
   * Personal Damage Taken Overlay
   */

  val personalDamageTakenOverlay = TileBuilder.create()
    .skinType(SkinType.DONUT_CHART)
    //    .prefSize(TILE_WIDTH, TILE_HEIGHT)
    .title("DamageTaken Done")
    .textVisible(true)
    .sectionTextVisible(true)
    .build();

  val personalDamageTakenPane = new VBox()
  personalDamageTakenPane.setBackground(background)
  personalDamageTakenPane.getChildren.add(personalDamageTakenOverlay)
  personalDamageTakenPane.setPrefSize(200,200)
  val personalDtpsOverlay = new Stage()
  personalDtpsOverlay.initStyle(StageStyle.Utility)
  val dtpsOverlayScene = new Scene(personalDamageTakenPane)
  personalDtpsOverlay.setTitle("Damage Taken")
  personalDtpsOverlay.setAlwaysOnTop(true)

  
  
  /**
   * Group Damage Overlay
   */
  val groupDamagePane = new VBox()

  for (i <- Range(0,6)) {
    val stacked = new StackPane()
    val text = new Text()
    text.setText("This is my text!!")
    val rect = Rectangle((i-8)*(-25),30)
    val backgroundRect = Rectangle(200, 30)
    backgroundRect.setStyle("-fx-fill: #FF908D; -fx-stroke: black; -fx-stroke-width: 2;")
    rect.setStyle("-fx-fill: #FF3633; -fx-stroke: black; -fx-stroke-width: 2;");
    stacked.getChildren.addAll(backgroundRect,rect,text)
    stacked.setAlignment(Pos.CenterLeft)
    groupDamagePane.getChildren.add(stacked)
  }

  groupDamagePane.setBackground(background)
  groupDamagePane.setPrefSize(200,200)
  val groupDpsOverlay = new Stage()
  groupDpsOverlay.initStyle(StageStyle.Utility)
  val groupDpsOverlayScene = new Scene(groupDamagePane)
  groupDpsOverlay.setTitle("Group DPS")
  groupDpsOverlay.setAlwaysOnTop(true)


  /**
   * Group Healing Overlay
   */
  val groupHealingPane = new VBox()

  for (i <- Range(0,3)) {
    val stacked = new StackPane()
    val text = new Text()
    text.setText("This is my text!!")
    val rect = Rectangle((i-8)*(-25),30)
    val backgroundRect = Rectangle(200, 30)
    backgroundRect.setStyle("-fx-fill: #48FF80; -fx-stroke: black; -fx-stroke-width: 2;")
    rect.setStyle("-fx-fill: #5CFF47; -fx-stroke: black; -fx-stroke-width: 2;");
    stacked.getChildren.addAll(backgroundRect,rect,text)
    stacked.setAlignment(Pos.CenterLeft)
    groupHealingPane.getChildren.add(stacked)
  }

  groupHealingPane.setBackground(background)
  groupHealingPane.setPrefSize(200,200)
  val groupHpsOverlay = new Stage()
  groupHpsOverlay.initStyle(StageStyle.Utility)
  val groupHpsOverlayScene = new Scene(groupHealingPane)
  groupHpsOverlay.setTitle("Group Hps")
  groupHpsOverlay.setAlwaysOnTop(true)
  



  /**
   * Movable Windows, this is a WIP
   */
  // TODO: Fix movable windows, click and drag icon?
  private var anchorPt: Point2D = null
  private var previousLocation: Point2D = null

  initMovablePlayer(dpsOverlayScene)
//  initMovablePlayer(dpsPane.getScene)
//  initMovablePlayer(dpsPercentageOverlay.getScene)



    private def initMovablePlayer(scene:Scene): Unit = {
      scene.onMousePressed = (event: MouseEvent) => anchorPt = new Point2D(event.screenX, event.screenY)

      scene.onMouseDragged = (event: MouseEvent) =>
        if (anchorPt != null && previousLocation != null) {
          personalDpsOverlay.x = previousLocation.x + event.screenX - anchorPt.x
          personalDpsOverlay.y = previousLocation.y + event.screenY - anchorPt.y
        }

      scene.onMouseReleased = (event: MouseEvent) => previousLocation = new Point2D(personalDpsOverlay.getX, personalDpsOverlay.getY)

      personalDpsOverlay.onShown = (event: WindowEvent) => previousLocation = new Point2D(personalDpsOverlay.getX, personalDpsOverlay.getY)
    }



//  dpsPane.getChildren.add(dpsPercentageOverlay)

}

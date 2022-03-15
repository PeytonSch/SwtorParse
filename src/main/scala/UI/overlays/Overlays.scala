package UI.overlays

import UI.{ElementLoader, Tiles}
import eu.hansolo.tilesfx.Tile.SkinType
import eu.hansolo.tilesfx.{Tile, TileBuilder}
import javafx.event.EventHandler
import logger.Logger
import scalafx.scene.Scene
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{Background, BackgroundFill, CornerRadii, HBox, StackPane, VBox}
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
import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.scene.control.ScrollPane.ScrollBarPolicy
import scalafx.scene.control.{Button, Label, ScrollPane}

import scala.collection.mutable
import scala.util.Random

object Overlays {

  val backgroundFill = new BackgroundFill(Color.web("#585858"), CornerRadii.Empty, Insets.Empty)
//  val damageBackgroundFill = new BackgroundFill(Color.web("#FF908D"), CornerRadii.Empty, Insets.Empty)
//  val healBackgroundFill = new BackgroundFill(Color.web("#FF908D"), CornerRadii.Empty, Insets.Empty)
  val backgroundFillArray = Array(backgroundFill)
  val background = new Background(backgroundFillArray)

  val overlayButtonStyle = "-fx-background-color: #2a2a2a;" +
                           "-fx-text-fill: #ecec7a;" +
                           "-fx-font-size: 12px;"


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
    val move = new Label(" | ↔↕ | ")
    move.setStyle(overlayButtonStyle)

    box.getChildren.addAll(players,boss,companion,all,label,move)
    box.setBackground(Tiles.background)
    box
  }

  def createMovableTop(): HBox = {
    val box = new HBox()
    val move = new Label("|↔↕|")

    box.getChildren.addAll(move)
    box
  }


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
  val personalDamageTop = createMovableTop()
  personalDamagePane.setBackground(background)
  val personalDpsOverlay = new Stage()
  personalDamagePane.getChildren.addAll(personalDamageTop,personalDamageOverlay)
  personalDamagePane.setPrefSize(200,200)
  personalDpsOverlay.initStyle(StageStyle.Undecorated)
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
  val personalHealingTop = createMovableTop()
  personalHealingPane.setBackground(background)
  personalHealingPane.getChildren.addAll(personalHealingTop,personalHealingOverlay)
  personalHealingPane.setPrefSize(200,200)
  val personalHpsOverlay = new Stage()
  personalHpsOverlay.initStyle(StageStyle.Undecorated)
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
  val personalDamageTakenTop = createMovableTop()
  personalDamageTakenPane.setBackground(background)
  personalDamageTakenPane.getChildren.addAll(personalDamageTakenTop,personalDamageTakenOverlay)
  personalDamageTakenPane.setPrefSize(200,200)
  val personalDtpsOverlay = new Stage()
  personalDtpsOverlay.initStyle(StageStyle.Undecorated)
  val dtpsOverlayScene = new Scene(personalDamageTakenPane)
  personalDtpsOverlay.setTitle("Damage Taken")
  personalDtpsOverlay.setAlwaysOnTop(true)

  
  
  /**
   * Group Damage Overlay
   */
  val groupDamageOuter = new VBox()
  groupDamageOuter.setBackground(Tiles.background)
  val groupDamagePane = new VBox()
  val groupDamageTop = createMovableTopWithToggles("dps")

  val groupDamageScrollPane = new ScrollPane()
  groupDamageScrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
  groupDamageScrollPane.setBackground(Tiles.background)

  groupDamageOuter.getChildren.addAll(groupDamageTop,groupDamageScrollPane)

  // this populates some test data on start
//  for (i <- Range(0,6)) {
//    val stacked = new StackPane()
//    val text = new Text()
//    text.setText("This is my text!!")
//    val rect = Rectangle((i-8)*(-25),30)
//    val backgroundRect = Rectangle(200, 30)
//    backgroundRect.setStyle("-fx-fill: #FF908D; -fx-stroke: black; -fx-stroke-width: 2;")
//    rect.setStyle("-fx-fill: #FF3633; -fx-stroke: black; -fx-stroke-width: 2;");
//    stacked.getChildren.addAll(backgroundRect,rect,text)
//    stacked.setAlignment(Pos.CenterLeft)
//    groupDamagePane.getChildren.add(stacked)
//  }

  groupDamagePane.setBackground(background)
  groupDamagePane.setPrefSize(200,200)
  val groupDpsOverlay = new Stage()
  groupDpsOverlay.initStyle(StageStyle.Undecorated)
  groupDamageScrollPane.setContent(groupDamagePane)
  val groupDpsOverlayScene = new Scene(groupDamageOuter)
  groupDpsOverlay.setTitle("Group DPS")
  groupDpsOverlay.setAlwaysOnTop(true)


  /**
   * Group Healing Overlay
   */
  val groupHealingOuter = new VBox()
  groupHealingOuter.setBackground(Tiles.background)
  val groupHealingPane = new VBox()
  val groupHealingTop = createMovableTopWithToggles("heal")
  groupHealingPane.getChildren.add(groupHealingTop)

  val groupHealingScrollPane = new ScrollPane()
  groupHealingScrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
  groupHealingScrollPane.setBackground(Tiles.background)

  groupHealingOuter.getChildren.addAll(groupHealingTop,groupHealingScrollPane)

  // this populates some test data on start
//  for (i <- Range(0,3)) {
//    val stacked = new StackPane()
//    val text = new Text()
//    text.setText("This is my text!!")
//    val rect = Rectangle((i-8)*(-25),30)
//    val backgroundRect = Rectangle(200, 30)
//    backgroundRect.setStyle("-fx-fill: #48FF80; -fx-stroke: black; -fx-stroke-width: 2;")
//    rect.setStyle("-fx-fill: #5CFF47; -fx-stroke: black; -fx-stroke-width: 2;");
//    stacked.getChildren.addAll(backgroundRect,rect,text)
//    stacked.setAlignment(Pos.CenterLeft)
//    groupHealingPane.getChildren.add(stacked)
//  }

  groupHealingPane.setBackground(background)
  groupHealingPane.setPrefSize(200,200)
  val groupHpsOverlay = new Stage()
  groupHpsOverlay.initStyle(StageStyle.Undecorated)
  groupHealingScrollPane.setContent(groupHealingPane)
  val groupHpsOverlayScene = new Scene(groupHealingOuter)
  groupHpsOverlay.setTitle("Group Hps")
  groupHpsOverlay.setAlwaysOnTop(true)


  // ARRAY of Anchor Points so everything has its own point data stored
  // TODO: Write these points to a file and read them in so that we save the last location so they go there on startup
  private var anchors: mutable.ArrayBuffer[Point2D] = mutable.ArrayBuffer(null,null,null,null,null,null,null,null)
  private var previousLocations: mutable.ArrayBuffer[Point2D] = mutable.ArrayBuffer(null,null,null,null,null,null,null,null)

  initMovableVBox(groupDamageTop,groupDpsOverlay,0)
  initMovableVBox(groupHealingTop,groupHpsOverlay,1)
  initMovableVBox(personalDamageTakenTop,personalDtpsOverlay,2)
  initMovableVBox(personalDamageTop,personalDpsOverlay,3)
  initMovableVBox(personalHealingTop,personalHpsOverlay,4)

//  initMovableScene(personalDamageOverlay.getScene,5)
//  initMovableScene(personalHealingOverlay.getScene,6)
//  initMovableScene(dpsOverlayScene,7)



  /**
   * Movable Windows, this is a WIP
   */
  // TODO: Fix boss windows, click and drag icon?
//  private var anchorPt: Point2D = null
//  private var previousLocation: Point2D = null


//  initMovablePlayer(dpsPane.getScene)
//  initMovablePlayer(dpsPercentageOverlay.getScene)



    private def initMovableScene(scene:Scene,index:Int): Unit = {
      scene.onMousePressed = (event: MouseEvent) => anchors(index) = new Point2D(event.screenX, event.screenY)

      scene.onMouseDragged = (event: MouseEvent) =>
        if (anchors(index) != null && previousLocations(index) != null) {
          personalDpsOverlay.x = previousLocations(index).x + event.screenX - anchors(index).x
          personalDpsOverlay.y = previousLocations(index).y + event.screenY - anchors(index).y
        }

      scene.onMouseReleased = (event: MouseEvent) => previousLocations(index) = new Point2D(personalDpsOverlay.getX, personalDpsOverlay.getY)

      personalDpsOverlay.onShown = (event: WindowEvent) => previousLocations(index) = new Point2D(personalDpsOverlay.getX, personalDpsOverlay.getY)
    }

  private def initMovableVBox(box:HBox, overlay: Stage, index:Int): Unit = {

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


//  private def initMovableTile(box:VBox, overlay: Tile ): Unit = {
//
//    box.setOnMouseClicked(new EventHandler[javafx.scene.input.MouseEvent] {
//      override def handle(event: javafx.scene.input.MouseEvent): Unit = {
//        anchorPt = new Point2D(event.screenX, event.screenY)
//      }
//    })
//
//    box.setOnMouseDragged(new EventHandler[javafx.scene.input.MouseEvent] {
//      override def handle(event: javafx.scene.input.MouseEvent): Unit = {
//        if (anchorPt != null && previousLocation != null) {
//          overlay.getSc = previousLocation.x + event.screenX - anchorPt.x
//          overlay.y = previousLocation.y + event.screenY - anchorPt.y
//        }
//      }
//    })
//
//    box.setOnMouseReleased(new EventHandler[javafx.scene.input.MouseEvent] {
//      override def handle(event: javafx.scene.input.MouseEvent): Unit = {
//        previousLocation = new Point2D(overlay.getX, overlay.getY)
//      }
//    })



//  dpsPane.getChildren.add(dpsPercentageOverlay)

}

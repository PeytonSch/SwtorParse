package UI.overlays

import Controller.Controller
import UI.ElementLoader.{overlayDisplayModeDPS, overlayDisplayModeHPS}
import UI.Tiles
import UI.overlays.OverlayUtils.{createMovableTopWithToggles, initMovableVBox, setCheckboxAction}
import Utils.Config.settings
import logger.Logger
import scalafx.geometry.Pos
import scalafx.scene.Scene
import scalafx.scene.control.{CheckBox, ScrollPane}
import scalafx.scene.control.ScrollPane.ScrollBarPolicy
import scalafx.scene.layout.{StackPane, VBox}
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.Text
import scalafx.stage.{Stage, StageStyle}
import UI.UIStyle._


object GroupHealing extends Overlay {

  override def getOverlay(): Stage = groupHpsOverlay

  override def clear(): Unit = groupHealingPane.getChildren.clear()

  override def refresh(): Unit = {

    clear()

    /**
     * Update Overlay Group Healing Done
     */

    // what actor has done the most Healing this tick?
    var maxHealing = 1
    var totalHealing = 1
    for (actor <- Controller.getCurrentCombat().getCombatActors()) {
      totalHealing = totalHealing + actor.getHealingDone()
      if (actor.getHealingDone() > maxHealing && actor.getActorType() == "Player") maxHealing = actor.getHealingDone()
    }
    if(totalHealing > 1) totalHealing = totalHealing - 1

    val sortedByHealingDone = Controller.getCurrentCombat().getCombatActors().sortWith(_.getHealingDone() > _.getHealingDone()).filter(_.getHealingDone() > 0)

    // only display the toggled mode
    val filterHealingByMode = overlayDisplayModeHPS match {
      case "player" => sortedByHealingDone.filter(x => (x.getActorType() == "Player"))
      case "boss" => sortedByHealingDone.filter(x => !(x.getActorType() == "Companion")) // TODO: Implement a Boss type for bosses
      case "comp" => sortedByHealingDone.filter(x => (x.getActorType() == "Player" || (x.getActorType() == "Companion")))
      case "all" => sortedByHealingDone
      case _ => {
        Logger.warn(s"Variable error for filtered overlays. Variable value ${overlayDisplayModeDPS} unexpected. Setting to \"player\" and continuing.")
        overlayDisplayModeDPS = "player"
        sortedByHealingDone.filter(_.getActorType() == "Player")
      }
    }

    for (actor <- filterHealingByMode) {
      val stacked = new StackPane()
      val text = new Text()
      val percentMaxFill: Int = ((actor.getHealingDone().toDouble / maxHealing) * 200).toInt
      val percentMax: Int = ((actor.getHealingDone().toDouble / totalHealing) * 100).toInt
      text.setText(actor.getActor().getName() + ": " + actor.getHealingDonePerSecond() + " (" + percentMax + "%)")
      val rect = Rectangle(percentMaxFill,30)
      val backgroundRect = Rectangle(600, 30)
      backgroundRect.setStyle("-fx-fill: #48FF80; -fx-stroke: black; -fx-stroke-width: 2;")
      if(actor.getActorType() == "Player"){
        rect.setStyle("-fx-fill: #5CFF47; -fx-stroke: black; -fx-stroke-width: 2;")
      }
      else if (actor.getActorType() == "Companion") {
        rect.setStyle("-fx-fill: #B93DFF; -fx-stroke: black; -fx-stroke-width: 2;")
      }
      else {
        rect.setStyle("-fx-fill: #4C3DFF; -fx-stroke: black; -fx-stroke-width: 2;")
      }
      stacked.getChildren.addAll(backgroundRect,rect,text)
      stacked.setAlignment(Pos.CenterLeft)
      groupHealingPane.getChildren.add(stacked)
    }


  }



  /**
   * Group Healing Overlay
   */
  val groupHealingOuter = new VBox()
  groupHealingOuter.setBackground(background)
  val groupHealingPane = new VBox()
  val groupHealingTop = createMovableTopWithToggles("heal")
  groupHealingTop.setId("groupHealingTop")
  //  groupHealingPane.getChildren.add(groupHealingTop)

  val groupHealingScrollPane = new ScrollPane()
  groupHealingScrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
  groupHealingScrollPane.setBackground(background)

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
  groupHpsOverlay.setScene(groupHpsOverlayScene)

  initMovableVBox(groupHealingTop,groupHpsOverlay,1)

  override def createSettingsCheckbox(): CheckBox = {
    val hpsCheckbox = new CheckBox("Group Healing")
    setCheckboxAction(hpsCheckbox, GroupHealing.getOverlay(),"groupHpsOverlayEnabled","groupHealingTop")
    if (settings.getBoolean("groupHpsOverlayEnabled",false)) {
      hpsCheckbox.setSelected(true)
      GroupHealing.getOverlay().setX(settings.getDouble("groupHealingTop_X",500))
      GroupHealing.getOverlay().setY(settings.getDouble("groupHealingTop_Y",500))
      GroupHealing.getOverlay().show()
    }
    hpsCheckbox
  }


}

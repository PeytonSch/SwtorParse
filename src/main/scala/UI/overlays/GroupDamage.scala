package UI.overlays

import Controller.Controller
import UI.ElementLoader.overlayDisplayModeDPS
import UI.UIStyle
import UI.UIStyle.background
import UI.overlays.OverlayUtils.{createMovableTopWithToggles, initMovableVBox, setCheckboxAction}
import Utils.Config.settings
import logger.Logger
import scalafx.geometry.Pos
import scalafx.scene.{Node, Scene}
import scalafx.scene.control.{CheckBox, ScrollPane}
import scalafx.scene.control.ScrollPane.ScrollBarPolicy
import scalafx.scene.layout.{StackPane, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.Text
import scalafx.stage.{Stage, StageStyle}

object GroupDamage extends Overlay {

  override def getOverlay(): Stage = groupDpsOverlay

  override def clear(): Unit = groupDamagePane.getChildren.clear()


  override def refresh(): Unit = {

    clear()
    /**
     * Update Overlay Group Damage Done
     */

    // what actor has done the most damage this tick?
    var maxDamage = 1
    var totalDamage = 1
    // TODO: Adjust the percentages to show based on mode
    var totalPlayerDamage = 1
    for (actor <- Controller.getCurrentCombat().getCombatActors()) {
      totalDamage = totalDamage + actor.getDamageDone()
      if (actor.getDamageDone() > maxDamage && actor.getActorType() == "Player") maxDamage = actor.getDamageDone()
    }
    if (totalDamage > 1) totalDamage = totalDamage - 1
    if (totalPlayerDamage > 1) totalPlayerDamage = totalPlayerDamage - 1

    val sortedByDamageDone = Controller.getCurrentCombat().getCombatActors().sortWith(_.getDamageDone() > _.getDamageDone()).filter(_.getDamageDone() > 0)

    // only display the toggled mode
    val filterDamageByMode = overlayDisplayModeDPS match {
      case "player" => sortedByDamageDone.filter(x => (x.getActorType() == "Player"))
      case "boss" => sortedByDamageDone.filter(x => !(x.getActorType() == "Companion")) // TODO: Implement a Boss type for bosses
      case "comp" => sortedByDamageDone.filter(x => (x.getActorType() == "Player" || (x.getActorType() == "Companion")))
      case "all" => sortedByDamageDone
      case _ => {
        Logger.warn(s"Variable error for filtered overlays. Variable value ${overlayDisplayModeDPS} unexpected. Setting to \"player\" and continuing.")
        overlayDisplayModeDPS = "player"
        sortedByDamageDone.filter(_.getActorType() == "Player")
      }
    }


    for (actor <- filterDamageByMode) {
      val stacked = new StackPane()
      val text = new Text()
      val percentMaxFill: Int = ((actor.getDamageDone().toDouble / maxDamage) * 200).toInt
      val percentMax: Int = ((actor.getDamageDone().toDouble / totalDamage) * 100).toInt
      text.setText(actor.getActor().getName() + ": " + actor.getDamagePerSecond() + " (" + percentMax + "%)")
      val rect = Rectangle(percentMaxFill,30)
      val backgroundRect = Rectangle(600, 30)
      if(actor.getActorType() == "Player"){
        rect.setStyle("-fx-fill: #FF3633; -fx-stroke: black; -fx-stroke-width: 2;")
      }
      else if (actor.getActorType() == "Companion") {
        rect.setStyle("-fx-fill: #B93DFF; -fx-stroke: black; -fx-stroke-width: 2;")
      }
      else {
        rect.setStyle("-fx-fill: #4C3DFF; -fx-stroke: black; -fx-stroke-width: 2;")
      }
      backgroundRect.setStyle("-fx-fill: #FF908D; -fx-stroke: black; -fx-stroke-width: 2;")
      backgroundRect.setOpacity(1)
      rect.setOpacity(1)
      stacked.getChildren.addAll(backgroundRect,rect,text)
      stacked.setAlignment(Pos.CenterLeft)
      stacked.setStyle("-fx-background-color: rgba(0,255,0,0)")
      stacked.setOpacity(1)
      groupDamagePane.getChildren.add(stacked)
    }
  }

  /**
   * Group Damage Overlay
   */
  val groupDamageOuter = new VBox()
  groupDamageOuter.setBackground(background)
  val groupDamagePane = new VBox()
  val groupDamageTop = createMovableTopWithToggles("dps")
  groupDamageTop.setId("groupDamageTop")

  val groupDamageScrollPane = new ScrollPane()
  groupDamageScrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
  groupDamageScrollPane.setBackground(background)

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
//  groupDamagePane.setMaxSize(200,200)
  val groupDpsOverlay = new Stage()
  groupDpsOverlay.initStyle(StageStyle.Undecorated)
  //  groupDpsOverlay.initStyle(StageStyle.Undecorated)
  groupDamageScrollPane.setContent(groupDamagePane)
  val groupDpsOverlayScene = new Scene(groupDamageOuter)
  groupDpsOverlay.setTitle("Group DPS")
  groupDpsOverlay.setAlwaysOnTop(true)
  groupDpsOverlay.setScene(groupDpsOverlayScene)

  initMovableVBox(groupDamageTop,groupDpsOverlay,0)



  override def createSettingsCheckbox(): CheckBox = {
    val dpsCheckbox = new CheckBox("Group Damage")
    dpsCheckbox.setStyle(UIStyle.mediumLightLabel)
    UIStyle.setHoverable(dpsCheckbox,UIStyle.mediumLightLabel + UIStyle.textHoverLightBlue)
    setCheckboxAction(dpsCheckbox, GroupDamage.getOverlay(),"groupDpsOverlayEnabled","groupDamageTop")
    if (settings.getBoolean("groupDpsOverlayEnabled",false)) {
      dpsCheckbox.setSelected(true)
      GroupDamage.getOverlay().setX(settings.getDouble("groupDamageTop_X",500))
      GroupDamage.getOverlay().setY(settings.getDouble("groupDamageTop_Y",500))
      GroupDamage.getOverlay().show()
    }
    dpsCheckbox
  }


}

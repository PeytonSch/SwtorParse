package UI.overlays

import Controller.Controller
import UI.ElementLoader.overlayDisplayModeDtps
import UI.Tiles
import UI.overlays.OverlayUtils.{background, createMovableTopWithToggles, initMovableVBox, setCheckboxAction}
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

object GroupDTPS extends Overlay {

  override def getOverlay(): Stage = groupDtpsOverlay

  override def clear(): Unit = groupDtpsPane.getChildren.clear()

  override def refresh(): Unit = {

    clear()
    /**
     * Update Overlay Group Damage Taken
     */

    // what actor has done the most damage this tick?
    var maxDamageTaken = 1
    var totalDamageTaken = 1
    // TODO: Adjust the percentages to show based on mode
    var totalPlayerDamageTaken = 1
    for (actor <- Controller.getCurrentCombat().getCombatActors()) {
      totalDamageTaken = totalDamageTaken + actor.getDamageTaken()
      if (actor.getDamageTaken() > maxDamageTaken && actor.getActorType() == "Player") maxDamageTaken = actor.getDamageTaken()
    }
    if (totalDamageTaken > 1) totalDamageTaken = totalDamageTaken - 1
    if (totalPlayerDamageTaken > 1) totalPlayerDamageTaken = totalPlayerDamageTaken - 1

    val sortedByDamageTaken = Controller.getCurrentCombat().getCombatActors().sortWith(_.getDamageTaken() > _.getDamageTaken()).filter(_.getDamageTaken() > 0)

    // only display the toggled mode
    val filterDamageTakenByMode = overlayDisplayModeDtps match {
      case "player" => sortedByDamageTaken.filter(x => (x.getActorType() == "Player"))
      case "boss" => sortedByDamageTaken.filter(x => !(x.getActorType() == "Companion")) // TODO: Implement a Boss type for bosses
      case "comp" => sortedByDamageTaken.filter(x => (x.getActorType() == "Player" || (x.getActorType() == "Companion")))
      case "all" => sortedByDamageTaken
      case _ => {
        Logger.warn(s"Variable error for filtered overlays. Variable value ${overlayDisplayModeDtps} unexpected. Setting to \"player\" and continuing.")
        overlayDisplayModeDtps = "player"
        sortedByDamageTaken.filter(_.getActorType() == "Player")
      }
    }

    for (actor <- filterDamageTakenByMode) {
      val stacked = new StackPane()
      val text = new Text()
      val percentMaxFill: Int = ((actor.getDamageTaken().toDouble / maxDamageTaken) * 200).toInt
      val percentMax: Int = ((actor.getDamageTaken().toDouble / totalDamageTaken) * 100).toInt
      text.setText(actor.getActor().getName() + ": " + actor.getDamageTakenPerSecond() + " (" + percentMax + "%)")
      val rect = Rectangle(percentMaxFill,30)
      val backgroundRect = Rectangle(600, 30)
      if(actor.getActorType() == "Player"){
        rect.setStyle("-fx-fill: #FFE410; -fx-stroke: black; -fx-stroke-width: 2;")
      }
      else if (actor.getActorType() == "Companion") {
        rect.setStyle("-fx-fill: #B93DFF; -fx-stroke: black; -fx-stroke-width: 2;")
      }
      else {
        rect.setStyle("-fx-fill: #4C3DFF; -fx-stroke: black; -fx-stroke-width: 2;")
      }
      backgroundRect.setStyle("-fx-fill: #FFDF99; -fx-stroke: black; -fx-stroke-width: 2;")
      stacked.getChildren.addAll(backgroundRect,rect,text)
      stacked.setAlignment(Pos.CenterLeft)
      stacked.setStyle("-fx-background-color: rgba(0,255,0,0)")
      groupDtpsPane.getChildren.add(stacked)
    }
  }

  /**
   * Group Dtps Overlay
   */
  val groupDtpsOuter = new VBox()
  groupDtpsOuter.setBackground(Tiles.background)
  val groupDtpsPane = new VBox()
  val groupDtpsTop = createMovableTopWithToggles("dtps")
  groupDtpsTop.setId("groupDtpsTop")
  val groupDtpsScrollPane = new ScrollPane()
  groupDtpsScrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
  groupDtpsScrollPane.setBackground(Tiles.background)

  groupDtpsOuter.getChildren.addAll(groupDtpsTop,groupDtpsScrollPane)

  groupDtpsPane.setBackground(background)
  groupDtpsPane.setPrefSize(200,200)
  val groupDtpsOverlay = new Stage()
  groupDtpsOverlay.initStyle(StageStyle.Undecorated)
  groupDtpsScrollPane.setContent(groupDtpsPane)
  val groupDtpsOverlayScene = new Scene(groupDtpsOuter)
  groupDtpsOverlay.setTitle("Group DTPS")
  groupDtpsOverlay.setAlwaysOnTop(true)
  groupDtpsOverlay.setScene(groupDtpsOverlayScene)

  initMovableVBox(groupDtpsTop,groupDtpsOverlay,7)


  override def createSettingsCheckbox(): CheckBox = {
    val dtpsCheckbox = new CheckBox("Group Damage Taken")
    setCheckboxAction(dtpsCheckbox, GroupDTPS.getOverlay(),"groupDtpsOverlayEnabled","groupDtpsTop")
    if (settings.getBoolean("groupDtpsOverlayEnabled",false)) {
      dtpsCheckbox.setSelected(true)
      GroupDTPS.getOverlay().setX(settings.getDouble("groupDtpsTop_X",500))
      GroupDTPS.getOverlay().setY(settings.getDouble("groupDtpsTop_Y",500))
      GroupDTPS.getOverlay().show()
    }
    dtpsCheckbox
  }


}

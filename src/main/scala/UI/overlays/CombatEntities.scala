package UI.overlays

import Controller.Controller
import UI.Tiles
import UI.overlays.OverlayUtils.{background, initMovableVBox, setCheckboxAction}
import Utils.Config.settings
import scalafx.geometry.Pos
import scalafx.scene.Scene
import scalafx.scene.control.{CheckBox, ScrollPane}
import scalafx.scene.control.ScrollPane.ScrollBarPolicy
import scalafx.scene.layout.{StackPane, VBox}
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.Text
import scalafx.stage.{Stage, StageStyle}

object CombatEntities extends Overlay {

  def getOverlay() = entitiesInCombatOverlay

  override def clear(): Unit = entitiesInCombatPane.getChildren.clear()

  override def refresh(): Unit = {

    clear()

    /**
     * Update Entities in Combat Health
     */

    val sortedActorsByHealth = Controller.getCurrentCombat().getCombatActors()
      .filter(_.getActorType() != "Player")
      .filter(_.getActorType() != "Companion")
      .filter(_.getActor().getHealth().getCurrent() > 0)
      .sortWith(_.getActor().getHealth().getMax() > _.getActor().getHealth().getMax())

    for (actor <- sortedActorsByHealth) {
      val stacked = new StackPane()
      val text = new Text()
      val percentMaxFill: Int = ((actor.getActor().getHealth().getCurrent().toDouble / actor.getActor().getHealth().getMax()) * 350).toInt
      val percentMax: Double = ((actor.getActor().getHealth().getCurrent().toDouble / actor.getActor().getHealth().getMax()) * 100).toInt
      text.setText(actor.getActor().getName() + ": " + " (" + percentMax + "%)")
      val rect = Rectangle(percentMaxFill,30)
      val backgroundRect = Rectangle(600, 30)
      backgroundRect.setStyle("-fx-fill: #FF908D; -fx-stroke: black; -fx-stroke-width: 2;")
      rect.setStyle("-fx-fill: #FF3633; -fx-stroke: black; -fx-stroke-width: 2;")

      // TODO: Make Boss vs Adds Different Colors
      //      if(actor.getActorType() == "Player"){
      //        rect.setStyle("-fx-fill: #5CFF47; -fx-stroke: black; -fx-stroke-width: 2;")
      //      }
      //      else if (actor.getActorType() == "Companion") {
      //        rect.setStyle("-fx-fill: #B93DFF; -fx-stroke: black; -fx-stroke-width: 2;")
      //      }
      //      else {
      //        rect.setStyle("-fx-fill: #4C3DFF; -fx-stroke: black; -fx-stroke-width: 2;")
      //      }
      stacked.getChildren.addAll(backgroundRect,rect,text)
      stacked.setAlignment(Pos.CenterLeft)
      entitiesInCombatPane.getChildren.add(stacked)
    }
  }


  /**
   * Non Player Entities in Combat
   */
  val entitiesInCombatOuter = new VBox() //holds top and scroll pane
  entitiesInCombatOuter.setBackground(Tiles.background)
  val entitiesInCombatPane = new VBox() // goes in scrollpane with health bars
  val entitiesInCombatTop = OverlayUtils.createMovableTop()
  entitiesInCombatTop.setId("entitiesInCombatTop")
  val entitiesInCombatScrollPane = new ScrollPane()
  entitiesInCombatScrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
  entitiesInCombatScrollPane.setBackground(Tiles.background)

  entitiesInCombatOuter.getChildren.addAll(entitiesInCombatTop,entitiesInCombatScrollPane)

  entitiesInCombatPane.setBackground(background)
  entitiesInCombatPane.setPrefSize(350,150)
  val entitiesInCombatOverlay = new Stage()
  entitiesInCombatOverlay.initStyle(StageStyle.Undecorated)
  entitiesInCombatScrollPane.setContent(entitiesInCombatPane)
  val entitiesInCombatOverlayScene = new Scene(entitiesInCombatOuter)
  entitiesInCombatOverlay.setTitle("Entities In Combat")
  entitiesInCombatOverlay.setAlwaysOnTop(true)
  entitiesInCombatOverlay.setScene(entitiesInCombatOverlayScene)


  initMovableVBox(entitiesInCombatTop,entitiesInCombatOverlay,5)

  override def createSettingsCheckbox(): CheckBox = {
    val combatEntitiesCheckbox = new CheckBox("Combat Entities")
    setCheckboxAction(combatEntitiesCheckbox, CombatEntities.getOverlay(),"combatEntitiesOverlayEnabled","entitiesInCombatTop")
    if (settings.getBoolean("combatEntitiesOverlayEnabled",false)) {
      combatEntitiesCheckbox.setSelected(true)
      CombatEntities.getOverlay().setX(settings.getDouble("entitiesInCombatTop_X",500))
      CombatEntities.getOverlay().setY(settings.getDouble("entitiesInCombatTop_Y",500))
      CombatEntities.getOverlay().show()
    }
    combatEntitiesCheckbox
  }


}

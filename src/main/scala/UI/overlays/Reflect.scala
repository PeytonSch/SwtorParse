package UI.overlays

import Controller.Controller
import UI.Tiles
import UI.overlays.OverlayUtils.{background, createMovableTop, initMovableVBox, setCheckboxAction}
import Utils.Config.settings
import scalafx.geometry.Pos
import scalafx.scene.Scene
import scalafx.scene.control.{CheckBox, ScrollPane}
import scalafx.scene.control.ScrollPane.ScrollBarPolicy
import scalafx.scene.layout.{StackPane, VBox}
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.Text
import scalafx.stage.{Stage, StageStyle}

object Reflect extends Overlay {

  override def getOverlay(): Stage = reflectDamageOverlay

  override def clear(): Unit = reflectDamagePane.getChildren.clear()

  override def refresh(): Unit = {

    clear()
    /**
     * Update Reflect Leaderboard
     */

    var maxReflectDamage = 1
    var totalReflectDamage = 1
    for (actor <- Controller.getCurrentCombat().getCombatActors()) {
      totalReflectDamage = totalReflectDamage + actor.reflectDamage
      if (actor.reflectDamage > maxReflectDamage && actor.getActorType() == "Player") maxReflectDamage = actor.reflectDamage
    }
    if(totalReflectDamage > 1) totalReflectDamage = totalReflectDamage - 1

    val sortedByReflectDamageDone = Controller.getCurrentCombat().getCombatActors().sortWith(_.reflectDamage > _.reflectDamage).filter(_.reflectDamage > 0)

    for (actor <- sortedByReflectDamageDone) {
      val stacked = new StackPane()
      val text = new Text()
      val percentMaxFill: Int = (actor.reflectDamage.toDouble / maxReflectDamage * 200).toInt
      val percentMax: Double = ((actor.reflectDamage.toDouble / totalReflectDamage) * 100).toInt
      text.setText(actor.getActor().getName() + ": " + actor.reflectDamage + " (" + percentMax + "%)")
      val rect = Rectangle(percentMaxFill,30)
      val backgroundRect = Rectangle(600, 30)
      backgroundRect.setStyle("-fx-fill: #FFBE55; -fx-stroke: black; -fx-stroke-width: 2;")
      rect.setStyle("-fx-fill: #FF8900; -fx-stroke: black; -fx-stroke-width: 2;")
      stacked.getChildren.addAll(backgroundRect,rect,text)
      stacked.setAlignment(Pos.CenterLeft)
      reflectDamagePane.getChildren.add(stacked)
    }
  }



  /**
   * Reflect Leaderboard
   */
  val reflectDamageOuter = new VBox() //holds top and scroll pane
  reflectDamageOuter.setBackground(Tiles.background)
  val reflectDamagePane = new VBox() // goes in scrollpane with health bars
  val reflectDamageTop = createMovableTop()
  reflectDamageTop.setId("reflectDamageTop")

  val reflectDamageScrollPane = new ScrollPane()
  reflectDamageScrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
  reflectDamageScrollPane.setBackground(Tiles.background)

  reflectDamageOuter.getChildren.addAll(reflectDamageTop,reflectDamageScrollPane)

  reflectDamagePane.setBackground(background)
  reflectDamagePane.setPrefSize(200,200)
  val reflectDamageOverlay = new Stage()
  reflectDamageOverlay.initStyle(StageStyle.Undecorated)
  reflectDamageScrollPane.setContent(reflectDamagePane)
  val reflectDamageOverlayScene = new Scene(reflectDamageOuter)
  reflectDamageOverlay.setTitle("Entities In Combat")
  reflectDamageOverlay.setAlwaysOnTop(true)
  reflectDamageOverlay.setScene(reflectDamageOverlayScene)

  initMovableVBox(reflectDamageTop,reflectDamageOverlay,6)

  override def createSettingsCheckbox(): CheckBox = {
    val reflectDamageCheckbox = new CheckBox("Reflect Leaderboard")
    setCheckboxAction(reflectDamageCheckbox, Reflect.getOverlay(),"reflectOverlayEnabled","reflectDamageTop")
    if (settings.getBoolean("reflectOverlayEnabled",false)) {
      reflectDamageCheckbox.setSelected(true)
      Reflect.getOverlay().setX(settings.getDouble("reflectDamageTop_X",500))
      Reflect.getOverlay().setY(settings.getDouble("reflectDamageTop_Y",500))
      Reflect.getOverlay().show()
    }

    reflectDamageCheckbox
  }

}

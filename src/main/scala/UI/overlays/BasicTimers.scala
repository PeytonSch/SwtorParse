package UI.overlays

import Controller.Controller
import UI.{Tiles, UIStyle}
import UI.overlays.OverlayUtils.{createMovableTop, createMovableTopWithToggles, initMovableVBox, setCheckboxAction}
import UI.timers.ActiveTimers
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

object BasicTimers extends Overlay {

  override def getOverlay(): Stage = basicTimerOverlay

  override def clear(): Unit = basicTimerPane.getChildren.clear()

  override def refresh(): Unit = {

    clear()
    /**
     * Update Overlay
     */
    val runningTimers = ActiveTimers.getActiveTimers
    val now = System.nanoTime()
    for (timer <- runningTimers) {
      val timeRemainingFull = timer.getCooldown - ((now - timer.getTriggeredAt) / 1000000000.toDouble)
      val timeRemaining = BigDecimal(timeRemainingFull).setScale(1, BigDecimal.RoundingMode.HALF_UP).toDouble
      if (timeRemaining < 0) ActiveTimers.deactivatTimer(timer)
      val stacked = new StackPane()
      val text = new Text()
      val percentMaxFill: Int = ((timeRemaining / timer.getCooldown)  * 200).toInt
      text.setText(timer.getName + ": " + timeRemaining)
      val rect = Rectangle(percentMaxFill,30)
      val backgroundRect = Rectangle(600, 30)
      rect.setStyle("-fx-fill: #A723FF; -fx-stroke: black; -fx-stroke-width: 2;")
      backgroundRect.setStyle("-fx-fill: #8B12FF; -fx-stroke: black; -fx-stroke-width: 2;")
      stacked.getChildren.addAll(backgroundRect,rect,text)
      stacked.setAlignment(Pos.CenterLeft)
      stacked.setStyle("-fx-background-color: rgba(0,255,0,0)")
      basicTimerPane.getChildren.add(stacked)
    }
  }

  /**
   * Basic Timer Overlay
   */
  val basicTimerOuter = new VBox()
  basicTimerOuter.setBackground(UIStyle.background)
  val basicTimerPane = new VBox()
  val basicTimerTop = createMovableTop()
  basicTimerTop.setId("basicTimerTop")
  val basicTimerScrollPane = new ScrollPane()
  basicTimerScrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
  basicTimerScrollPane.setBackground(UIStyle.background)

  basicTimerOuter.getChildren.addAll(basicTimerTop,basicTimerScrollPane)

  basicTimerPane.setBackground(UIStyle.background)
  basicTimerPane.setPrefSize(200,200)
  val basicTimerOverlay = new Stage()
  basicTimerOverlay.initStyle(StageStyle.Undecorated)
  basicTimerScrollPane.setContent(basicTimerPane)
  val basicTimerOverlayScene = new Scene(basicTimerOuter)
  basicTimerOverlay.setTitle("Timers")
  basicTimerOverlay.setAlwaysOnTop(true)
  basicTimerOverlay.setScene(basicTimerOverlayScene)

  initMovableVBox(basicTimerTop,basicTimerOverlay,8)


  override def createSettingsCheckbox(): CheckBox = {
    val timerCheckbox = new CheckBox("Basic Timers")
    setCheckboxAction(timerCheckbox, BasicTimers.getOverlay(),"basicTimerOverlayEnabled","basicTimerTop")
    if (settings.getBoolean("basicTimerOverlayEnabled",false)) {
      timerCheckbox.setSelected(true)
      BasicTimers.getOverlay().setX(settings.getDouble("basicTimerTop_X",500))
      BasicTimers.getOverlay().setY(settings.getDouble("basicTimerTop_Y",500))
      BasicTimers.getOverlay().show()
    }
    timerCheckbox
  }


}

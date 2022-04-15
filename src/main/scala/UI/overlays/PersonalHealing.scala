package UI.overlays

import Controller.Controller
import UI.UICodeConfig
import UI.overlays.OverlayUtils.{createMovableTop, initMovableVBox, setCheckboxAction}
import Utils.Config.settings
import eu.hansolo.tilesfx.Tile.SkinType
import eu.hansolo.tilesfx.TileBuilder
import eu.hansolo.tilesfx.chart.ChartData
import scalafx.scene.Scene
import scalafx.scene.control.CheckBox
import scalafx.scene.layout.VBox
import scalafx.stage.{Stage, StageStyle}

import UI.UIStyle._


object PersonalHealing extends Overlay {


  override def getOverlay(): Stage = personalHpsOverlay

  override def clear(): Unit = personalHealingOverlay.clearChartData()

  override def refresh(): Unit = {

    clear()
    /**
     * Update Overlay Your Healing Done
     */

    personalHealingOverlay.setTitle(s"Hps: ${Controller.getCurrentCombat().getPlayerInCombatActor().getHealingDonePerSecond()}")
    for (healingTypeDone <- Controller.getCurrentCombat().getPlayerInCombatActor().getHealingDoneStats()) {
      for (healSource <- healingTypeDone._2.keys) {
        val healValue = Controller.getCurrentCombat().getPlayerInCombatActor().getHealingDoneStats().get("").get(healSource)
        personalHealingOverlay.addChartData(new ChartData(healSource,healValue,UICodeConfig.randomColor()))
      }
    }
  }


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
  personalHealingTop.setId("personalHealingTop")
  personalHealingPane.setBackground(background)
  personalHealingPane.getChildren.addAll(personalHealingTop,personalHealingOverlay)
  personalHealingPane.setPrefSize(200,200)
  val personalHpsOverlay = new Stage()
  personalHpsOverlay.initStyle(StageStyle.Undecorated)
  val hpsOverlayScene = new Scene(personalHealingPane)
  personalHpsOverlay.setTitle("Healing Done")
  personalHpsOverlay.setAlwaysOnTop(true)
  personalHpsOverlay.setScene(hpsOverlayScene)

  initMovableVBox(personalHealingTop,personalHpsOverlay,4)

  override def createSettingsCheckbox(): CheckBox = {
    val personalHpsCheckbox = new CheckBox("Personal Healing Done")
    setCheckboxAction(personalHpsCheckbox, PersonalHealing.getOverlay(),"personalHpsOverlayEnabled","personalHealingTop")
    if (settings.getBoolean("personalHpsOverlayEnabled",false)) {
      personalHpsCheckbox.setSelected(true)
      PersonalHealing.getOverlay().setX(settings.getDouble("personalHealingTop_X",500))
      PersonalHealing.getOverlay().setY(settings.getDouble("personalHealingTop_Y",500))
      PersonalHealing.getOverlay().show()
    }
    personalHpsCheckbox
  }

}

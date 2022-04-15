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


object PersonalDamage extends Overlay {

  override def getOverlay(): Stage = personalDpsOverlay

  override def clear(): Unit = personalDamageOverlay.clearChartData()

  override def refresh(): Unit = {

    clear()
    /**
     * Update Overlay Your Damage Done
     */

    personalDamageOverlay.setTitle(s"Dps: ${Controller.getCurrentCombat().getPlayerInCombatActor().getDamagePerSecond()}")
    for (damageTypeDone <- Controller.getCurrentCombat().getPlayerInCombatActor().getDamageDone1DStats()) {
      for (damageSource <- damageTypeDone._2.keys) {
        val value = Controller.getCurrentCombat().getPlayerInCombatActor().getDamageDone1DStats().get("").get(damageSource)
        personalDamageOverlay.addChartData(new ChartData(damageSource,value,UICodeConfig.randomColor()))
      }
    }
    //    Overlays.personalDamageOverlay.clearChartData()
    //    Overlays.personalDamageOverlay.setTitle(s"DPS: ${Controller.getCurrentCombat().getPlayerInCombatActor().getDamagePerSecond()}")
    //    for (damageTypeDone <- Controller.getCurrentCombat().getPlayerInCombatActor().getDamageTypeDone()) {
    //      damageTypeDone._1 match {
    //        case "internal" => {
    //          Overlays.personalDamageOverlay.addChartData(new ChartData(damageTypeDone._1,damageTypeDone._2,uiCodeConfig.internalColor))        }
    //        case "kinetic" => {
    //          Overlays.personalDamageOverlay.addChartData(new ChartData(damageTypeDone._1,damageTypeDone._2,uiCodeConfig.kineticColor))
    //        }
    //        case "energy" => {
    //          Overlays.personalDamageOverlay.addChartData(new ChartData(damageTypeDone._1,damageTypeDone._2,uiCodeConfig.energyColor))
    //        }
    //        case "elemental" => {
    //          Overlays.personalDamageOverlay.addChartData(new ChartData(damageTypeDone._1,damageTypeDone._2,uiCodeConfig.elementalColor))
    //        }
    //        case "No Type" =>
    //        case x => {
    //          println(s"Got Unknown Damage type: ${x}")
    //          Overlays.personalDamageOverlay.addChartData(new ChartData(damageTypeDone._1,damageTypeDone._2,uiCodeConfig.internalColor))
    //        }
    //      }
    //    }
  }


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
  personalDamageTop.setId("personalDamageTop")
  personalDamagePane.setBackground(background)
  val personalDpsOverlay = new Stage()
  personalDamagePane.getChildren.addAll(personalDamageTop,personalDamageOverlay)
  personalDamagePane.setPrefSize(200,200)
  personalDpsOverlay.initStyle(StageStyle.Undecorated)
  val dpsOverlayScene = new Scene(personalDamagePane)
  personalDpsOverlay.setTitle("Damage Done")
  personalDpsOverlay.setAlwaysOnTop(true)
  personalDpsOverlay.setScene(dpsOverlayScene)

  initMovableVBox(personalDamageTop,personalDpsOverlay,3)

  override def createSettingsCheckbox(): CheckBox = {
    val personalDpsCheckbox = new CheckBox("Personal Damage Done")
    setCheckboxAction(personalDpsCheckbox, PersonalDamage.getOverlay(),"personalDpsOverlayEnabled","personalDamageTop")
    if (settings.getBoolean("personalDpsOverlayEnabled",false)) {
      personalDpsCheckbox.setSelected(true)
      PersonalDamage.getOverlay().setX(settings.getDouble("personalDamageTop_X",500))
      PersonalDamage.getOverlay().setY(settings.getDouble("personalDamageTop_Y",500))
      PersonalDamage.getOverlay().show()
    }
    personalDpsCheckbox
  }

}

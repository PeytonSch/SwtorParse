package UI.overlays

import Controller.Controller
import UI.UICodeConfig
import UI.overlays.OverlayUtils.{background, createMovableTop, initMovableVBox, setCheckboxAction}
import Utils.Config.settings
import eu.hansolo.tilesfx.Tile.SkinType
import eu.hansolo.tilesfx.TileBuilder
import eu.hansolo.tilesfx.chart.ChartData
import scalafx.scene.Scene
import scalafx.scene.control.CheckBox
import scalafx.scene.layout.VBox
import scalafx.stage.{Stage, StageStyle}

object PersonalDamageTaken extends Overlay {

  override def getOverlay(): Stage = personalDtpsOverlay

  override def clear(): Unit = personalDamageTakenOverlay.clearChartData()

  override def refresh(): Unit = {

    clear()
    /**
     * Update Overlay Your Damage Taken
     */

    personalDamageTakenOverlay.setTitle(s"Dtps: ${Controller.getCurrentCombat().getPlayerInCombatActor().getDamageTakenPerSecond()}")
    for (damageTypeTaken <- Controller.getCurrentCombat().getPlayerInCombatActor().getDamageTaken1DStats()) {
      for (damageSource <- damageTypeTaken._2.keys) {
        val value = Controller.getCurrentCombat().getPlayerInCombatActor().getDamageTaken1DStats().get("").get(damageSource)
        personalDamageTakenOverlay.addChartData(new ChartData(damageSource,value,UICodeConfig.randomColor()))
      }
    }
  }



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
  personalDamageTakenTop.setId("personalDamageTakenTop")
  personalDamageTakenPane.setBackground(background)
  personalDamageTakenPane.getChildren.addAll(personalDamageTakenTop,personalDamageTakenOverlay)
  personalDamageTakenPane.setPrefSize(200,200)
  val personalDtpsOverlay = new Stage()
  personalDtpsOverlay.initStyle(StageStyle.Undecorated)
  val dtpsOverlayScene = new Scene(personalDamageTakenPane)
  personalDtpsOverlay.setTitle("Damage Taken")
  personalDtpsOverlay.setAlwaysOnTop(true)
  personalDtpsOverlay.setScene(dtpsOverlayScene)

  initMovableVBox(personalDamageTakenTop,personalDtpsOverlay,2)

  override def createSettingsCheckbox(): CheckBox = {
    val personalDtpsCheckbox = new CheckBox("Personal Damage Taken")
    setCheckboxAction(personalDtpsCheckbox, PersonalDamageTaken.getOverlay(),"personalDtpsOverlayEnabled","personalDamageTakenTop")
    if (settings.getBoolean("personalDtpsOverlayEnabled",false)) {
      personalDtpsCheckbox.setSelected(true)
      PersonalDamageTaken.getOverlay().setX(settings.getDouble("personalDamageTakenTop_X",500))
      PersonalDamageTaken.getOverlay().setY(settings.getDouble("personalDamageTakenTop_Y",500))
      PersonalDamageTaken.getOverlay().show()
    }
    personalDtpsCheckbox
  }

}

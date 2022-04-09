package UI.tabs

import UI.overlays.Overlays
import UI.overlays.Overlays.{groupDamageOuter, groupDamagePane, groupDamageScrollPane}
import Utils.Config.settings
import javafx.collections.FXCollections
import logger.Logger
import scalafx.event.ActionEvent
import scalafx.scene.control.{Button, CheckBox, ComboBox, Label, Slider, TextField}
import scalafx.scene.layout.{GridPane, HBox, StackPane, VBox}
import scalafx.Includes._
import scalafx.stage.Stage

object Settings extends UITab {

  // This can be though of as like a layout
  val pane = new GridPane()

  pane.setGridLinesVisible(true)

  val parent = new HBox()

  val boarderStyle = "-fx-border-color: #C4BFAE;\n" +
                      "-fx-border-insets: 5;\n" +
                      "-fx-border-width: 3;\n" +
                      "-fx-border-style: solid;\n"

  val left = new VBox()
  left.setStyle(boarderStyle)
  left.setPrefSize(800,1000)
  val right = new VBox()
  right.setStyle(boarderStyle)
  right.setPrefSize(800,1000)

  parent.getChildren.addAll(left,right)

  pane.add(parent,0,0)


  override def addToUI(): GridPane = pane


  /**
   * Interface Checkboxes
   */

  val dpsCheckbox = new CheckBox("Group Damage")
  setCheckboxAction(dpsCheckbox, Overlays.groupDpsOverlay,"groupDpsOverlayEnabled","groupDamageTop")
  if (settings.getBoolean("groupDpsOverlayEnabled",false)) {
    dpsCheckbox.setSelected(true)
    Overlays.groupDpsOverlay.setX(settings.getDouble("groupDamageTop_X",500))
    Overlays.groupDpsOverlay.setY(settings.getDouble("groupDamageTop_Y",500))
    Overlays.groupDpsOverlay.show()
  }

  val hpsCheckbox = new CheckBox("Group Healing")
  setCheckboxAction(hpsCheckbox, Overlays.groupHpsOverlay,"groupHpsOverlayEnabled","groupHealingTop")
  if (settings.getBoolean("groupHpsOverlayEnabled",false)) {
    hpsCheckbox.setSelected(true)
    Overlays.groupHpsOverlay.setX(settings.getDouble("groupHealingTop_X",500))
    Overlays.groupHpsOverlay.setY(settings.getDouble("groupHealingTop_Y",500))
    Overlays.groupHpsOverlay.show()
  }

  val personalDpsCheckbox = new CheckBox("Personal Damage Done")
  setCheckboxAction(personalDpsCheckbox, Overlays.personalDpsOverlay,"personalDpsOverlayEnabled","personalDamageTop")
  if (settings.getBoolean("personalDpsOverlayEnabled",false)) {
    personalDpsCheckbox.setSelected(true)
    Overlays.personalDpsOverlay.setX(settings.getDouble("personalDamageTop_X",500))
    Overlays.personalDpsOverlay.setY(settings.getDouble("personalDamageTop_Y",500))
    Overlays.personalDpsOverlay.show()
  }

  val personalHpsCheckbox = new CheckBox("Personal Healing Done")
  setCheckboxAction(personalHpsCheckbox, Overlays.personalHpsOverlay,"personalHpsOverlayEnabled","personalHealingTop")
  if (settings.getBoolean("personalHpsOverlayEnabled",false)) {
    personalHpsCheckbox.setSelected(true)
    Overlays.personalHpsOverlay.setX(settings.getDouble("personalHealingTop_X",500))
    Overlays.personalHpsOverlay.setY(settings.getDouble("personalHealingTop_Y",500))
    Overlays.personalHpsOverlay.show()
  }

  val personalDtpsCheckbox = new CheckBox("Personal Damage Taken")
  setCheckboxAction(personalDtpsCheckbox, Overlays.personalDtpsOverlay,"personalDtpsOverlayEnabled","personalDamageTakenTop")
  if (settings.getBoolean("personalDtpsOverlayEnabled",false)) {
    personalDtpsCheckbox.setSelected(true)
    Overlays.personalDtpsOverlay.setX(settings.getDouble("personalDamageTakenTop_X",500))
    Overlays.personalDtpsOverlay.setY(settings.getDouble("personalDamageTakenTop_Y",500))
    Overlays.personalDtpsOverlay.show()
  }

  val combatEntitiesCheckbox = new CheckBox("Combat Entities")
  setCheckboxAction(combatEntitiesCheckbox, Overlays.entitiesInCombatOverlay,"combatEntitiesOverlayEnabled","entitiesInCombatTop")
  if (settings.getBoolean("combatEntitiesOverlayEnabled",false)) {
    combatEntitiesCheckbox.setSelected(true)
    Overlays.entitiesInCombatOverlay.setX(settings.getDouble("entitiesInCombatTop_X",500))
    Overlays.entitiesInCombatOverlay.setY(settings.getDouble("entitiesInCombatTop_Y",500))
    Overlays.entitiesInCombatOverlay.show()
  }

  val reflectDamageCheckbox = new CheckBox("Reflect Leaderboard")
  setCheckboxAction(reflectDamageCheckbox, Overlays.reflectDamageOverlay,"reflectOverlayEnabled","reflectDamageTop")
  if (settings.getBoolean("reflectOverlayEnabled",false)) {
    reflectDamageCheckbox.setSelected(true)
    Overlays.reflectDamageOverlay.setX(settings.getDouble("reflectDamageTop_X",500))
    Overlays.reflectDamageOverlay.setY(settings.getDouble("reflectDamageTop_Y",500))
    Overlays.reflectDamageOverlay.show()
  }

  val overlayLabel = new Label("OVERLAYS")
  overlayLabel.setStyle("-fx-font-size: 20;")

  left.getChildren.addAll(
    overlayLabel,
    dpsCheckbox,
    hpsCheckbox,
    personalDpsCheckbox,
    personalHpsCheckbox,
    personalDtpsCheckbox,
    combatEntitiesCheckbox,
    reflectDamageCheckbox
  )


  /**
   * Checkbox actions
   */

  def setCheckboxAction(c:CheckBox, overlay: Stage, settingName: String,posSetting: String): Unit = {
    c.onAction = (event: ActionEvent) => {
      if (c.selectedProperty().value == true) {
        overlay.setX(settings.getDouble(posSetting+"_X",500))
        overlay.setY(settings.getDouble(posSetting+"_Y",500))
        overlay.show()
        settings.putBoolean(settingName,true)
      }
      else {
        overlay.hide()
        settings.putBoolean(settingName,false)
      }
    }
  }

  /**
   * Right Settings
   */
  val guildLabel = new Label("Guild:")
  val guildTextField = new TextField()
  guildTextField.promptText = settings.get("guild","Guild")
  guildTextField.setText(settings.get("guild",""))
  val raidTeamLabel = new Label("Raid Team:")
  val raidTeamTextField = new TextField()
  raidTeamTextField.promptText = settings.get("raidTeam","Raid Team Name")
  raidTeamTextField.setText(settings.get("raidTeam",""))
  val serverLabel = new Label("Server:")
  val serverDropDown = new ComboBox(Seq[String]("Satele Shan","Star Forge","Tulak Hord","Darth Malgus","The Leviathan"))

  val logDirLabel = new Label("Configured Log Directory")
  val logDirTextField = new TextField()
  logDirTextField.setEditable(false)
  logDirTextField.setText(settings.get("logDirectory","Log Directory Not Set: Use File -> Choose Log Dir"))

  serverDropDown.setValue((settings.get("server","Select Server")))

  right.getChildren.addAll(
    guildLabel,guildTextField,
    raidTeamLabel, raidTeamTextField,
    serverLabel, serverDropDown,
    logDirLabel, logDirTextField
  )


  val overlayOpacity = new Slider()
  overlayOpacity.setMin(0)
  overlayOpacity.setMax(1)
  overlayOpacity.setValue(settings.getDouble("overlayOpacity",1))
  val overLayOpacityLabel = new Label(s"Overlay Opacity: ${(overlayOpacity.value.value * 100).toInt} %")

  right.getChildren.addAll(overLayOpacityLabel,overlayOpacity)

  // Path delimiter setting

  val delimiterDropDown = new ComboBox(Seq[String]("/ (mac, linux?)","\\ (windows)"))

  delimiterDropDown.setValue((settings.get("pathDelimiter","Select Path Delimiter")))

  right.getChildren.addAll(delimiterDropDown)



  /**
   * Save with apply button
   */

  val applyButton = new Button("Apply")

  right.getChildren.addAll(applyButton)

  applyButton.onAction = (event: ActionEvent) => {
    // Save Settings
    val guild = guildTextField.getText
    val raidTeam = raidTeamTextField.getText
    val server = serverDropDown.getValue
    val delimiter = delimiterDropDown.getValue
    if (guild != null) settings.put("guild", guild)
    if (raidTeam != null) settings.put("raidTeam",raidTeam)
    if (server != null) settings.put("server", server)
    if (delimiter != null) {
      if (delimiter == "/ (mac, linux?)") {
        settings.put("pathDelimiter", "/")
      } else if (delimiter == "\\ (windows)") {
        settings.put("pathDelimiter", "\\")
      }
    }

    Logger.highlight(s"Saved Data: ${guild}, ${raidTeam}, ${server}, ${delimiter} ")
  }


  /**
   * Overlay Opacity Slider On Slide
   */

  overlayOpacity.valueProperty.addListener{ (o: javafx.beans.value.ObservableValue[_ <: Number], oldVal: Number, newVal: Number) =>

    overLayOpacityLabel.text = s"Overlay Opacity: ${(newVal.doubleValue() * 100).toInt} %"
    settings.putDouble("overlayOpacity",newVal.doubleValue())

//    Logger.highlight(s"Opacity: ${newVal.doubleValue()}")
    Overlays.groupDamageScrollPane.setOpacity(newVal.doubleValue())
    Overlays.groupHealingScrollPane.setOpacity(newVal.doubleValue())


//    groupDamageOuter.setStyle(s"-fx-background-color: rgba(104,103,103,${newVal.doubleValue()})")
//    groupDamagePane.setStyle(s"-fx-background-color: rgba(104,103,103,${newVal.doubleValue()})")
//    groupDamageScrollPane.setStyle(s"-fx-background-color: rgba(104,103,103,${newVal.doubleValue()})")
//
//    for (child <- groupDamagePane.getChildren.toSeq) {
//      for (littleChild <- child.asInstanceOf[javafx.scene.layout.StackPane].getChildren.toSeq) {
//        Logger.highlight(s"${littleChild}")
//        littleChild.setStyle("-fx-background-color: rgba(104,255,103,1)")
//      }
//    }



    //    Overlays.groupDamagePane.setOpacity(newVal.doubleValue())
//    Overlays.groupDamageOuter.setOpacity(newVal.doubleValue())
//    Overlays.groupDamagePane.setStyle(s"-fx-background-color: rgba(0,0,255,${newVal.doubleValue()})")
//    Overlays.groupHealingPane.setStyle(s"-fx-background-color: rgba(0,0,255,${newVal.doubleValue()})")
  }






}

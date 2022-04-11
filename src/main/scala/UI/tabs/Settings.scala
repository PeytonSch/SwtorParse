package UI.tabs

import UI.overlays.{CombatEntities, GroupDTPS, GroupDamage, GroupHealing, PersonalDamage, PersonalDamageTaken, PersonalHealing, Reflect}
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


  val overlayLabel = new Label("OVERLAYS")
  overlayLabel.setStyle("-fx-font-size: 20;")

  /**
   * Add Overlay Checkboxes
   */

  left.getChildren.addAll(
    overlayLabel,
    GroupDamage.createSettingsCheckbox(),
    GroupHealing.createSettingsCheckbox(),
    GroupDTPS.createSettingsCheckbox(),
    PersonalDamage.createSettingsCheckbox(),
    PersonalHealing.createSettingsCheckbox(),
    PersonalDamageTaken.createSettingsCheckbox(),
    CombatEntities.createSettingsCheckbox(),
    Reflect.createSettingsCheckbox()
  )




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
    GroupDamage.groupDamageScrollPane.setOpacity(newVal.doubleValue())
    GroupHealing.groupHealingScrollPane.setOpacity(newVal.doubleValue())


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

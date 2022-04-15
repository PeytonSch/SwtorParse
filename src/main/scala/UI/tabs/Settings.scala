package UI.tabs

import UI.UIStyle
import UI.overlays.{BasicTimers, CombatEntities, GroupDTPS, GroupDamage, GroupHealing, PersonalDamage, PersonalDamageTaken, PersonalHealing, Reflect}
import Utils.Config.settings
import javafx.collections.FXCollections
import logger.Logger
import scalafx.event.ActionEvent
import scalafx.scene.control.{Button, CheckBox, ComboBox, Label, Slider, TextField}
import scalafx.scene.layout.{GridPane, HBox, Priority, StackPane, VBox}
import scalafx.Includes._
import scalafx.geometry.Pos
import scalafx.stage.Stage

import scala.reflect.internal.util.Position

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
  overlayLabel.setStyle(UIStyle.extraLargeLightLabel)

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
    Reflect.createSettingsCheckbox(),
    BasicTimers.createSettingsCheckbox()
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

  // Set Styles
  // make it easier to adjust these
  val labelStyle = UIStyle.largeLightLabel
  val textFieldStyle = UIStyle.textFieldStyle
  guildLabel.setStyle(labelStyle)
  raidTeamLabel.setStyle(labelStyle)
  serverLabel.setStyle(labelStyle)
  logDirLabel.setStyle(labelStyle)

  guildTextField.setStyle(textFieldStyle)
  raidTeamTextField.setStyle(textFieldStyle)
  logDirTextField.setStyle(textFieldStyle)

  serverDropDown.setStyle(textFieldStyle)

  // text field width based off of loaded log dir
  val rightWidth = 450

  guildTextField.setPrefWidth(rightWidth)
  raidTeamTextField.setPrefWidth(rightWidth)
  logDirTextField.setPrefWidth(rightWidth)
  serverDropDown.setPrefWidth(rightWidth)

  // Create HBoxes
  val guildHbox = new HBox{
    children = Seq(guildLabel,UIStyle.createSpacer(),guildTextField)
    style = UIStyle.mainBackgroundObject
    hgrow = Priority.Always
    alignment = Pos.BaselineCenter
  }
  val raidTeamHbox = new HBox{
    children = Seq(raidTeamLabel,UIStyle.createSpacer(), raidTeamTextField)
    style = UIStyle.mainBackgroundObject
    hgrow = Priority.Always
    alignment = Pos.BaselineCenter
  }
  val serverHbox = new HBox{
    children = Seq(serverLabel,UIStyle.createSpacer(), serverDropDown)
    style = UIStyle.mainBackgroundObject
    hgrow = Priority.Always
    alignment = Pos.BaselineCenter
  }
  val logDirHbox = new HBox{
    children = Seq(logDirLabel,UIStyle.createSpacer(), logDirTextField)
    style = UIStyle.mainBackgroundObject
    hgrow = Priority.Always
    alignment = Pos.BaselineCenter
  }

  right.getChildren.addAll(
    guildHbox, raidTeamHbox,
    serverHbox, logDirHbox
  )

  val overlayOpacity = new Slider()
  overlayOpacity.setMin(0)
  overlayOpacity.setMax(1)
  overlayOpacity.setValue(settings.getDouble("overlayOpacity",1))
  val overLayOpacityLabel = new Label(s"Overlay Opacity: ${(overlayOpacity.value.value * 100).toInt} %")

  overLayOpacityLabel.setStyle(labelStyle)
  overlayOpacity.setPrefWidth(rightWidth)

  val opacityHbox = new HBox{
    children = Seq(overLayOpacityLabel,UIStyle.createSpacer(), overlayOpacity)
    style = UIStyle.mainBackgroundObject
    hgrow = Priority.Always
    alignment = Pos.BaselineCenter
  }

  right.getChildren.addAll(opacityHbox)

  // Path delimiter setting

  val delimiterLabel = new Label("Path Delimiter")

  delimiterLabel.setStyle(labelStyle)

  val delimiterDropDown = new ComboBox(Seq[String]("/ (mac, linux?)","\\ (windows)"))

  delimiterDropDown.setStyle(textFieldStyle)
  delimiterDropDown.setPrefWidth(rightWidth)
  delimiterDropDown.setValue((settings.get("pathDelimiter","Select Path Delimiter")))

  val delimiterHbox = new HBox{
    children = Seq(delimiterLabel,UIStyle.createSpacer(), delimiterDropDown)
    style = UIStyle.mainBackgroundObject
    hgrow = Priority.Always
    alignment = Pos.BaselineCenter
  }

  right.getChildren.addAll(delimiterHbox)



  /**
   * Save with apply button
   */

  val applyButton = new Button("Apply")
  applyButton.setStyle(UIStyle.uiButtonStyle)
  UIStyle.setHoverable(applyButton,UIStyle.uiButtonHoverStyle)

  val applyHbox = new HBox{
    children = Seq(UIStyle.createSpacer(),applyButton)
  }

  right.getChildren.addAll(applyHbox)

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

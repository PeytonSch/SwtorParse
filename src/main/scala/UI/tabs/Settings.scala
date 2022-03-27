package UI.tabs

import UI.overlays.Overlays
import logger.Logger
import scalafx.event.ActionEvent
import scalafx.scene.control.{CheckBox, Label}
import scalafx.scene.layout.{GridPane, HBox, VBox}
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
  left.setPrefSize(500,1000)
  val right = new VBox()
  right.setStyle(boarderStyle)

  parent.getChildren.addAll(left,right)

  pane.add(parent,0,0)


  override def addToUI(): GridPane = pane


  /**
   * Interface Checkboxes
   */

  val dpsCheckbox = new CheckBox("Group Damage")
  setCheckboxAction(dpsCheckbox, Overlays.groupDpsOverlay)
  val hpsCheckbox = new CheckBox("Group Healing")
  setCheckboxAction(hpsCheckbox, Overlays.groupHpsOverlay)
  val personalDpsCheckbox = new CheckBox("Personal Damage Done")
  setCheckboxAction(personalDpsCheckbox, Overlays.personalDpsOverlay)
  val personalHpsCheckbox = new CheckBox("Personal Healing Done")
  setCheckboxAction(personalHpsCheckbox, Overlays.personalHpsOverlay)
  val personalDtpsCheckbox = new CheckBox("Personal Damage Taken")
  setCheckboxAction(personalDtpsCheckbox, Overlays.personalDtpsOverlay)
  val combatEntitiesCheckbox = new CheckBox("Combat Entities")
  setCheckboxAction(combatEntitiesCheckbox, Overlays.entitiesInCombatOverlay)

  val overlayLabel = new Label("OVERLAYS")
  overlayLabel.setStyle("-fx-font-size: 20;")

  left.getChildren.addAll(
    overlayLabel,
    dpsCheckbox,
    hpsCheckbox,
    personalDpsCheckbox,
    personalHpsCheckbox,
    personalDtpsCheckbox,
    combatEntitiesCheckbox
  )


  /**
   * Checkbox actions
   */

  def setCheckboxAction(c:CheckBox, overlay: Stage): Unit = {
    c.onAction = (event: ActionEvent) => {
      if (c.selectedProperty().value == true) overlay.show()
      else overlay.hide()
    }
  }





}

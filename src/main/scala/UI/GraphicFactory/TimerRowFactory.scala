package UI.GraphicFactory

import UI.{UICodeConfig, UIStyle}
import UI.timers.Timer
import logger.Logger
import scalafx.scene.control.{CheckBox, Label}
import scalafx.scene.layout.HBox
import scalafx.event.ActionEvent
import scalafx.geometry.Pos
import scalafx.scene.shape.Rectangle


/**
 * Create nice timer rows to display timers as
 */
object TimerRowFactory {

//  val hboxStyle: String =
//    """
//      |-fx-background-color: #2f4f4f;
//      |-fx-padding: 15;
//      |-fx-spacing: 10;
//      |""".stripMargin
//
//  val textStyle: String =
//    """
//      |-fx-font-size: 22;
//      |-fx-padding: 3px;
//      |-fx-border-insets: 3px;
//      |-fx-background-insets: 3px;
//      |""".stripMargin

  /**
   * Create a row that displays a timer
   */
  def createRow(name: String,
                source: String,
                area: String,
                ability: String,
                cooldown: Double,
                color: String
               ): Timer = {

    // The basis of the row should be an hbox
    val base = new HBox()
    base.setStyle(UIStyle.insetBackgroundObject)
    base.setPrefWidth(UICodeConfig.existingTimerWidthHeight._1)

    // each row should have a checkbox to enable or disable the timer
    val enabledCheckBox = new CheckBox()
    enabledCheckBox.setStyle(UIStyle.mediumLightLabel)

    // each row should have a label for the timer name
    val timerName = new Label(name)
    timerName.setStyle(UIStyle.mediumLightLabel)

    // each row should have the name of the source
    val timerSource = new Label(source)
    timerSource.setStyle(UIStyle.mediumLightLabel)

    // each row should have the ability
    val timerAbility = new Label(ability)
    timerAbility.setStyle(UIStyle.mediumLightLabel)

    // each row should have the area the timer applies to
    val timerArea = new Label(area)
    timerArea.setStyle(UIStyle.mediumLightLabel)

    // each row should have the CD of the timer
    val cdArea = new Label(cooldown.toString)
    cdArea.setStyle(UIStyle.mediumLightLabel)

    val displayColor = new Rectangle{
      width = 30
      height = 30
      style = UIStyle.rectangleStyle(UIStyle.getHexColor(color))
    }

    // Some Styling
    base.onMouseEntered = event => {
      base.setStyle(UIStyle.mainBackgroundObject)
      timerName.setStyle(UIStyle.mediumLightLabel + UIStyle.textHoverLightBlue)
      timerSource.setStyle(UIStyle.mediumLightLabel + UIStyle.textHoverLightBlue)
      timerAbility.setStyle(UIStyle.mediumLightLabel + UIStyle.textHoverLightBlue)
      timerArea.setStyle(UIStyle.mediumLightLabel + UIStyle.textHoverLightBlue)
      cdArea.setStyle(UIStyle.mediumLightLabel + UIStyle.textHoverLightBlue)
    }
    base.onMouseExited = event => {
      base.setStyle(UIStyle.insetBackgroundObject)
      timerName.setStyle(UIStyle.mediumLightLabel)
      timerSource.setStyle(UIStyle.mediumLightLabel)
      timerAbility.setStyle(UIStyle.mediumLightLabel)
      timerArea.setStyle(UIStyle.mediumLightLabel)
      cdArea.setStyle(UIStyle.mediumLightLabel)
    }

    base.getChildren.addAll(
      enabledCheckBox,UIStyle.createSpacer(),
      timerName,UIStyle.createSpacer(),
      timerSource,UIStyle.createSpacer(),
      timerAbility,UIStyle.createSpacer(),
      timerArea,UIStyle.createSpacer(),
      cdArea,UIStyle.createSpacer(),
      displayColor, UIStyle.createSpacer(),
    )

    new Timer(
      base,
      enabledCheckBox,
      name,source,ability,area,
      cooldown, color
    )

  }

}



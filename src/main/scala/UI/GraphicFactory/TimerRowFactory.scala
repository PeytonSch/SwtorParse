package UI.GraphicFactory

import UI.UICodeConfig
import UI.timers.Timer
import logger.Logger
import scalafx.scene.control.{CheckBox, Label}
import scalafx.scene.layout.HBox
import scalafx.event.ActionEvent


/**
 * Create nice timer rows to display timers as
 */
object TimerRowFactory {

  val hboxStyle: String =
    """
      |-fx-background-color: #2f4f4f;
      |-fx-padding: 15;
      |-fx-spacing: 10;
      |""".stripMargin

  val textStyle: String =
    """
      |-fx-font-size: 22;
      |-fx-padding: 3px;
      |-fx-border-insets: 3px;
      |-fx-background-insets: 3px;
      |""".stripMargin

  /**
   * Create a row that displays a timer
   */
  def createRow(name: String,
                source: String,
                area: String,
                ability: String,
                cooldown: Double): Timer = {

    // The basis of the row should be an hbox
    val base = new HBox()
    base.setStyle(hboxStyle)
    base.setPrefWidth(UICodeConfig.existingTimerWidthHeight._1)

    // each row should have a checkbox to enable or disable the timer
    val enabledCheckBox = new CheckBox()

    // each row should have a label for the timer name
    val timerName = new Label(name)
    timerName.setStyle(textStyle)

    // each row should have the name of the source
    val timerSource = new Label(source)
    timerSource.setStyle(textStyle)

    // each row should have the ability
    val timerAbility = new Label(ability)
    timerAbility.setStyle(textStyle)

    // each row should have the area the timer applies to
    val timerArea = new Label(area)
    timerArea.setStyle(textStyle)

    // each row should have the CD of the timer
    val cdArea = new Label(cooldown.toString)
    timerArea.setStyle(textStyle)

    base.getChildren.addAll(
      enabledCheckBox,timerName,timerSource,timerAbility,timerArea, cdArea
    )

    new Timer(
      base,
      enabledCheckBox,
      name,source,ability,area,
      cooldown
    )

  }

}



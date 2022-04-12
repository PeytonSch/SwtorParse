package UI.timers

import scalafx.event.ActionEvent
import scalafx.scene.control.CheckBox
import scalafx.scene.layout.HBox
import scalafx.Includes._

class Timer(
                row: HBox,
                enabled: CheckBox,
                name: String,
                source: String,
                ability: String,
                area: String,
                cooldown: Double
              ) {

  def addToUI: HBox = row
  def getAbility: String = ability
  def getCooldown: Double = cooldown
  def getName = name

  var triggeredAt = System.nanoTime()

  def getTriggeredAt = triggeredAt

  def trigger = {triggeredAt = System.nanoTime()}

  enabled.onAction = (event: ActionEvent) => {
    if (enabled.isSelected){
      ActiveTimers.enableTimer(this)
    }
    else {
      ActiveTimers.denableTimer(this)
    }

  }



}

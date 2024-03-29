package UI.timers

import logger.Logger
import patterns.LogInformation

/** This is the interface for currently active timers
  */

object ActiveTimers {

  // the list of abilities in the active timers
  var enabledTimers: Seq[Timer] = Seq()

  var activeTimers: Seq[Timer] = Seq()

  def getActiveTimers: Seq[Timer] =
    activeTimers.sortWith(_.getCooldown < _.getCooldown)

  def enableTimer(timer: Timer) = {
    // add the ability to the abilities to monitor list
    enabledTimers = enabledTimers :+ timer
//    Logger.highlight(s"${enabledTimers.mkString(",")}")
  }

  def denableTimer(timer: Timer) = {
    // add the ability to the abilities to monitor list
    enabledTimers = enabledTimers.filter(_ != timer)
//    Logger.highlight(s"${enabledTimers.mkString(",")}")
  }

  def deactivatTimer(timer: Timer): Unit = {
    activeTimers = activeTimers.filter(_ != timer)
  }

  /** Called from Controller as it is parsing, if it encounters an ability, it
    * spawns the correct timer
    */
  def checkAbility(logInfo: LogInformation) = {
    for (
      timer <- (
        enabledTimers
          .filter(
            _.getTriggerOn == "Ability Activate"
          )
          .filter(
            _.getAbility == logInfo.getAction().getName()
          )
      )
    ) {
      triggerTimer(timer)
    }
  }

  /** Called from Controller as it is parsing, if it encounters combat start, it
    * starts all timers set to begin on combat start
    */
  def checkCombatStart(logInfo: LogInformation) = {
    for (
      timer <- (
        enabledTimers
          .filter(
            _.getTriggerOn == "Combat Start"
          )
      )
    ) {
      triggerTimer(timer)
    }
  }

  // A Private function that triggers a timer
  // Other function in this class pass it timers to trigger
  private def triggerTimer(timer: Timer) = {
//      Logger.highlight(s"Found Timer for Ability ${timer.getAbility} from log ${logInfo.toString}")
    timer.trigger
    // if the timer is already in the list don't duplicate it, though we do want the updated time
    activeTimers = activeTimers.filter(_ != timer) :+ timer
  }

}

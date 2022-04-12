package Controller

import Combat.CombatInstance
import UI.ElementLoader
import UI.timers.ActiveTimers
import Utils.Timer
import logger.Logger
import parsing.Actors.Actor
import parsing.Result.{ApplyEffect, Event}
import patterns.Actions.SafeLogin
import patterns.LogInformation
import patterns.Result.{EnterCombat, ExitCombat}

import scala.collection.mutable

// TODO: This should be an object
object Controller {

  var currentCombat : CombatInstance = null // a combat instance you are currently in
  var previousCombat: CombatInstance = null // the last combat you were in. This is used for some UI refreshing when not in combat
  var playerToon : String = "" // The name of the character the user is currently logged into
  var allCombatInstances : Vector[CombatInstance] = Vector()

  var playerToonIdString : String = ""

  /**
   * Reset the controller, we do this when we open a new combat file
   */
  def resetController(): Unit = {
    allCombatInstances = Vector()
    currentCombat = null
    previousCombat = null
    playerToon = ""
    playerToonIdString = ""
  }


  def setCurrentCombatInstance(i : CombatInstance): Unit = {
    previousCombat = currentCombat
    currentCombat = i
  }

  def startNewCombat(logInfo : LogInformation) = {
    setCurrentCombatInstance(new CombatInstance)
    this.getCurrentCombat().setPlayerInCombat(playerToonIdString)
    this.getCurrentCombat().setCombatStartTimeStamp(logInfo)
    //println(s"Current combat is ${this.currentCombat}")
    allCombatInstances = allCombatInstances :+ currentCombat
    //println(s"All combat instances has size ${allCombatInstances.size}")

  }

  def endCombat() = {
    setCurrentCombatInstance(null)
  }

  def returnToPreviousCombatInstance(): Unit = {
    currentCombat = previousCombat
  }

  // companions get login lines as well so only set it the first time
  def setPlayerToon(s:String): Unit = if(playerToon == "") playerToon = s

  def appendToCombatActors(a : Actor): Unit = currentCombat.appendToCombatActors(a)

  def addDamageToCurrentCombat(logInfo: LogInformation): Unit = currentCombat.addDamageToCurrentCombat(logInfo)

  def getAllCombatInstances() = allCombatInstances

  def getCurrentCombat() = currentCombat

  def getPlayerToonName() = playerToon

  def getCurrentCombatActors() = currentCombat.getCombatActors()

  def getLastCurrentCombatActor() = currentCombat.getLastCurrentCombatActor()

  def setPlayerToonIdString(str: String) = if(playerToonIdString == "") playerToonIdString = str

  def getCurrentPlayerIdString() = this.playerToonIdString

  def getCurrentPlayerDamage(): Int = {
    currentCombat.getPlayerInCombatActor().getDamageDone()
  }

  def getCombatInstanceById(id:String): CombatInstance = {
    for (cInst <- allCombatInstances) {
      if (cInst.getNameFromActors == id) {
        return cInst
      }
    }
    null
  }

  /**
   * This function takes the output from parser.getLines and puts the information
   * into combat instances, collects values, etc. It was originally in main but I
   * abstracted it here so that it can be easily used in testing to ensure correct
   * behavior and to keep it out of the way.
   * @param logLines
   */
  def parseLatest(logLines : IndexedSeq[LogInformation]): Unit = {

    Logger.trace(s"Controller received ${logLines.length} lines from parser")
    for (logInfo <- logLines) {

      /**
       * Check for Entering or Exiting Combat
       */
      // Check to see if we entered or exit combat
      if(logInfo.getResult().isInstanceOf[EnterCombat]) {
        this.startNewCombat(logInfo)
      } else if (logInfo.getResult().isInstanceOf[ExitCombat]) {
        this.endCombat()
        // when we end combat, have the UI refresh the combat instance menu
        ElementLoader.loadCombatInstanceMenu()
      }

      // Check for login action
      if (logInfo.getAction().isInstanceOf[SafeLogin]){
        Logger.trace("Found Login Action, setting player toon name")
        this.setPlayerToon(logInfo.getPerformer().getName())
        this.setPlayerToonIdString(logInfo.getPerformer().getId().toString)
        //println(s"Got Login of Toon: ${controller.getPlayerToonName()} from line ${logInfo}")
      }

      // if we are currently in combat
      if (this.currentCombat != null) {

        // update combat time
        this.getCurrentCombat().combatTimeSeconds = logInfo.getTime() - this.getCurrentCombat().startTimeStamp

        // Make sure the actor and target are in the combat actors set
        this.appendToCombatActors(logInfo.getPerformer())
        this.appendToCombatActors(logInfo.getTarget())

        // Update Threat To actors
        this.getCurrentCombat().addThreatToCurrentCombat(logInfo)

        // see if the Result is an ApplyEffect and see if its name is Damage
        if (logInfo.getResult().isInstanceOf[ApplyEffect] && logInfo.getResult().asInstanceOf[ApplyEffect].getName() == "Damage") {
          this.getCurrentCombat().addDamageToCurrentCombat(logInfo)
        }

        // Check healing
        if (logInfo.getResult().isInstanceOf[ApplyEffect] && logInfo.getResult().asInstanceOf[ApplyEffect].getName() == "Heal") {
          this.getCurrentCombat().addHealingToCurrentCombat(logInfo)
        }

        if (logInfo.getResult().isInstanceOf[Event]) {
          this.getCurrentCombat().addEventToCombat(logInfo)

          // if event is ability activate
          if (logInfo.getResult().asInstanceOf[Event].getName == "AbilityActivate") {
            // check for timers, but only if we are live logging
            if (logLines.length < 1000000){
              ActiveTimers.checkAbility(logInfo)
            }
          }
        }

      }


    }
  }

}

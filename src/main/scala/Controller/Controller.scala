package Controller

import Combat.CombatInstance
import parsing.Actors.Actor
import parsing.Result.ApplyEffect
import patterns.Actions.SafeLogin
import patterns.LogInformation
import patterns.Result.{EnterCombat, ExitCombat}

class Controller () {

  var currentCombat : CombatInstance = null // a combat instance you are currently in
  var playerToon : String = "" // The name of the character the user is currently logged into
  var allCombatInstances : Vector[CombatInstance] = Vector()


  def setCurrentCombatInstance(i : CombatInstance): Unit = {
    //println(s"Setting current combat to ${i}")
    currentCombat = i
  }

  def startNewCombat() = {
    setCurrentCombatInstance(new CombatInstance)
    //println(s"Current combat is ${this.currentCombat}")
    allCombatInstances = allCombatInstances :+ currentCombat
    //println(s"All combat instances has size ${allCombatInstances.size}")

  }

  def endCombat() = {
    setCurrentCombatInstance(null)
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


  /**
   * This function takes the output from parser.getLines and puts the information
   * into combat instances, collects values, etc. It was originally in main but I
   * abstracted it here so that it can be easily used in testing to ensure correct
   * behavior and to keep it out of the way.
   * @param logLines
   */
  def parseLatest(logLines : IndexedSeq[LogInformation]): Unit = {

    for (logInfo <- logLines) {

      /**
       * Check for Entering or Exiting Combat
       */
      // Check to see if we entered or exit combat
      if(logInfo.getResult().isInstanceOf[EnterCombat]) {
        this.startNewCombat()
      } else if (logInfo.getResult().isInstanceOf[ExitCombat]) {
        this.endCombat()
      }

      // Check for login action
      if (logInfo.getAction().isInstanceOf[SafeLogin]){
        this.setPlayerToon(logInfo.getPerformer().getName())
        //println(s"Got Login of Toon: ${controller.getPlayerToonName()} from line ${logInfo}")
      }

      // if we are currently in combat
      if (this.currentCombat != null) {

        // Make sure the actor and target are in the combat actors set
        this.appendToCombatActors(logInfo.getPerformer())
        this.appendToCombatActors(logInfo.getTarget())

        // see if the Result is an ApplyEffect and see if its name is Damage
        if (logInfo.getResult().isInstanceOf[ApplyEffect] && logInfo.getResult().asInstanceOf[ApplyEffect].getName() == "Damage") {
          this.getCurrentCombat().addDamageToCurrentCombat(logInfo)
        }

      }


    }

  }

}

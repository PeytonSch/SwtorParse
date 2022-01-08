package Controller

import Combat.CombatInstance
import parsing.Actors.Actor
import patterns.LogInformation

class Controller () {

  var currentCombat : CombatInstance = null // a combat instance you are currently in
  var playerToon : String = "" // The name of the character the user is currently logged into
  var allCombatInstances : Vector[CombatInstance] = Vector()


  def setCurrentCombatInstance(i : CombatInstance): Unit = {
    println(s"Setting current combat to ${i}")
    currentCombat = i
  }

  def startNewCombat() = {
    setCurrentCombatInstance(new CombatInstance)
    println(s"Current combat is ${this.currentCombat}")
    allCombatInstances = allCombatInstances :+ currentCombat
    println(s"All combat instances has size ${allCombatInstances.size}")

  }

  def endCombat() = {
    setCurrentCombatInstance(null)
  }

  def setPlayerToon(s:String): Unit = playerToon = s

  def appendToCombatActors(a : Actor): Unit = currentCombat.appendToCombatActors(a)

  def addDamageToCurrentCombat(logInfo: LogInformation): Unit = currentCombat.addDamageToCurrentCombat(logInfo)

  def getAllCombatInstances() = allCombatInstances

  def getCurrentCombat() = currentCombat

  def getPlayerToonName() = playerToon

  def getCurrentCombatActors() = currentCombat.getCombatActors()

}

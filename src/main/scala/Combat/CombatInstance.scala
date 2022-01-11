package Combat

import parsing.Actors.{Actor, NoneActor}
import parsing.Values.Value
import patterns.LogInformation

import scala.collection.IterableOnce.iterableOnceExtensionMethods

class CombatInstance (

                     ){

  var combatActors : Vector[CombatActorInstance] = Vector()

  var playerInCombat : String = ""

  def setPlayerInCombat(player:String): Unit = playerInCombat = player

  def getPlayerInCombatId() = playerInCombat

  def getPlayerInCombatActor() = this.getCombatActorByIdString(playerInCombat)

  // make a combat instance name from actors
  def getName: String = {
    var str = ""
    for (a <- combatActors) {
      str += a.getIdString()
    }
    str
  }

  def appendToCombatActors(a:Actor): Unit = {

    if (a == null || a.isInstanceOf[NoneActor]) return

    var updated = false

    // iterate through combatActors and if we find the same id, then update that combat actor
    for (actor <- combatActors) {
      if (actor.getIdString() == a.getId().toString) {
        actor.updateActor(a)
        updated = true
      }
    }
    // if we did not update an old actor, then add it to the list
    if (!updated) {
      val newActor = new CombatActorInstance
      newActor.newCombatActorInstance(a)
      combatActors = combatActors :+ newActor
    }
    // regardless of the filter operation, add the new actor in

  }

  def getCombatActors() = combatActors

  def getLastCurrentCombatActor() = combatActors.last.getActor()

  def addDamageToCurrentCombat(logInfo : LogInformation): Unit = {
    // check which actor is performing the damage and add it to their damage
    val performerId: String = logInfo.getPerformer().getId().toString
    val targetId: String = logInfo.getTarget().getId().toString
    val totalValue : Int = logInfo.getResulValue().getTotalValue()

    // find the combatActor to add damage to
    for (actor <- combatActors) {
      if (actor.getIdString() == performerId) {
        actor.updateDamageDone(totalValue)
      }
    }

    // find the combatActor to add damage taken to
    for (actor <- combatActors) {
      if (actor.getIdString() == targetId) {
        actor.updateDamageTaken(totalValue)
      }
    }

  }

  def getCombatActorByIdString(id:String): CombatActorInstance = {
    var result : CombatActorInstance = null
    for (actor <- combatActors) {
      if (actor.getIdString() == id) {
        result = actor
      }
    }
    result
  }


}

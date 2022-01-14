package Combat

import parsing.Actors.{Actor, NoneActor}
import parsing.Values.Value
import parsing.subTypes.LogTimestamp
import patterns.LogInformation

import java.time.{LocalDate, LocalTime}
import scala.collection.IterableOnce.iterableOnceExtensionMethods

class CombatInstance (

                     ){

  var combatActors : Vector[CombatActorInstance] = Vector()

  var playerInCombat : String = ""

  var startTimeStamp : LogTimestamp = null
  def setCombatStartTimeStamp(logInfo: LogInformation) = {
    if(startTimeStamp == null) this.startTimeStamp = logInfo.getTime()
    else {
      println("Error, tried updating timestamp that was already set")
    }
  }

  override def toString: String = s"Combat Instance: ${this.startTimeStamp}"


  def setPlayerInCombat(player:String): Unit = playerInCombat = player

  def getPlayerInCombatId() = playerInCombat

  def getPlayerInCombatActor() = this.getCombatActorByIdString(playerInCombat)

  // make a combat instance name from actors
  def getNameFromActors: String = {
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
    val totalValue : Int = logInfo.getResulValue().getFullValue()
    // TODO: this time should be the number of seconds since this combat instance started
    val durationMarkFromStart = logInfo.getTime() - this.startTimeStamp

    // find the combatActor to add damage to
    for (actor <- combatActors) {
      if (actor.getIdString() == performerId) {
        actor.updateDamageDone(totalValue,durationMarkFromStart)
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

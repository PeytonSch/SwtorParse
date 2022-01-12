package Combat

import parsing.Actors.{Actor, NoneActor}
import parsing.Values.Value
import patterns.LogInformation

import java.time.{LocalDate, LocalTime}
import scala.collection.IterableOnce.iterableOnceExtensionMethods

class CombatInstance (

                     ){

  var combatActors : Vector[CombatActorInstance] = Vector()

  var playerInCombat : String = ""

  var startTimeStamp : LocalTime = null
  def setCombatStartTimeStamp(logInfo: LogInformation) = {
    this.startTimeStamp = LocalTime.parse(logInfo.getTime().toString)
  }

  override def toString: String = s"Combat Instance: ${this.startTimeStamp}"


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
    // TODO: this time should be the number of seconds since this combat instance started
    val timestamp : LocalTime = LocalTime.parse(logInfo.getTime().toString)
    // Tihs gets the time to epoch second but assumes the log was created on this day. Not really a big
    // deal if we dont show date information
    // TODO: Can we extract the date information from the log name
    val timeFromStartOfDay = timestamp.toSecondOfDay
    val dayFromEpoch =  LocalDate.now().atStartOfDay().toEpochSecond(java.time.ZoneOffset.UTC)
    val instantFromEpoch = timeFromStartOfDay + dayFromEpoch
    println(s"Got seconds from epoch ${instantFromEpoch}")
    val instant : java.time.Instant = java.time.Instant.ofEpochSecond(instantFromEpoch)

    // find the combatActor to add damage to
    for (actor <- combatActors) {
      if (actor.getIdString() == performerId) {
        actor.updateDamageDone(totalValue,instant)
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

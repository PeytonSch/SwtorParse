package Combat

import logger.{LogLevel, Logger}
import parsing.Actors.{Actor, NoneActor, Player}
import parsing.Values.Value
import parsing.subTypes.{Health, LogTimestamp}
import patterns.LogInformation

import java.time.{LocalDate, LocalTime}
import scala.collection.IterableOnce.iterableOnceExtensionMethods
import scala.collection.mutable

class CombatInstance (

                     ){

  var combatActors : Vector[CombatActorInstance] = Vector()

  var playerInCombat : String = ""

  var combatTimeSeconds : Int = 0

  var startTimeStamp : LogTimestamp = null
  def setCombatStartTimeStamp(logInfo: LogInformation) = {
    if(startTimeStamp == null) this.startTimeStamp = logInfo.getTime()
    else {
      Logger.error("Error, tried updating timestamp that was already set")
    }
  }

  override def toString: String = s"Combat Instance: ${this.startTimeStamp}"


  def setPlayerInCombat(player:String): Unit = playerInCombat = player

  def getPlayerInCombatId() = playerInCombat

  def getPlayerInCombatActor() = this.getCombatActorByNameOrID(playerInCombat)

  // make a combat instance name from actors
  def getNameFromActors: String = {
    var str = ""
    for (a <- combatActors) {
      if (a.getActorType() == "Other") {
        str += a.getActor().getName()
      }

    }
    str
  }

  /**
   * Timer Suggested Abilities
   */
  // TODO: These need to be keyed off performer ID, otherwise things like bestia monsters with the same name mess it up
  // Key: (Ability Name, Performer)
  // Value: List[Time Activated At], List[Health % Activated At]
  var timerSuggestionMap: mutable.Map[(String,String),(List[Double],List[Double])] = mutable.Map()

  def getTimerSuggestionMap = timerSuggestionMap

  // add Event to Combat
  def addEventToCombat(logInfo : LogInformation): Unit = {
    val performerId: String = logInfo.getPerformer().getId().toString
    val performerName: String = logInfo.getPerformer().getName()
    val performerHealth: Health = logInfo.getPerformer().getHealth()
    val healthPercent: Int = if (performerHealth.getMax() != 0) ((performerHealth.getCurrent() / performerHealth.getMax().toDouble) * 100).toInt else 0
    val ability : String = logInfo.getAction().getName()
    val durationMarkFromStart = logInfo.getTime() - this.startTimeStamp

    // we only care about non players for suggestions
    if (!logInfo.getPerformer().isInstanceOf[Player] && ability != "No Action"){
//      Logger.highlight(s"Got ${performerId}, ${performerName}, ${performerHealth}, ${ability}")
      if(timerSuggestionMap.contains((ability,performerName))){
        val timeList: List[Double] = timerSuggestionMap(ability,performerName)._1 :+ (durationMarkFromStart)
        val healthList: List[Double] = timerSuggestionMap(ability,performerName)._2 :+ (healthPercent)
        timerSuggestionMap((ability,performerName)) = (timeList,healthList)
      }
      else {
        val timeList: List[Double] = List(durationMarkFromStart)
        val healthList: List[Double] = List(healthPercent)
        timerSuggestionMap((ability,performerName)) = (timeList,healthList)
      }

    }

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
    val performerName: String = logInfo.getPerformer().getName()
    val targetId: String = logInfo.getTarget().getId().toString
    val targetName: String = logInfo.getTarget().getName()
    val totalValue : Int = logInfo.getResulValue().getFullValue()
    val damageType : String = logInfo.getResulValue().getValueType()
    val damageSource : String = logInfo.getAction().getName()
    val durationMarkFromStart = logInfo.getTime() - this.startTimeStamp
    val crit = logInfo.getResulValue().getCrit()

    // find the combatActor to add damage to
    for (actor <- combatActors) {
      if (actor.getIdString() == performerId) {
        actor.updateDamageDone(totalValue,durationMarkFromStart,damageType,damageSource,crit,targetName)
      }
    }

    // find the combatActor to add damage taken to
    for (actor <- combatActors) {
      if (actor.getIdString() == targetId) {
        actor.updateDamageTaken(totalValue,durationMarkFromStart,damageType,damageSource,crit,performerName)
      }
    }

  }

  def addHealingToCurrentCombat(logInfo : LogInformation): Unit = {
    // check which actor is performing the damage and add it to their damage
    // performers can be none in healing lines
    val performerId: String = logInfo.getPerformer().getId().toString
    val performerName: String = logInfo.getPerformer().getName()
    val targetId: String = logInfo.getTarget().getId().toString
    val targetName: String = logInfo.getTarget().getName()
    val totalValue : Int = logInfo.getResulValue().getFullValue()
    // heals never seem to have a type but we'll leave this here anyways
    val damageType : String = logInfo.getResulValue().getValueType()
    val damageSource : String = logInfo.getAction().getName()
    val durationMarkFromStart = logInfo.getTime() - this.startTimeStamp
    val crit = logInfo.getResulValue().getCrit()

    // find the combatActor to add healing to
    for (actor <- combatActors) {
      if (actor.getIdString() == performerId) {
        actor.updateHealingDone(totalValue,durationMarkFromStart,damageType,damageSource,crit,targetName)
      }
    }

    // find the combatActor to add healing taken to
    for (actor <- combatActors) {
      if (actor.getIdString() == targetId) {
        actor.updateHealingTaken(totalValue,durationMarkFromStart,damageType,damageSource, crit, performerName)
      }
    }

  }

  def addThreatToCurrentCombat(logInfo : LogInformation): Unit = {
    // check which actor is performing the damage and add it to their damage
    val performerId: String = logInfo.getPerformer().getId().toString
    val threatValue = logInfo.getThreatValue().getValue()
    val threatSource : String = logInfo.getAction().getName()
    val durationMarkFromStart = logInfo.getTime() - this.startTimeStamp
    val threatType = "" // know types of threat as far as I know, but need this in here for ability stats

    // find the combatActor to add threat to
    for (actor <- combatActors) {
      if (actor.getIdString() == performerId) {
        actor.updateThreat(threatValue,durationMarkFromStart,"",threatSource)
      }
    }

  }

  def getCombatActorByNameOrID(lookingFor: String): CombatActorInstance = {
    var result : CombatActorInstance = null
    result = getCombatActorByIdString(lookingFor)
    if (result == null) {
      result = getCombatActorByNameString(lookingFor)
    }

    if (result == null) {
      Logger.error(s"Could not find combat actor ${lookingFor}")
    }

    result

  }

  def getCombatActorByIdString(id:String): CombatActorInstance = {
    var result : CombatActorInstance = null
    for (actor <- combatActors) {
      if (actor.getIdString() == id) {
        result = actor
      }
    }

    if (result == null) {
      Logger.error(s"Could not get combat actor by ID string ${id} in combat ${this.toString}. Returning first actor")
      result = combatActors(0)
    }
    result
  }

  def getCombatActorByNameString(name:String): CombatActorInstance = {
    var result : CombatActorInstance = null
    for (actor <- combatActors) {
      if (actor.getActor().getName() == name) {
        result = actor
      }
    }
    if (result == null) {
      Logger.error(s"Could not get combat actor by name string ${name} in combat ${this.toString}. Returning first actor")
      result = combatActors(0)
    }
    result
  }

  // This was created for the drop down menu. Players just have their names,
  // but anything else that could have multiple names is Name : InstanceID
  def getCombatActorByPrettyNameID(name:String): CombatActorInstance = {
    var result : CombatActorInstance = null
    for (actor <- combatActors) {
      if (actor.getActor().getPrettyNameWithInstanceIdIfNecessary() == name) {
        result = actor
      }
    }
    if (result == null) {
      Logger.error(s"Could not get combat actor by prettyNameId ${name} in combat ${this.toString}")
      if (playerInCombat != "") {
        result = getCombatActorByIdString(playerInCombat)
      } else {
        Logger.error("Player in combat string not set, returning first actor")
        result = combatActors(0)
      }
    }
    result
  }


}

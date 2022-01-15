package Combat

import eu.hansolo.tilesfx.chart.ChartData
import parsing.Actors.Actor
import parsing.subTypes.ActorId
import patterns.LogInformation

import scala.collection.mutable

/**
 * A combat Actor Instance is an instance of an Actor in combat.
 * It is a way to keep track of all actor stats in an instance of combat.
 *
 * We can use this to make a handful of things easier. Among these are
 * - Keep track of what actors are in the instance of combat.
 *    | We can have 1 instance of combatActorInstance and then update the
 *    | corresponding actor, rather than doing the funky drop and update method
 *    | I had before
 *
 * - Add up and retreive all stats from each actor in combat instances
 *    | This class can hold all dps,dtps,htps, etc for itself in one instance of combat
 *    | This is better than having catch all maps of actors to dps/hps/etc.
 *
 */
class CombatActorInstance {

  /**
   * each actor combat instance will have a variable actor type, this will
   * be updated with the most up to date instance of an actor. This means it will
   * always include up to date health and positioning etc.
   */
  var actor : Actor = null

  def getActor() = actor

  /**
   * This string will be how we know different Actor instances belong to the same
   * CombatActorInstance. In other words, it will link actors by their id's
   */
  var actorIdString : String = null
  var actorId : ActorId = null

  def getIdString() = actorIdString


  var damageDoneTimeSeries : mutable.Map[Int,Int] = mutable.Map()
  var damageDone = 0
  var damagePerSecondTimeSeries : mutable.Map[Int,Int] = mutable.Map()
  var damageDoneStats : mutable.Map[String,mutable.Map[String,Int]] = mutable.Map()
  def getDamageDoneStats() = damageDoneStats
  var damageTypeDone : mutable.Map[String,Int] = mutable.Map()
  def getDamageTypeDone() = damageTypeDone
  def updateDamageDone(damageAmount: Int, axisValue : Int, damageType : String, damageSource : String): Unit = {
    damageDone += damageAmount
    // update damage Types
    if (damageTypeDone.contains(damageType)){
      val newDamageInBucket : Int = damageTypeDone.get(damageType).get + damageAmount
      damageTypeDone += (damageType -> newDamageInBucket)
    }
    // if we don't have any data for this second yet, add that key value
    else {
      damageTypeDone(damageType) = damageAmount
    }

    /**
     * Update damageDoneTimeSeries
     * */
      // check if we already have damage done in this second
      if (damageDoneTimeSeries.contains(axisValue)){
        val newDamageInBucket : Int = damageDoneTimeSeries.get(axisValue).get + damageAmount
        damageDoneTimeSeries += (axisValue -> newDamageInBucket)
      }
        // if we don't have any data for this second yet, add that key value
      else {
        damageDoneTimeSeries(axisValue) = damageAmount
      }

    /**
     * Update damagePerSecondTimeSeries
     */
    if (damagePerSecondTimeSeries.contains(axisValue)){
      damagePerSecondTimeSeries += (axisValue -> damageDone / (axisValue + 1))
    }
    // if we don't have any data for this second yet, add that key value
    else {
      damagePerSecondTimeSeries(axisValue) = damageDone / (axisValue + 1)
    }

    /**
     * Damage Done Stats, can we combine this and get rid of the above?
     */
    // see if we have seen this type
    if (damageDoneStats.contains(damageType)){
      // if we have seen this type, check the inner key and see if we have this ability
      if (damageDoneStats(damageType).contains(damageSource)) {
        // if we have the ability update the damage amount for that ability
        val newDamageValue = damageDoneStats(damageType)(damageSource) + damageAmount
        damageDoneStats(damageType) += (damageSource -> newDamageValue)
      } else {
        // if we dont have that ability add it to the inner map
        damageDoneStats(damageType)(damageSource) = damageAmount
      }
    }
    // if we have nothing for this damage type, we know we can just add the stats
    else {
      damageDoneStats(damageType) = mutable.Map(damageSource -> damageAmount)
    }

  }
  def getDamageDone() = damageDone
  def getDamageDoneTimeSeries() = damageDoneTimeSeries
  def getDamagePerSecondTimeSeries() = damagePerSecondTimeSeries

  var damageTaken = 0
  def getDamageTaken() = damageTaken
  var damageTypeTaken : mutable.Map[String,Int] = mutable.Map()
  def getDamageTypeTaken() = damageTypeTaken

  // TODO: Can we get rid of damageTypeTaken and make this all based off of damageTakenStats
  // damage taken stats: Map(DamageType -> (Ability -> Amount)
  var damageTakenStats : mutable.Map[String,mutable.Map[String,Int]] = mutable.Map()
  def getDamageTakenStats() = damageTakenStats

  // TODO: Add DTPS graph functionality, make it toggleable in the UI
  def updateDamageTaken(damageAmount: Int, axisValue : Int, damageType : String, damageSource : String): Unit = {
    damageTaken += damageAmount
    // update damage Types
    if (damageTypeTaken.contains(damageType)){
      val newDamageInBucket : Int = damageTypeTaken.get(damageType).get + damageAmount
      damageTypeTaken += (damageType -> newDamageInBucket)
    }
    // if we don't have any data for this second yet, add that key value
    else {
      damageTypeTaken(damageType) = damageAmount
    }

    /**
     * Damage Taken Stats, can we combine this and get rid of the above?
     */
    // see if we have seen this type
    if (damageTakenStats.contains(damageType)){
      // if we have seen this type, check the inner key and see if we have this ability
      if (damageTakenStats(damageType).contains(damageSource)) {
        // if we have the ability update the damage amount for that ability
        val newDamageValue = damageTakenStats(damageType)(damageSource) + damageAmount
        damageTakenStats(damageType) += (damageSource -> newDamageValue)
      } else {
        // if we dont have that ability add it to the inner map
        damageTakenStats(damageType)(damageSource) = damageAmount
      }
    }
      // if we have nothing for this damage type, we know we can just add the stats
    else {
      damageTakenStats(damageType) = mutable.Map(damageSource -> damageAmount)
    }

  }




  /**
   * When we find a new actor, not in our current combat, we will instantiate one
   * using this method. It will set base variables such as actor and actorIdString.
   *
   * If we ever encounter a null value, there is a good chance it should be set here.
   * @param a
   */
  def newCombatActorInstance(a:Actor): Unit = {
    this.updateActor(a)
  }

  def updateActor(a: Actor): Unit = {
    actor = a
    actorIdString = a.getId().toString
    actorId = a.getId()
  }

}

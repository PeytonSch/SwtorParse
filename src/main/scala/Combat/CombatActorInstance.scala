package Combat

import UI.GraphicFactory.SpreadSheetRow
import eu.hansolo.tilesfx.chart.ChartData
import logger.Logger
import parsing.Actors.{Actor, Companion, Player}
import parsing.subTypes.ActorId
import patterns.LogInformation
import scalafx.collections.ObservableBuffer

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

  var actorType : String = null

  def getActor() = actor

  /**
   * This string will be how we know different Actor instances belong to the same
   * CombatActorInstance. In other words, it will link actors by their id's
   */
  var actorIdString : String = null
  var actorId : ActorId = null

  def getIdString() = actorIdString

  def getActorType():String = actorType

  /**
   * Stat Values
   */
  var damageDone = 0
  def getDamageDone() = damageDone
  var damagePerSecond: Double = 0
  def getDamagePerSecond() = damagePerSecond
  var damageTaken = 0
  def getDamageTaken() = damageTaken
  var damageTakenPerSecond: Double = 0
  def getDamageTakenPerSecond() = damageTakenPerSecond
  var healingDone = 0
  def getHealingDone() = healingDone
  var healingDonePerSecond = 0
  def getHealingDonePerSecond() = healingDonePerSecond
  var healingTaken = 0
  def getHealingTaken() = healingTaken
  var healingTakenPerSecond: Double = 0
  def getHealingTakenPerSecond() = healingTakenPerSecond
  var threatDone = 0
  def getThreatDone() = threatDone
  var threatDonePerSecond = 0
  def getThreatDonePerSecond() = threatDonePerSecond
  var totalDamageAbilities = 0
  var critDamageAbilities = 0
  def getCritDamagePercent(): Double = {
    if (totalDamageAbilities == 0) 0
    else critDamageAbilities.toDouble/totalDamageAbilities
  }
  // TODO: Expand this to an apm graph
  // TODO: This calculation is wrong, using totalDamageAbilities is wrong
  var apmTimeSec: Int = 0
  def getApm(): Double = {
    if (apmTimeSec ==0) 0
    else ((totalDamageAbilities.toDouble*60)/ apmTimeSec)
  }

  /**
   * Graph Series
   */
  var damageDoneTimeSeries : mutable.Map[Int,Int] = mutable.Map()
  var damagePerSecondTimeSeries : mutable.Map[Int,Int] = mutable.Map()
  def getDamageDoneTimeSeries() = damageDoneTimeSeries
  def getDamagePerSecondTimeSeries() = damagePerSecondTimeSeries
  var healingDoneTimeSeries : mutable.Map[Int,Int] = mutable.Map()
  def gethealingDoneTimeSeries() = healingDoneTimeSeries
  var healingPerSecondTimeSeries : mutable.Map[Int,Int] = mutable.Map()
  def gethealingPerSecondTimeSeries() = healingPerSecondTimeSeries

  var damageTakenTimeSeries : mutable.Map[Int,Int] = mutable.Map()
  def getDamageTakenTimeSeries() = damageTakenTimeSeries
  var damageTakenPerSecondTimeSeries : mutable.Map[Int,Int] = mutable.Map()
  def getDamageTakenPerSecondTimeSeries() = damageTakenPerSecondTimeSeries

  var healingTakenTimeSeries : mutable.Map[Int,Int] = mutable.Map()
  def gethealingTakenTimeSeries() = healingTakenTimeSeries
  var healingTakenPerSecondTimeSeries : mutable.Map[Int,Int] = mutable.Map()
  def getHealingTakenPerSecondTimeSeries() = healingTakenPerSecondTimeSeries

  var threatDoneTimeSeries : mutable.Map[Int,Int] = mutable.Map()
  def getThreatDoneTimeSeries() = threatDoneTimeSeries
  var threatPerSecondTimeSeries : mutable.Map[Int,Int] = mutable.Map()
  def getThreatPerSecondTimeSeries() = threatPerSecondTimeSeries


  /**
   * Pie Chart Info
   */
  var damageDoneStats : mutable.Map[String,mutable.Map[String,Int]] = mutable.Map()
  def getDamageDoneStats() = damageDoneStats
  var damageDone1DStats : mutable.Map[String,mutable.Map[String,Int]] = mutable.Map()
  def getDamageDone1DStats() = damageDone1DStats
  var damageTypeDone : mutable.Map[String,Int] = mutable.Map()
  def getDamageTypeDone() = damageTypeDone

  var damageTakenStats : mutable.Map[String,mutable.Map[String,Int]] = mutable.Map()
  def getDamageTakenStats() = damageTakenStats
  var damageTaken1DStats : mutable.Map[String,mutable.Map[String,Int]] = mutable.Map()
  def getDamageTaken1DStats() = damageTaken1DStats

  var healingTakenStats : mutable.Map[String,mutable.Map[String,Int]] = mutable.Map()
  def gethealingTakenStats() = healingTakenStats
  var healingDoneStats : mutable.Map[String,mutable.Map[String,Int]] = mutable.Map()
  def getHealingDoneStats() = healingDoneStats

  var threatDoneStats : mutable.Map[String,mutable.Map[String,Int]] = mutable.Map()
  def getThreatDoneStats() = threatDoneStats

  /**
   * Type Taken Wheel
   */
  var damageTypeTaken : mutable.Map[String,Int] = mutable.Map()
  def getDamageTypeTaken() = damageTypeTaken

  /**
   * Spreadsheet Data
   */
  // TODO: Thinking that dps and total % should be calculated at the end
  // Key: Ability, Target
  // Value hits,normal hits, crit hits,norm,crit,avg,miss,dps,total,total %
  var damageDoneSheetDataMap: mutable.Map[(String,String),(Int,Int,Int,Int,Int,Int,Double,Int,Int,Double)] = mutable.Map()
  var damageTakenSheetDataMap: mutable.Map[(String,String),(Int,Int,Int,Int,Int,Int,Double,Int,Int,Double)] = mutable.Map()
  var healingDoneSheetDataMap: mutable.Map[(String,String),(Int,Int,Int,Int,Int,Int,Double,Int,Int,Double)] = mutable.Map()
  var healingTakenSheetDataMap: mutable.Map[(String,String),(Int,Int,Int,Int,Int,Int,Double,Int,Int,Double)] = mutable.Map()



  /**
   * Dont Forget Pub Toons
   *
   * // True Reflects
   * Operative: Blow for blow | Back At Ya
   * Jug: Saber Reflect | Saber Reflect
   * Merc: Responsive Safeguards | Echoing Deterrence
   * PT: Sonic Rebounder | Sonic Rebounder
   *
   * TODO: Add option for retaliatory damage
   * // Retaliatory Damage
   * Cloak of Pain
   * Pt one (close and personal)
   */

  var reflectDamage: Int = 0 // To store total reflect damage done
  var rebounderDamage: Int = 0 // To store just the reflect damage from rebounders
  // if the ability source is in this list, add it to reflect damage
  val reflectAbilityList:List[String] = List("Saber Reflect", "Sonic Rebounder","Responsive Safeguards","Blow for Blow","Back At Ya", "Echoing Deterrence")


  import SpreadSheetCalculators._
  def getDamageDoneSpreadSheetData():ObservableBuffer[SpreadSheetRow] = getSpreadSheetData(damageDoneSheetDataMap)
  def getDamageTakenSpreadSheetData():ObservableBuffer[SpreadSheetRow] = getSpreadSheetData(damageTakenSheetDataMap)
  def getHealingDoneSpreadSheetData():ObservableBuffer[SpreadSheetRow] = getSpreadSheetData(healingDoneSheetDataMap)
  def getHealingTakenSpreadSheetData():ObservableBuffer[SpreadSheetRow] = getSpreadSheetData(healingTakenSheetDataMap)
  
  def getDamageDoneByAbilitySpreadSheetData():ObservableBuffer[SpreadSheetRow] = getSpreadSheetDataByAbility(aggregateOnAbility(damageDoneSheetDataMap))
  def getDamageDoneToTargetTypeSpreadSheetData():ObservableBuffer[SpreadSheetRow] = getSpreadSheetDataToTarget(aggregateOnTargetType(damageDoneSheetDataMap))
  def getDamageTakenByAbilitySpreadSheetData():ObservableBuffer[SpreadSheetRow] = getSpreadSheetDataByAbility(aggregateOnAbility(damageTakenSheetDataMap))
  def getDamageTakenToTargetTypeSpreadSheetData():ObservableBuffer[SpreadSheetRow] = getSpreadSheetDataToTarget(aggregateOnTargetType(damageTakenSheetDataMap))
  def getHealingDoneByAbilitySpreadSheetData():ObservableBuffer[SpreadSheetRow] = getSpreadSheetDataByAbility(aggregateOnAbility(healingDoneSheetDataMap))
  def getHealingDoneToTargetTypeSpreadSheetData():ObservableBuffer[SpreadSheetRow] = getSpreadSheetDataToTarget(aggregateOnTargetType(healingDoneSheetDataMap))
  def getHealingTakenByAbilitySpreadSheetData():ObservableBuffer[SpreadSheetRow] = getSpreadSheetDataByAbility(aggregateOnAbility(healingTakenSheetDataMap))
  def getHealingTakenToTargetTypeSpreadSheetData():ObservableBuffer[SpreadSheetRow] = getSpreadSheetDataToTarget(aggregateOnTargetType(healingTakenSheetDataMap))


  def updateDamageDone(damageAmount: Int, axisValue : Int, damageType : String, damageSource : String, crit : Boolean, target: String): Unit = {
    damageDone += damageAmount
    damagePerSecond = damageDone / (axisValue+1)
    totalDamageAbilities += 1
    apmTimeSec = axisValue

    if (crit) critDamageAbilities += 1

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


    /**
     * for a 1d approach, always add all damage to an empty damage type.
     * This supports our overlays that only have 1 ring
     */
    // see if we have seen this type
    if (damageDone1DStats.contains("")){
      // if we have seen this type, check the inner key and see if we have this ability
      if (damageDone1DStats("").contains(damageSource)) {
        // if we have the ability update the damage amount for that ability
        val newDamageValue = damageDone1DStats("")(damageSource) + damageAmount
        damageDone1DStats("") += (damageSource -> newDamageValue)
      } else {
        // if we dont have that ability add it to the inner map
        damageDone1DStats("")(damageSource) = damageAmount
      }
    }
    // if we have nothing for this damage type, we know we can just add the stats
    else {
      damageDone1DStats("") = mutable.Map(damageSource -> damageAmount)
    }


    /**
     * Update Spreadsheet data
     */
    if (damageDoneSheetDataMap.contains((damageSource,target))){
      val current = damageDoneSheetDataMap((damageSource,target))
      val hits = current._1 + 1
      val normHits = if (crit) {
        current._2
      } else {
        current._2 + 1
      }
      val critHits = if (crit) {
        current._3 + 1
      } else {
        current._3
      }
      val norm = if (crit) {
        current._4
      } else {
        (current._4 + damageAmount) / hits
      }
      val critVal = if (crit) {
        current._5 + damageAmount
      } else {
        current._5
      }
      val avg = (current._6 + damageAmount) / hits
      val miss = 0
      val dps = 0
      val total = current._9 + damageAmount
      val totalPercent = 0

      damageDoneSheetDataMap((damageSource,target)) = (hits,normHits,critHits,norm,critVal,avg,miss,dps,total,totalPercent)

    } else {
      val hits = 1
      val normHits = if (crit) 0 else 1
      val critHits = if (crit) 1 else 0
      val norm = if (crit) 0 else damageAmount
      val critVal = if (crit) damageAmount else 0
      val avg = damageAmount
      val miss = 0
      val dps = 0
      val total = damageAmount
      val totalPercent = 0
      damageDoneSheetDataMap((damageSource,target)) = (hits,normHits,critHits,norm,critVal,avg,miss,dps,total,totalPercent)
    }

    /**
     * Reflect Damage
     */
    if (reflectAbilityList.contains(damageSource)){
      reflectDamage = reflectDamage + damageAmount
      if (damageSource == "Sonic Rebounder") {
        rebounderDamage = rebounderDamage + damageAmount
      }
    }


  }





  // TODO: Can we get rid of damageTypeTaken and make this all based off of damageTakenStats
  // damage taken stats: Map(DamageType -> (Ability -> Amount)
  // TODO: Add DTPS graph functionality, make it toggleable in the UI
  def updateDamageTaken(damageAmount: Int, axisValue : Int, damageType : String, damageSource : String, crit:Boolean, performer: String): Unit = {
    damageTaken += damageAmount
    damageTakenPerSecond = damageTaken / (axisValue+1)
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
     * Update damageTakenTimeSeries
     * */
    // check if we already have damage done in this second
    if (damageTakenTimeSeries.contains(axisValue)){
      val newDamageInBucket : Int = damageTakenTimeSeries.get(axisValue).get + damageAmount
      damageTakenTimeSeries += (axisValue -> newDamageInBucket)
    }
    // if we don't have any data for this second yet, add that key value
    else {
      damageTakenTimeSeries(axisValue) = damageAmount
    }

    /**
     * Update damageTakenPerSecondTimeSeries
     */
    if (damageTakenPerSecondTimeSeries.contains(axisValue)){
      damageTakenPerSecondTimeSeries += (axisValue -> damageTaken / (axisValue + 1))
    }
    // if we don't have any data for this second yet, add that key value
    else {
      damageTakenPerSecondTimeSeries(axisValue) = damageTaken / (axisValue + 1)
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

    /**
     * for a 1d approach, always add all damage to an empty damage type.
     * This supports our overlays that only have 1 ring
     */
    // see if we have seen this type
    if (damageTaken1DStats.contains("")){
      // if we have seen this type, check the inner key and see if we have this ability
      if (damageTaken1DStats("").contains(damageSource)) {
        // if we have the ability update the damage amount for that ability
        val newDamageValue = damageTaken1DStats("")(damageSource) + damageAmount
        damageTaken1DStats("") += (damageSource -> newDamageValue)
      } else {
        // if we dont have that ability add it to the inner map
        damageTaken1DStats("")(damageSource) = damageAmount
      }
    }
    // if we have nothing for this damage type, we know we can just add the stats
    else {
      damageTaken1DStats("") = mutable.Map(damageSource -> damageAmount)
    }



    /**
     * Update Spreadsheet data
     */
    if (damageTakenSheetDataMap.contains((damageSource,performer))){
      val current = damageTakenSheetDataMap((damageSource,performer))
      val hits = current._1 + 1
      val normHits = if (crit) {
        current._2
      } else {
        current._2 + 1
      }
      val critHits = if (crit) {
        current._3 + 1
      } else {
        current._3
      }
      val norm = if (crit) {
        current._4
      } else {
        (current._4 + damageAmount) / hits
      }
      val critVal = if (crit) {
        current._5 + damageAmount
      } else {
        current._5
      }
      val avg = (current._6 + damageAmount) / hits
      val miss = 0
      val dps = 0
      val total = current._9 + damageAmount
      val totalPercent = 0

      damageTakenSheetDataMap((damageSource,performer)) = (hits,normHits,critHits,norm,critVal,avg,miss,dps,total,totalPercent)

    } else {
      val hits = 1
      val normHits = if (crit) 0 else 1
      val critHits = if (crit) 1 else 0
      val norm = if (crit) 0 else damageAmount
      val critVal = if (crit) damageAmount else 0
      val avg = damageAmount
      val miss = 0
      val dps = 0
      val total = damageAmount
      val totalPercent = 0
      damageTakenSheetDataMap((damageSource,performer)) = (hits,normHits,critHits,norm,critVal,avg,miss,dps,total,totalPercent)
    }

  }

  // TODO: Add fancy pie charts for heal taken and done on healing tab
  def updateHealingDone(healAmount: Int, axisValue : Int, healType : String, healSource : String,crit:Boolean,target:String): Unit = {
    healingDone += healAmount
    healingDonePerSecond = healingDone / (axisValue+1)

    /**
     * Update healingDoneTimeSeries
     * */
    // check if we already have damage done in this second
    if (healingDoneTimeSeries.contains(axisValue)){
      val newHealingInBucket : Int = healingDoneTimeSeries.get(axisValue).get + healAmount
      healingDoneTimeSeries += (axisValue -> newHealingInBucket)
    }
    // if we don't have any data for this second yet, add that key value
    else {
      healingDoneTimeSeries(axisValue) = healAmount
    }

    /**
     * Update healingPerSecondTimeSeries
     */
    if (healingPerSecondTimeSeries.contains(axisValue)){
      healingPerSecondTimeSeries += (axisValue -> healingDone / (axisValue + 1))
    }
    // if we don't have any data for this second yet, add that key value
    else {
      healingPerSecondTimeSeries(axisValue) = healingDone / (axisValue + 1)
    }

    /**
     * Healing Done Stats, can we combine this and get rid of the above?
     */
    // see if we have seen this type, note, most (all?) heals are of no type
    if (healingDoneStats.contains(healType)){
      // if we have seen this type, check the inner key and see if we have this ability
      if (healingDoneStats(healType).contains(healSource)) {
        // if we have the ability update the heal amount for that ability
        val newHealValue = healingDoneStats(healType)(healSource) + healAmount
        healingDoneStats(healType) += (healSource -> newHealValue)
      } else {
        // if we dont have that ability add it to the inner map
        healingDoneStats(healType)(healSource) = healAmount
      }
    }
    // if we have nothing for this damage type, we know we can just add the stats
    else {
      healingDoneStats(healType) = mutable.Map(healSource -> healAmount)
    }


    /**
     * Update Spreadsheet data
     */
    // TODO: Abstract spreadsheet data to a function
    if (healingDoneSheetDataMap.contains((healSource,target))){
      val current = healingDoneSheetDataMap((healSource,target))
      val hits = current._1 + 1
      val normHits = if (crit) {
        current._2
      } else {
        current._2 + 1
      }
      val critHits = if (crit) {
        current._3 + 1
      } else {
        current._3
      }
      val norm = if (crit) {
        current._4
      } else {
        // TODO: This needs to be normal hits not total hits
        (current._4 + healAmount) / hits
      }
      val critVal = if (crit) {
        current._5 + healAmount
      } else {
        current._5
      }
      val avg = (current._6 + healAmount) / hits
      val miss = 0
      val dps = 0
      val total = current._9 + healAmount
      val totalPercent = 0

      healingDoneSheetDataMap((healSource,target)) = (hits,normHits,critHits,norm,critVal,avg,miss,dps,total,totalPercent)

    } else {
      val hits = 1
      val normHits = if (crit) 0 else 1
      val critHits = if (crit) 1 else 0
      val norm = if (crit) 0 else healAmount
      val critVal = if (crit) healAmount else 0
      val avg = healAmount
      val miss = 0
      val dps = 0
      val total = healAmount
      val totalPercent = 0
      healingDoneSheetDataMap((healSource,target)) = (hits,normHits,critHits,norm,critVal,avg,miss,dps,total,totalPercent)
    }

  }

  def updateHealingTaken(healAmount: Int, axisValue : Int, healType : String, healSource : String, crit: Boolean, performer: String): Unit = {
    healingTaken += healAmount
    healingTakenPerSecond = healingTaken / (axisValue+1)


    /**
     * Update healingTakenTimeSeries
     * */
    // check if we already have damage done in this second
    if (healingTakenTimeSeries.contains(axisValue)){
      val newHealingInBucket : Int = healingTakenTimeSeries.get(axisValue).get + healAmount
      healingTakenTimeSeries += (axisValue -> newHealingInBucket)
    }
    // if we don't have any data for this second yet, add that key value
    else {
      healingTakenTimeSeries(axisValue) = healAmount
    }


    /**
     * Update healingTakenPerSecondTimeSeries
     */
    if (healingTakenPerSecondTimeSeries.contains(axisValue)){
      healingTakenPerSecondTimeSeries += (axisValue -> healingTaken / (axisValue + 1))
    }
    // if we don't have any data for this second yet, add that key value
    else {
      healingTakenPerSecondTimeSeries(axisValue) = healingTaken / (axisValue + 1)
    }


    /**
     * Damage Taken Stats, can we combine this and get rid of the above?
     */
    // see if we have seen this type
    if (healingTakenStats.contains(healType)){
      // if we have seen this type, check the inner key and see if we have this ability
      if (healingTakenStats(healType).contains(healSource)) {
        // if we have the ability update the damage amount for that ability
        val newHealValue = healingTakenStats(healType)(healSource) + healAmount
        healingTakenStats(healType) += (healSource -> newHealValue)
      } else {
        // if we dont have that ability add it to the inner map
        healingTakenStats(healType)(healSource) = healAmount
      }
    }
    // if we have nothing for this heal type, we know we can just add the stats
    else {
      healingTakenStats(healType) = mutable.Map(healSource -> healAmount)
    }

    /**
     * Update Spreadsheet data
     */
    // TODO: Abstract spreadsheet data to a function
    if (healingTakenSheetDataMap.contains((healSource,performer))){
      val current = healingTakenSheetDataMap((healSource,performer))
      val hits = current._1 + 1
      val normHits = if (crit) {
        current._2
      } else {
        current._2 + 1
      }
      val critHits = if (crit) {
        current._3 + 1
      } else {
        current._3
      }
      val norm = if (crit) {
        current._4
      } else {
        // TODO: This needs to be normal hits not total hits
        (current._4 + healAmount) / hits
      }
      val critVal = if (crit) {
        current._5 + healAmount
      } else {
        current._5
      }
      val avg = (current._6 + healAmount) / hits
      val miss = 0
      val dps = 0
      val total = current._9 + healAmount
      val totalPercent = 0

      healingTakenSheetDataMap((healSource,performer)) = (hits,normHits,critHits,norm,critVal,avg,miss,dps,total,totalPercent)

    } else {
      val hits = 1
      val normHits = if (crit) 0 else 1
      val critHits = if (crit) 1 else 0
      val norm = if (crit) 0 else healAmount
      val critVal = if (crit) healAmount else 0
      val avg = healAmount
      val miss = 0
      val dps = 0
      val total = healAmount
      val totalPercent = 0
      healingTakenSheetDataMap((healSource,performer)) = (hits,normHits,critHits,norm,critVal,avg,miss,dps,total,totalPercent)
    }

  }


  def updateThreat(threatAmount: Int, axisValue : Int, threatType: String, threatSource : String): Unit = {
    threatDone += threatAmount
    threatDonePerSecond = threatDone / (axisValue+1)

    /**
     * Update threatDoneTimeSeries
     * */
    // check if we already have damage done in this second
    if (threatDoneTimeSeries.contains(axisValue)){
      val newThreatInBucket : Int = threatDoneTimeSeries.get(axisValue).get + threatAmount
      threatDoneTimeSeries += (axisValue -> newThreatInBucket)
    }
    // if we don't have any data for this second yet, add that key value
    else {
      threatDoneTimeSeries(axisValue) = threatAmount
    }

    /**
     * Update threatPerSecondTimeSeries
     */
    if (threatPerSecondTimeSeries.contains(axisValue)){
      threatPerSecondTimeSeries += (axisValue -> threatDone / (axisValue + 1))
    }
    // if we don't have any data for this second yet, add that key value
    else {
      threatPerSecondTimeSeries(axisValue) = threatDone / (axisValue + 1)
    }

    /**
     * threat Done Stats, can we combine this and get rid of the above?
     */
    // see if we have seen this type, note, most (all?) threats are of no type
    if (threatDoneStats.contains(threatType)){
      // if we have seen this type, check the inner key and see if we have this ability
      if (threatDoneStats(threatType).contains(threatSource)) {
        // if we have the ability update the threat amount for that ability
        val newthreatValue = threatDoneStats(threatType)(threatSource) + threatAmount
        threatDoneStats(threatType) += (threatSource -> newthreatValue)
      } else {
        // if we dont have that ability add it to the inner map
        threatDoneStats(threatType)(threatSource) = threatAmount
      }
    }
    // if we have nothing for this damage type, we know we can just add the stats
    else {
      threatDoneStats(threatType) = mutable.Map(threatSource -> threatAmount)
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

    if (a.isInstanceOf[Player]){
      actorType = "Player"
    }
    else if (a.isInstanceOf[Companion]){
      actorType = "Companion"
    }
    else {
      actorType = "Other"
    }
  }

}

package Combat

import eu.hansolo.tilesfx.chart.ChartData
import parsing.Actors.Actor
import parsing.subTypes.ActorId
import patterns.LogInformation

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


  // Chart Data is going to hold the UI information for this CombatActorInstance
  var damageDoneTimeSeries : Vector[(Int,java.time.Instant)] = null
  var damageDone = 0
  def updateDamageDone(damageAmount: Int, axisValue : java.time.Instant): Unit = {
    damageDone += damageAmount
//    val chartData = new ChartData(damageAmount,axisValue)
//    println(s"Created chart data ${chartData}")
    if (damageDoneTimeSeries == null) damageDoneTimeSeries = Vector((damageAmount,axisValue))
    else {
      println(s"Updating damage done time sereis with ${damageAmount} at ${axisValue}")
      damageDoneTimeSeries = damageDoneTimeSeries :+ (damageAmount,axisValue)
    }

  }
  def getDamageDone() = damageDone
  def getDamageDoneTimeSeries() = damageDoneTimeSeries

  var damageTaken = 0
  def updateDamageTaken(i: Int): Unit = damageTaken += i
  def getDamageTaken() = damageTaken




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

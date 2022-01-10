package Combat

import parsing.Actors.{Actor, NoneActor}
import parsing.Values.Value
import patterns.LogInformation

import scala.collection.IterableOnce.iterableOnceExtensionMethods

class CombatInstance (

                     ){

  var combatActors : Vector[Actor] = Vector()

  // Actor ID : String, Damage: Int
  var combatDamage : Map[String,Int] = Map()

  def appendToCombatActors(a:Actor): Unit = {
    /**
     * Right now, the way the parser works, is it returns up to two new actor object with every line parsed.
     *
     * That means, with two back to back lines where the actor @Xan does something, two @Xan Actor objects are returned.
     *
     * We dont want 20 @Xan actors, we only want one, so here we need to check the actor set for an actor with that
     * id already in it. If that actor is in the set, we should remove it and insert the new actor object that has
     * an updated health and position information.
     *
     * This is difficult to do with a set, so I have changed combat Actors back to a vector
     */

    if (a == null || a.isInstanceOf[NoneActor]) return

    // iterate through combatActors and if we find the same id, then remove that actor
    for (actor <- combatActors) {
      if (actor.isInstanceOf[NoneActor]){

      }
      else if (actor.getId().compare(a.getId())) {
        combatActors = combatActors.filterNot(_.getId() eq actor.getId())

      }
    }
    // regardless of the filter operation, add the new actor in
    combatActors = combatActors :+ a
  }

  def getCombatActors() : Vector[Actor] = combatActors

  def getLastCurrentCombatActor(): Actor = combatActors.last

  def addDamageToCurrentCombat(logInfo : LogInformation): Unit = {
    //access the combat damage map, if it contains the String id for the actor creating damage, add to the int
    val id: String = logInfo.getPerformer().getId().toString
    val totalValue : Int = logInfo.getResulValue().getTotalValue()

    if (combatDamage.contains(id)) {
      combatDamage = combatDamage.updated(id,combatDamage(id) + totalValue)
    } else {
      combatDamage = combatDamage updated (id,totalValue)
    }

  }

  def getCombatDamage() = combatDamage


}

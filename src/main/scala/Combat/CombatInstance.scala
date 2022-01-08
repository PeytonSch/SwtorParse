package Combat

import parsing.Actors.Actor
import patterns.LogInformation

import scala.collection.IterableOnce.iterableOnceExtensionMethods

class CombatInstance (

                     ){

  var combatActors : Vector[Actor] = Vector()

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

    if (a == null) return

    // iterate through combatActors and if we find the same id, then remove that actor
    println(s"Received actor: ${a.getId()}, checking to see if this actor is in the following: ")
    combatActors.foreach(println)
    for (actor <- combatActors) {
      if (actor.getId().compare(a.getId())) {
        combatActors = combatActors.filterNot(_.getId() eq actor.getId())
      }
    }
    // regardless of the filter operation, add the new actor in
    combatActors = combatActors :+ a
  }

  def getCombatActors() : Vector[Actor] = combatActors

  def addDamageToCurrentCombat(logInfo : LogInformation): Unit = {

  }


}

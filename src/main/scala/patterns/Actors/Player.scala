package patterns.Actors

import patterns.subTypes.{Health, Position}

class Player (
               name : String,
               position : Position,
               health :  Health
            ) extends Actor {

  override def toString: String = name + " " + position + " " + health

  override def isPlayer(): Boolean = true

  /**
   * Parse a string containing an actor and return an instance of actor
   * @param actorString
   * @return
   */


}

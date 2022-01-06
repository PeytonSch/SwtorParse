package parsing.Actors

import parsing.subTypes.{Health, Position}

class Player (
               name : String,
               position : Position,
               health :  Health
            ) extends Actor {

  override def toString: String = name + " " + position + " " + health

  override def isPlayer(): Boolean = true

  override def getName(): String = name

  override def getPosition(): Position = position

  override def getHealth(): Health = health


}

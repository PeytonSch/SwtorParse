package parsing.Actors

import parsing.subTypes.{Health, ActorId, Position}

class Npc (
            name : String,
            id : ActorId,
            position : Position,
            health :  Health
          ) extends Actor {

  override def toString: String = name + " " + id + " " + position + " " + health

  override def isPlayer(): Boolean = false

  override def getName(): String = name

  override def getPosition(): Position = position

  override def getHealth(): Health = health

  override def getId() = id
}

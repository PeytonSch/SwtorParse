package patterns.Actors

import patterns.subTypes.{Health, Id, Position}

class Npc (
            name : String,
            id : Id,
            position : Position,
            health :  Health
          ) extends Actor {

  override def toString: String = name + " " + id + " " + position + " " + health

  override def isPlayer(): Boolean = false

  override def getName(): String = name

  override def getPosition(): Position = position

  override def getHealth(): Health = health

  def getId(): Id = id

}

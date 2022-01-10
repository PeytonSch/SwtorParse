package parsing.Actors

import parsing.subTypes.{ActorId, Health, Position}

class Player (
               name : String,
               position : Position,
               health :  Health
            ) extends Actor {

  // honestly we should make this the id numbers after the name and not the name but this is easy and will work
  val id : ActorId = new ActorId(name,name)

  override def toString: String = name + " " + position + " " + health

  override def isPlayer(): Boolean = true

  override def getName(): String = name

  override def getPosition(): Position = position

  override def getHealth(): Health = health

  override def getId = id


}

package parsing.Actors

import parsing.subTypes.{Health, ActorId, Position}

trait Actor {

  def isPlayer(): Boolean
  def getName(): String
  def getPosition(): Position
  def getHealth(): Health

  def getId(): ActorId

  override def toString: String = "Err, this actor string needs to be overridden"



}

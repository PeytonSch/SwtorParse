package patterns.Actors

import patterns.subTypes.{Health, Id, Position}

trait Actor {

  def isPlayer(): Boolean
  def getName(): String
  def getPosition(): Position
  def getHealth(): Health

  override def toString: String = "Err, this actor string needs to be overridden"



}

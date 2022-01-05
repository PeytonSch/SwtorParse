package patterns.Actors

import patterns.subTypes.{Id, Position}

trait Actor {

  def isPlayer(): Boolean

  override def toString: String = "Err, this actor string needs to be overridden"



}

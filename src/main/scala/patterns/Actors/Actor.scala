package parsing.Actors

import parsing.subTypes.{Health, ActorId, Position}

trait Actor {

  def isPlayer(): Boolean
  def getName(): String
  def getPosition(): Position
  def getHealth(): Health

  def getId(): ActorId

  def getPrettyNameWithInstanceIdIfNecessary(): String = {
    if (isPlayer()) {
      getName()
    }
    else {
      s"${getName()} : ${getId().getInstanceId()}"
    }
  }

  override def toString: String = "Err, this actor string needs to be overridden"

  /**
   * This is untested, wrote it for a test and didn't use it.
   * @param a
   * @return
   */
  def compareActors(a: Actor): Boolean = {
    if (this.toString == a.toString) {
      true
    }
    else {
      false
    }
  }



}

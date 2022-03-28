package parsing.subTypes

import logger.Logger

class Health (
             current : Int,
             max : Int
             ){

  override def toString: String = "[ Current Health: " + current + " Max Health: " + max + " ]"
  def getCurrent() = {
    current
  }
  def getMax() = max
}

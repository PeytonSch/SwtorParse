package parsing.subTypes

import logger.Logger

class Health (
             current : Int,
             max : Int
             ){

  override def toString: String = "[ Current Health: " + current + " Max Health: " + max + " ]"
  def getCurrent() = {
    Logger.highlight(s"Got Current Health of: ${current}")
    current
  }
  def getMax() = max
}

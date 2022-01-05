package patterns

import patterns.Actors.Actor
import patterns.subTypes.LogTimestamp

class BaseInformation(
                     timestamp: LogTimestamp,
                     performer: Actor
                     ) {

  override def toString: String = timestamp + " " + performer




}
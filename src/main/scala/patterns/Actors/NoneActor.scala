package patterns.Actors
import patterns.subTypes.{Health, Position}

// TODO: This is not the best way to handle this, should probably make Actors Options
class NoneActor extends Actor {
  override def isPlayer(): Boolean = false

  override def getName(): String = ""

  override def getPosition(): Position = new Position(0,0,0,0)

  override def getHealth(): Health = new Health(0,0)
}

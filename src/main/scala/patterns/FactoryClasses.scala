package patterns

import patterns.Actors.{Actor, Companion, Npc, Player}
import patterns.subTypes.{Health, Id, LogTimestamp, Position}

class FactoryClasses {

  def timestampFromLine(line : String): LogTimestamp = {
    new LogTimestamp(line.split(']')(0).split('[')(1))
  }

  def actorFromString(logLine: String): Actor = {
    val actorString : String = logLine.split('[')(2).split(']')(0)
    // First determine if the actor is a player or npc
    // a player has an @ symbol, a companion has an @ and /
    // and an npc has neither
    val isPlayer: Boolean = actorString.contains('@')
    val isCompanion: Boolean = actorString.contains(Seq('@', '/'))

    // if it is a companion it does not contain an id
    // [@Heavy Sloth#689203382607232/Arcann {3915326546771968}:26518005410002|(-25.10,-63.50,-1.76,-142.73)|(2944/2944)]
    if (isCompanion) {
      val actorName = actorString.split('/')(1).split('{')(0).trim
      val actorTypeID = actorString.split('{')(1).split('}')(0).toInt
      val actorInstanceID = actorString.split(':')(1).split('|')(0).toInt
      val x_pos = actorString.split('(')(1).split(')')(0).split(',')(0).toDouble
      val y_pos = actorString.split('(')(1).split(')')(0).split(',')(1).toDouble
      val z_pos = actorString.split('(')(1).split(')')(0).split(',')(2).toDouble
      val dir_pos = actorString.split('(')(1).split(')')(0).split(',')(3).toDouble
      val current_health = actorString.split('(')(2).split('/')(0).toInt
      val max_health = actorString.split('(')(2).split('/')(1).dropRight(1).toInt
      new Companion(actorName, new Id(actorTypeID, actorInstanceID), new Position(x_pos, y_pos, z_pos, dir_pos), new Health(current_health, max_health))
    }
    // if it is a player it does not contain an id
    // [@Heavy Sloth#689203382607232|(-65.44,-57.60,-0.14,-83.93)|(2909/2909)]
    else if (isPlayer) {
      val actorName = actorString.split('@')(1).split('#')(0)
      val x_pos = actorString.split('(')(1).split(')')(0).split(',')(0).toDouble
      val y_pos = actorString.split('(')(1).split(')')(0).split(',')(1).toDouble
      val z_pos = actorString.split('(')(1).split(')')(0).split(',')(2).toDouble
      val dir_pos = actorString.split('(')(1).split(')')(0).split(',')(3).toDouble
      val current_health = actorString.split('(')(2).split('/')(0).toInt
      val max_health = actorString.split('(')(2).split('/')(1).dropRight(1).toInt
      new Player(actorName, new Position(x_pos, y_pos, z_pos, dir_pos), new Health(current_health, max_health))
    }
    // [Rival Acolyte {287749923930112}:26518005413169|(1.99,-125.04,-11.44,0.00)|(221/345)]
    else {
      val actorName = actorString.split('[')(1).split('{')(0).trim
      val actorTypeID = actorString.split('{')(1).split('}')(0).toInt
      val actorInstanceID = actorString.split(':')(1).split('|')(0).toInt
      val x_pos = actorString.split('(')(1).split(')')(0).split(',')(0).toDouble
      val y_pos = actorString.split('(')(1).split(')')(0).split(',')(1).toDouble
      val z_pos = actorString.split('(')(1).split(')')(0).split(',')(2).toDouble
      val dir_pos = actorString.split('(')(1).split(')')(0).split(',')(3).toDouble
      val current_health = actorString.split('(')(2).split('/')(0).toInt
      val max_health = actorString.split('(')(2).split('/')(1).dropRight(1).toInt
      new Npc(actorName, new Id(actorTypeID, actorInstanceID), new Position(x_pos, y_pos, z_pos, dir_pos), new Health(current_health, max_health))
    }
  }

  def baseInformationFromLine(logLine : String) = {
    new BaseInformation(timestampFromLine(logLine),actorFromString(logLine))
  }

}



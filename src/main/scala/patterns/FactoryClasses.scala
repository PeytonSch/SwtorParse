package patterns

import patterns.Actions.Action
import patterns.Actors.{Actor, Companion, NoneActor, Npc, Player}
import patterns.Result.{ApplyEffect, Result}
import patterns.subTypes.{ActorId, Health, LogTimestamp, Position}

class FactoryClasses {

  def timestampFromLine(line : String): LogTimestamp = {
    new LogTimestamp(line.split(']')(0).split('[')(1))
  }
  def targetActorFromLogLineString(logLine: String): Actor = {
    val actorString : String = logLine.split('[')(3).split(']')(0)
    // Check if the actor is empty, if so skip
    if (actorString == "") return new NoneActor
    // Check if the actor is the performer, if so skip
    if (actorString == "=") return performingActorFromLogLineString(logLine)

    actorFromActorString(actorString)
  }
  def performingActorFromLogLineString(logLine: String): Actor = {
    val actorString : String = logLine.split('[')(2).split(']')(0)
    actorFromActorString(actorString)
  }

  /**
   * Parse a string containing an actor and return an instance of actor
   * @param actorString
   * @return
   */
  def actorFromActorString(actorString: String): Actor = {
    // First determine if the actor is a player and companion or npc
    // a player has an @ symbol, a companion has an @ and /
    // and an npc has neither
    val isPlayerOrComp: Boolean = actorString.contains('@')

    // if it is a player it does not contain an id
    // [@Heavy Sloth#689203382607232|(-65.44,-57.60,-0.14,-83.93)|(2909/2909)]
    if (isPlayerOrComp) {
      val actorName = actorString.split('@')(1).split('#')(0)

      // If this happens it is a companion!
      // [@Heavy Sloth#689203382607232/Arcann {3915326546771968}:26518005410002|(-25.10,-63.50,-1.76,-142.73)|(2944/2944)]
      if (actorString.split('#')(1).split('|')(0).contains('{')) {
        val companionName = actorString.split('/')(1).split('{')(0).trim
        val actorTypeID = actorString.split('{')(1).split('}')(0)
        val actorInstanceID = actorString.split(':')(1).split('|')(0)
        val x_pos = actorString.split('(')(1).split(')')(0).split(',')(0).toDouble
        val y_pos = actorString.split('(')(1).split(')')(0).split(',')(1).toDouble
        val z_pos = actorString.split('(')(1).split(')')(0).split(',')(2).toDouble
        val dir_pos = actorString.split('(')(1).split(')')(0).split(',')(3).toDouble
        val current_health = actorString.split('(')(2).split('/')(0).toInt
        val max_health = actorString.split('(')(2).split('/')(1).dropRight(1).toInt
        new Companion(companionName, new ActorId(actorTypeID, actorInstanceID), new Position(x_pos, y_pos, z_pos, dir_pos), new Health(current_health, max_health))

      }
      else {
        val x_pos = actorString.split('(')(1).split(')')(0).split(',')(0).toDouble
        val y_pos = actorString.split('(')(1).split(')')(0).split(',')(1).toDouble
        val z_pos = actorString.split('(')(1).split(')')(0).split(',')(2).toDouble
        val dir_pos = actorString.split('(')(1).split(')')(0).split(',')(3).toDouble
        val current_health = actorString.split('(')(2).split('/')(0).toInt
        val max_health = actorString.split('(')(2).split('/')(1).dropRight(1).toInt
        new Player(actorName, new Position(x_pos, y_pos, z_pos, dir_pos), new Health(current_health, max_health))
      }
    }
    // [Rival Acolyte {287749923930112}:26518005413169|(1.99,-125.04,-11.44,0.00)|(221/345)]
    else {
      val actorName = actorString.split('{')(0).trim
      val actorTypeID = actorString.split('{')(1).split('}')(0)
      val actorInstanceID = actorString.split(':')(1).split('|')(0)
      val x_pos = actorString.split('(')(1).split(')')(0).split(',')(0).toDouble
      val y_pos = actorString.split('(')(1).split(')')(0).split(',')(1).toDouble
      val z_pos = actorString.split('(')(1).split(')')(0).split(',')(2).toDouble
      val dir_pos = actorString.split('(')(1).split(')')(0).split(',')(3).toDouble
      val current_health = actorString.split('(')(2).split('/')(0).toInt
      val max_health = actorString.split('(')(2).split('/')(1).dropRight(1).toInt
      new Npc(actorName, new ActorId(actorTypeID, actorInstanceID), new Position(x_pos, y_pos, z_pos, dir_pos), new Health(current_health, max_health))
    }
  }

  def baseInformationFromLine(logLine : String) = {
    new BaseInformation(timestampFromLine(logLine),performingActorFromLogLineString(logLine))
  }

  def actionFromLine(logLine: String): Action = {
    val name = logLine.split('[')(4).split('{')(0).trim
    // If it is an empty action, name will just be ']' and we should move on
    if (name == "]") return new Action("","")
    val id = logLine.split('[')(4).split('{')(1).split('}')(0)
    new Action(name,id)

  }


  def resultFromLine(logLine: String) : Result = {
    val name = logLine.split('[')(5).split('{')(0).trim
    val effectId = logLine.split('[')(5).split('{')(1).split('}')(0)
    val resultType = logLine.split('[')(5).split(':')(1).split('{')(0).trim
    val resultTypeId = logLine.split('[')(5).split(':')(1).split('{')(1).split('}')(0).trim

    // TODO: These should not all be Apply Effects
    if (name == "ApplyEffect") {
      new ApplyEffect(name,effectId,resultType,resultTypeId)
    }
    else {
      // TODO: This is wrong, just a place holder
      new ApplyEffect(name,effectId,resultType,resultTypeId)
    }


  }

}



package parsing

import logger.Logger
import parsing.Actions.DefaultAction
import parsing.Actors.{Actor, Companion, NoneActor, Npc, Player}
import parsing.Result.{ApplyEffect, Event, RemoveEffect, Result}
import parsing.Threat.ThreatValue
import parsing.Values.{CompleteNegation, NoValue, PartialNegation, RegularValue, Value}
import parsing.subTypes.{ActorId, Health, LogTimestamp, Position}
import parsing.Result.AreaEntered
import patterns.Actions.{Action, NoAction, SafeLogin}
import patterns.Result.{EnterCombat, ExitCombat}

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
    Logger.trace(s"actorFromActorString: ${actorString}")
    // First determine if the actor is a player and companion or npc
    // a player has an @ symbol, a companion has an @ and /
    // and an npc has neither
    val isPlayerOrComp: Boolean = if (actorString.contains("@UNKNOWN")) {
      false
    } else {
      actorString.contains('@')
    }

    // if it is a player it does not contain an id
    // [@Heavy Sloth#689203382607232|(-65.44,-57.60,-0.14,-83.93)|(2909/2909)]
    if (isPlayerOrComp) {
      val actorName = actorString.split('@')(1).split('#')(0)

      // If this happens it is a companion!
      // [@Heavy Sloth#689203382607232/Arcann {3915326546771968}:26518005410002|(-25.10,-63.50,-1.76,-142.73)|(2944/2944)]
      if (actorString.contains('#') && actorString.split('#')(1).split('|')(0).contains('{')) {
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
      if (actorString == "") {
        new NoneActor
      }
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
  }

  def actionFromLine(logLine: String): Action = {
    val name = logLine.split('[')(4).split('{')(0).trim
    // If it is an empty action, name will just be ']' and we should move on
    if (name == "]") return new NoAction()
    val id = logLine.split('[')(4).split('{')(1).split('}')(0)
    if (name == "Safe Login") {
      new SafeLogin(name,id)
    } else {
      new DefaultAction(name,id)
    }

  }


  def resultFromLine(logLine: String) : Result = {
    Logger.trace(s"resultFromLine: ${logLine}")
    val resultType = logLine.split('[')(5).split('{')(0).trim
    Logger.trace(s"Result Type: ${resultType}")
    val effectId = logLine.split('[')(5).split('{')(1).split('}')(0)
    Logger.trace(s"effectId: ${effectId}")
    val name = logLine.split('[')(5).split(':')(1).split('{')(0).trim
    Logger.trace(s"Result Name: ${name}")
    // TODO: Some Lines have extra [ in them, like Accuracy Reduced [Tech]
    var nameId = "0"
    try {
      nameId = logLine.split('[')(5).split('{')(2).split('}')(0).trim
    }
    catch {
      case e: ArrayIndexOutOfBoundsException => Logger.warn(s"Line failed to parse result nameId ${logLine}")
    }

    if (resultType == "ApplyEffect") {
      new ApplyEffect(resultType,effectId,name,nameId)
    }
    else if (resultType == "RemoveEffect") {
      new RemoveEffect(resultType,effectId,name,nameId)
    }
    else if (resultType == "Event") {
      if (name == "EnterCombat"){
        new EnterCombat(resultType,effectId,name,nameId)
      } else if (name == "ExitCombat") {
        new ExitCombat(resultType,effectId,name,nameId)
      } else {
        new Event(resultType,effectId,name,nameId)
      }

    }
    else if (resultType == "AreaEntered") {
      new AreaEntered(resultType,effectId,name,nameId)
    }
    else {
      // TODO: This is wrong, just a place holder
      new ApplyEffect("ERROR","Err","Else Case Hit","Err")
    }


  }

  def valueFromLine(logLine: String): Value = {

    Logger.trace(s"valueFromLine: ${logLine}")

    // we need to handle values differently if the result is an AreaEntered
    val resultType = logLine.split('[')(5).split('{')(0).trim
    if (resultType == "AreaEntered") {
      new NoValue
    } else {


      // First see if this line even contains a value
      val lineArray = logLine.split(']')
      if (lineArray.size > 5) {
        // Dont forget to trim off threat
        val extractedValue = logLine.split(']')(5).split('<')(0).trim
        Logger.trace("Contains a value: " + extractedValue)
        if (extractedValue.size < 1){
          return new NoValue
        }
        // If it does contain a value, determine how many parts it has

        /**
         * We need to see if the value line has any of the following
         * - is it a critical value
         * - does it have an excess amount
         * - does it have a value type, if so it also has an ID
         * - does it have a negation, if so it also has an ID
         * - There are two types of negations, partial negations with values and complete negations without
         *    - Maybe check if value is 0 for negation?
         */

        // Check if the value is 0
        // If it is 0, get complete negation information and return
        if (extractedValue(1) == '0' && extractedValue(2) != ')' && extractedValue != "(0 -)") {
          //println(s"Extracted: ${extractedValue} from ${logLine}")
          val negationType = extractedValue.split('-')(1).split('{')(0).trim
          val negatopmTypeId = extractedValue.split('-')(1).split('{')(1).split('}')(0).trim

          //println(s"Log line contains a complete negation: " +
          //  s"\n ${extractedValue} with negation ${negationType} with id ${negatopmTypeId}")
          new CompleteNegation(negationType, negatopmTypeId)
        }
        else {
          /**
           * These are the parts needed in a regular value. Using vars because it just makes sense
           */
          val containsCrit = extractedValue.split(' ')(0).contains('*')
          // if there is a crit drop the * off the base value
          var baseValue: Int = 0
          if (containsCrit) {
            val part = extractedValue.split(" ")(0).drop(1).trim.dropRight(1)
            if (part.contains('*')){
              baseValue = part.dropRight(1).toInt
            }
            else {
              baseValue = part.toInt
            }
//            baseValue = extractedValue.split(" ")(0).drop(1).trim.dropRight(1).toInt
          } else {
            val part = extractedValue.split(" ")(0).drop(1).trim
            if (part.contains(')')){
              baseValue = part.dropRight(1).toInt
            }
            else {
              // TODO: This is caused by the extra brackets as well:  Accuracy Reduced [Tech]
              try {
                baseValue = part.toInt
              }
              catch {
                case e: NumberFormatException => Logger.warn(s"Failed to parse line, number format exception: ${logLine}")
              }
            }
          }
          var excessValue: Int = 0
          var valueType: String = ""
          var valueTypeId: String = ""
          var partialNegation: PartialNegation = new PartialNegation("", "", 0, "", "")

          var containsExcess = false
          try {
            containsExcess = extractedValue.split(' ')(1).contains('~')
          } catch {
            case e: ArrayIndexOutOfBoundsException => // this happens on simple values like (861)
            case e: Throwable => println(s"Error: ${e}")
          }

          if (containsExcess) {
            // This handles values like (1764 ~99) where there is no space at the end
            if (extractedValue.split('~')(1).split(' ')(0).contains(')')) {
              excessValue = extractedValue.split('~')(1).split(' ')(0).dropRight(1).toInt
            }
            else {
              excessValue = extractedValue.split('~')(1).split(' ')(0).toInt
            }
            //println(s"Extracted Excess Value: ${excessValue} from ${extractedValue}")
          }

          // Extract the type, if there even is one it changes position depending on if there was an excess value
          // if it contains an excess it will be after excess if there is one
          if (containsExcess) {
            try {
              valueType = extractedValue.split('~')(1).split(' ')(1).split(' ')(0).trim
              valueTypeId = extractedValue.split('~')(1).split('{')(1).split('}')(0).trim
              //println(s"Extracted value type ${valueType} : ${valueTypeId} from ${extractedValue}")
            }
            catch {
              // If there is an index out of bound exception, it is because there is a line like (2022* ~0) which does not have a value type
              case e: IndexOutOfBoundsException =>
              case e: Throwable => println(s"Could not extract value type for ${extractedValue} + ${e}")
            }
          }
          // if it does not contain excess try to extract type
          else {
            // if it does not contain excess and was not a complete negation, I beleive it will always
            // be at the second index

            try {
              valueType = extractedValue.split(' ')(1).split('{')(0).trim
              valueTypeId = extractedValue.split('{')(1).split('}')(0).trim
              //println(s"Extracted value type ${valueType} : ${valueTypeId} from ${extractedValue}")
            }
            catch {
              case _:ArrayIndexOutOfBoundsException => //this happens on simple values like (861)
              case e : Throwable => println("Error: " + e)
            }
          }

          // Check for partial negation
          // at this point a - will only exist if there is a partial negation
          // for some reason there is this case with a random -) though as in this line: (51 ~0 energy {836045448940874} -)"
          if (extractedValue.contains('-') && !extractedValue.contains("-)")) {
            val negationType = extractedValue.split('-')(1).split(' ')(0).trim
            val negationId = extractedValue.split('-')(1).split('{')(1).split('}')(0).trim
            val negationAmount = extractedValue.split('-')(1).split('(')(1).split(' ')(0).toInt
            val negatedThrough = extractedValue.split('-')(1).split('(')(1).split(' ')(1).split(' ')(0).trim
            val negatedThroughId = extractedValue.split('-')(1).split('(')(1).split('{')(1).split('}')(0)

            partialNegation = new PartialNegation(negationType, negationId, negationAmount, negatedThrough, negatedThroughId)
          }


          new RegularValue(baseValue, containsCrit, excessValue, valueType, valueTypeId, partialNegation)
        }

      } else {
        new NoValue
      }
    }

  }


  def threatFromLine(logLine: String): ThreatValue = {
    // we need to handle values differently if the result is an AreaEntered
    val resultType = logLine.split('[')(5).split('{')(0).trim
    if (resultType == "AreaEntered") {
      new ThreatValue(0)
    } else {
      if (logLine.contains('<')) {
        new ThreatValue(logLine.split('<')(1).split('>')(0).toInt)
      }
      else {
        new ThreatValue(0)
      }
    }
  }

}



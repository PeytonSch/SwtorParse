package parser

import com.typesafe.config.ConfigFactory
import logger.{LogLevel, Logger}
import parsing.Actions.DefaultAction
import parsing.Actors.Actor
import parsing.Result.Result
import parsing.Threat.ThreatValue
import parsing.Values.Value
import parsing.subTypes.LogTimestamp
import parsing.FactoryClasses
import patterns.Actions.Action
import patterns.LogInformation

import scala.io.Source

/**
 * This parser.Parser Class is intended to handle extracting data from logs. It is a WIP
 */
class Parser {

  val config = ConfigFactory.load()

  val factory = new FactoryClasses

  var lastReadLine = 0

  def getNewLines(path: String): IndexedSeq[LogInformation] = {
    getLinesFromFile(path)
  }
  def getNewLines(): IndexedSeq[LogInformation] = {
    if(config.getString("RunMode.mode") == ("Staging")) {
      // this one is chunky chunky
//      getLinesFromFile("G:/Users/Peyton/Documents/Star Wars - The Old Republic/CombatLogs/combat_2022-02-20_20_26_07_955458.txt")
      getLinesFromFile("G:/Users/Peyton/Documents/Star Wars - The Old Republic/CombatLogs/combat_2022-02-20_18_37_06_264936.txt")
    }
    else {
//      getLinesFromFile("SampleLogs/combat_group_2021-12-30_21_56_04_432352.txt")
//      getLinesFromFile("SampleLogs/combat_solo_2021-12-30_20_58_33_468342.txt")
      getLinesFromFile("SampleLogs/combat_2022-02-20_20_26_07_955458.txt")

    }
  }

  def getLinesFromFile(path: String): IndexedSeq[LogInformation] = {

    // TODO: Can we grab only remaining lines somehow?
    // not sure why I need to do this and if I can remove it?
    val lines = if(config.getString("RunMode.mode") == ("Staging")) {
      Source.fromFile(path,"ISO-8859-1").getLines.toList
    } else {
      Source.fromFile(path).getLines.toList
    }
    Logger.trace(s"Found ${lines.size} lines to parse in file ${path}")
    // if there are no new read lines we dont need to do anything
    if (lastReadLine == lines.length-1){
      Logger.print("No new read lines",LogLevel.Trace)
      IndexedSeq()
    } else {
      val collected : IndexedSeq[LogInformation] = for (currentIndex <- Range(lastReadLine,lines.length)) yield {
        //println(s"Extracting ling ${currentIndex} from log")
        val line = lines(currentIndex)
        /**
         * Extract log information
         */
        val time : LogTimestamp = factory.timestampFromLine(line)
        val performer : Actor = factory.performingActorFromLogLineString(line)
        val target : Actor = factory.targetActorFromLogLineString(line)
        val action : Action = factory.actionFromLine(line)
        val result : Result = factory.resultFromLine(line)
        // See if this line has a value associated with it
        val resultValue : Value = factory.valueFromLine(line)
        val threatValue : ThreatValue = factory.threatFromLine(line)

        lastReadLine = currentIndex

        new LogInformation(time,performer,target,action,result,resultValue,threatValue)




      }

      //println(s"Read ${collected.size} log lines this tick")

      collected
    }

  }

  //def extractBase(line: String): BaseInformation = factory.baseInformationFromLine(line)


//  def parseLineInformation(line: String): ValueType = {
//
//    // TODO: Remove this
//    val temp:Temp = new Temp()
//
//    // split up the line into its parts
//    val splitLine = line.split("[\\[\\]@\\{\\}\\(\\)<>]")
//
//    // filter out the parts that are just newlines, spaces, or empty
//    val splitLineFinal: Array[String] = splitLine.filter(_ != "\n").filter(_ != " ").filter(_ != "")
//
//    // TODO: Remove spaces on the ends of some elements... maybe do this in the value class?
//
//    // Fix regex around type
//    // simple combat lines occur at different index
//    // [20:33:41.206] [@Ilumsharpshoota] [@Ilumsharpshoota] [] [Event {836045448945472}: LeaveCover {836045448945486}] ()
//    try{
//      splitLineFinal(7) = splitLineFinal(7).replace(": ","")
//    } catch {
//      case e =>
//    }
//
//    //println(line)
//    // \[\d\d:\d\d:\d\d.\d{3}\] \[@?[^\]]*] \[@?[^\]]*] [^\{]*\{\d*}] \[[^\{]*\{\d*}: [^\{]* \{\d*}] \(\)
//    val simpleNoValuePattern = "(\\[\\d\\d:\\d\\d:\\d\\d.\\d{3}\\] \\[@?[^\\]]*] \\[@?[^\\]]*] [^\\{]*\\{\\d*}] \\[[^\\{]*\\{\\d*}: [^\\{]* \\{\\d*}] \\(\\))".r
//    // \[\d\d:\d\d:\d\d.\d{3}\] \[@?[^\]]*] \[@?[^\]]*] [^\{]*\{\d*}] \[[^\{]*\{\d*}: [^\{]* \{\d*}] \(\d+\)
//    val simpleRegularValue = "(\\[\\d\\d:\\d\\d:\\d\\d.\\d{3}\\] \\[@?[^\\]]*] \\[@?[^\\]]*] [^\\{]*\\{\\d*}] \\[[^\\{]*\\{\\d*}: [^\\{]* \\{\\d*}] \\(\\d+ ?-?\\))".r
//    val simpleCriticalValue = "(\\[\\d\\d:\\d\\d:\\d\\d.\\d{3}\\] \\[@?[^\\]]*] \\[@?[^\\]]*] [^\\{]*\\{\\d*}] \\[[^\\{]*\\{\\d*}: [^\\{]* \\{\\d*}] \\(\\d+\\*\\))".r
//    val regularValueWithThreat = "(\\[\\d\\d:\\d\\d:\\d\\d.\\d{3}\\] \\[@?[^\\]]*] \\[@?[^\\]]*] [^\\{]*\\{\\d*}] \\[[^\\{]*\\{\\d*}: [^\\{]* \\{\\d*}] \\(\\d+ ?-?\\) <-?\\d*>)".r
//    val criticalValueWithThreat = "(\\[\\d\\d:\\d\\d:\\d\\d.\\d{3}\\] \\[@?[^\\]]*] \\[@?[^\\]]*] [^\\{]*\\{\\d*}] \\[[^\\{]*\\{\\d*}: [^\\{]* \\{\\d*}] \\(\\d+\\*\\) <-?\\d*>)".r
//    val noValueModifyThreat = "(\\[\\d\\d:\\d\\d:\\d\\d.\\d{3}\\] \\[@?[^\\]]*] \\[@?[^\\]]*] [^\\{]*\\{\\d*}] \\[[^\\{]*\\{\\d*}: [^\\{]* \\{\\d*}] \\(\\) <-?\\d*>)".r
//    // \[\d\d:\d\d:\d\d.\d{3}\] \[@?[^\]]*] \[@?[^\]]*] [^\{]*\{\d*}] \[[^\{]*\{\d*}: [^\{]* \{\d*}] \(\d* \w* \{\d*\}\) <\d*>
//    val regularDamageValue = "(\\[\\d\\d:\\d\\d:\\d\\d.\\d{3}\\] \\[@?[^\\]]*] \\[@?[^\\]]*] [^\\{]*\\{\\d*}] \\[[^\\{]*\\{\\d*}: [^\\{]* \\{\\d*}] \\(\\d* -?\\w* \\{\\d*\\}\\) <-?\\d*>)".r
//    // \[\d\d:\d\d:\d\d.\d{3}\] \[@?[^\]]*] \[@?[^\]]*] [^\{]*\{\d*}] \[[^\{]*\{\d*}: [^\{]* \{\d*}] \(\d*\* \w* \{\d*\}\) <\d*>
//    val criticalDamageValue = "(\\[\\d\\d:\\d\\d:\\d\\d.\\d{3}\\] \\[@?[^\\]]*] \\[@?[^\\]]*] [^\\{]*\\{\\d*}] \\[[^\\{]*\\{\\d*}: [^\\{]* \\{\\d*}] \\(\\d*\\* -?\\w* \\{\\d*\\}\\) <-?\\d*>)".r
//    val regularDamageNoThreat = "(\\[\\d\\d:\\d\\d:\\d\\d.\\d{3}\\] \\[@?[^\\]]*] \\[@?[^\\]]*] [^\\{]*\\{\\d*}] \\[[^\\{]*\\{\\d*}: [^\\{]* \\{\\d*}] \\(\\d* \\w* \\{\\d* ?-?\\}\\))".r
//    // \[\d\d:\d\d:\d\d.\d{3}\] \[@?[^\]]*] \[@?[^\]]*] [^\{]*\{\d*}] \[[^\{]*\{\d*}: [^\{]* \{\d*}] \(\d*\* \w* \{\d*\}\)
//    val criticalDamageNoThreat = "(\\[\\d\\d:\\d\\d:\\d\\d.\\d{3}\\] \\[@?[^\\]]*] \\[@?[^\\]]*] [^\\{]*\\{\\d*}] \\[[^\\{]*\\{\\d*}: [^\\{]* \\{\\d*}] \\(\\d*\\* \\w* \\{\\d*\\}\\))".r
//    // \[\d\d:\d\d:\d\d.\d{3}\] \[@?[^\]]*] \[@?[^\]]*] [^\{]*\[\] \[[^\{]*\{\d*}: [^\{]* \{\d*}] \(\)
//    val simpleEvent = "(\\[\\d\\d:\\d\\d:\\d\\d.\\d{3}\\] \\[@?[^\\]]*] \\[@?[^\\]]*] [^\\{]*\\[\\] \\[[^\\{]*\\{\\d*}: [^\\{]* \\{\\d*}] \\(\\))".r
//    val simpleEventWithValue = "(\\[\\d\\d:\\d\\d:\\d\\d.\\d{3}\\] \\[@[^\\]]*] \\[@[^\\]]*] \\[\\] \\[[^\\{]*\\{\\d*}: [^\\{]* \\{\\d*}] \\(\\d*\\))".r
//    val criticalDamageWithAbsorbWithThreat = "(\\[\\d\\d:\\d\\d:\\d\\d.\\d{3}\\] \\[@?[^\\]]*] \\[@?[^\\]]*] [^\\{]*\\{\\d*}] \\[[^\\{]*\\{\\d*}: [^\\{]* \\{\\d*}] \\(\\d*\\* \\w* \\{\\d*\\} \\(\\d* \\w* \\{\\d*\\}\\)\\) <-?\\d*>)".r
//    // \[\d\d:\d\d:\d\d.\d{3}\] \[@?[^\]]*] \[@?[^\]]*] [^\{]*\{\d*}] \[[^\{]*\{\d*}: [^\{]* \{\d*}] \(\d* \w* \{\d*\} \(\d* \w* \{\d*\}\)\) <\d*>
//    val regularDamageWithAbsorbWithThreat = "(\\[\\d\\d:\\d\\d:\\d\\d.\\d{3}\\] \\[@?[^\\]]*] \\[@?[^\\]]*] [^\\{]*\\{\\d*}] \\[[^\\{]*\\{\\d*}: [^\\{]* \\{\\d*}] \\(\\d* \\w* \\{\\d*\\} \\(\\d* \\w* \\{\\d*\\}\\)\\) <-?\\d*>)".r
//    val criticalDamageWithAbsorbNoThreat = "(\\[\\d\\d:\\d\\d:\\d\\d.\\d{3}\\] \\[@?[^\\]]*] \\[@?[^\\]]*] [^\\{]*\\{\\d*}] \\[[^\\{]*\\{\\d*}: [^\\{]* \\{\\d*}] \\(\\d*\\* \\w* \\{\\d*\\} \\(\\d* \\w* \\{\\d*\\}\\)\\))".r
//    // \[\d\d:\d\d:\d\d.\d{3}\] \[@?[^\]]*] \[@?[^\]]*] [^\{]*\{\d*}] \[[^\{]*\{\d*}: [^\{]* \{\d*}] \(\d* \w* \{\d*\} \(\d* \w* \{\d*\}\)\)
//    val regularDamageWithAbsorbNoThreat = "(\\[\\d\\d:\\d\\d:\\d\\d.\\d{3}\\] \\[@?[^\\]]*] \\[@?[^\\]]*] [^\\{]*\\{\\d*}] \\[[^\\{]*\\{\\d*}: [^\\{]* \\{\\d*}] \\(\\d* \\w* \\{\\d*\\} \\(\\d* \\w* \\{\\d*\\}\\)\\))".r
//    // \[\d\d:\d\d:\d\d.\d{3}\] \[@?[^\]]*] \[@?[^\]]*] [^\{]*\{\d*}] \[[^\{]*\{\d*}: [^\{]* \{\d*}] \(\d* \w* \{\d*\} -? ?\(\d* \w* \{\d*\}\)\)
//    val kineticDamageMinusAbsorb = "(\\[\\d\\d:\\d\\d:\\d\\d.\\d{3}\\] \\[@?[^\\]]*] \\[@?[^\\]]*] [^\\{]*\\{\\d*}] \\[[^\\{]*\\{\\d*}: [^\\{]* \\{\\d*}] \\(\\d* \\w* \\{\\d*\\} -? ?\\(\\d* \\w* \\{\\d*\\}\\)\\))".r
//    val criticalKineticDamageMinusAbsorb = "(\\[\\d\\d:\\d\\d:\\d\\d.\\d{3}\\] \\[@?[^\\]]*] \\[@?[^\\]]*] [^\\{]*\\{\\d*}] \\[[^\\{]*\\{\\d*}: [^\\{]* \\{\\d*}] \\(\\d*\\* \\w* \\{\\d*\\} -? ?\\(\\d* \\w* \\{\\d*\\}\\)\\))".r
//    val fallDamageLike = "(\\[\\d\\d:\\d\\d:\\d\\d.\\d{3}\\] \\[@?[^\\]]*] \\[\\] \\[\\] \\[[^\\{]*\\{\\d*}: [^\\{]* \\{\\d*}] \\(\\d+\\))".r
//
//    // careful with this one it matches more than just this
//    val enterCombat = "(\\[\\d\\d:\\d\\d:\\d\\d.\\d{3}\\] \\[@?[^\\]]*] \\[@?[^\\]]*] [^\\{]*\\[\\] \\[[^\\{]*\\{\\d*}: [^\\{]* \\{\\d*}] \\(.*\\))".r
//
//    line match {
//      /** This pattern matches things like class buffs, sprint, safe login
//       * [18:24:20.012] [@Ilumsharpshoota] [@Ilumsharpshoota] [Coordination {881945764429824}] [ApplyEffect {836045448945477}: Hunter's Boon {881945764430104}] ()
//       * [18:24:20.012] [@Ilumsharpshoota] [@Ilumsharpshoota] [Advanced Kyrprax Proficient Stim {4256312590336000}] [ApplyEffect {836045448945477}: Advanced Kyrprax Proficient Stim {4256312590336000}] (
//       */
//      case simpleNoValuePattern(c) => temp
//
//        /** These seem to possibly only be heals
//         * [@Chatoz] [@Ilumsharpshoota] [Kolto Probe {814832605462528}] [ApplyEffect {836045448945477}: Heal {836045448945500}] (2624)
//         * */
//      case simpleRegularValue(c) => {
//        //splitLineFinal.foreach(println)
//        val result = new SimpleRegularValue(splitLineFinal)
//        result
//      }
//      case simpleCriticalValue(c) =>temp
//      case regularValueWithThreat(c) =>temp
//      case criticalValueWithThreat(c) =>temp
//      case noValueModifyThreat(c) =>temp
//      case regularDamageValue(c) =>temp
//      case criticalDamageValue(c) =>temp
//      case regularDamageNoThreat(c) =>temp
//      case criticalDamageNoThreat(c) =>temp
//      case simpleEvent(c) =>temp
//      case simpleEventWithValue(c) =>temp
//      case criticalDamageWithAbsorbWithThreat(c) =>temp
//      case regularDamageWithAbsorbWithThreat(c) =>temp
//      case fallDamageLike(c) =>temp
//      case enterCombat(c) =>temp
//      case criticalDamageWithAbsorbNoThreat(c) =>temp
//      case regularDamageWithAbsorbNoThreat(c) =>temp
//      case kineticDamageMinusAbsorb(c) =>temp
//      case criticalKineticDamageMinusAbsorb(c) =>temp
//      case _ => {
//        println("Line does not match any patterns: " + line)
//        temp
//      }
//    }
//
//
//    // if the effect is a heal, contains value and no threat
//
//    // if the effect is damage, it contains a value, type, id, and threat
//
//    // if the effect is ModifyThreat it contains threat but no value
//
//
//  }

}

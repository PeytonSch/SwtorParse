package parser

import patterns.Actions.Action
import patterns.Actors.Actor
import patterns.subTypes.LogTimestamp
import patterns.{BaseInformation, FactoryClasses, SimpleRegularValue, Temp, ValueType}

import scala.io.Source

/**
 * This parser.Parser Class is intended to handle extracting data from logs. It is a WIP
 */
class Parser {

  val factory = new FactoryClasses

  var lastReadLine = 0

  def getNextLine(): String = {

    val lines = Source.fromFile("SampleLogs/combat_group_2021-12-30_21_56_04_432352.txt").getLines.toList

    val line = lines(lastReadLine)
    //println(line)
    lastReadLine = lastReadLine + 1
    //parseLineInformation(line)

    /**
     * Extract the timestamp, Actor name / Id / Position / Health
     */
    val time : LogTimestamp = factory.timestampFromLine(line)
    val performer : Actor = factory.performingActorFromLogLineString(line)
    val target : Actor = factory.targetActorFromLogLineString(line)
    val action : Action = factory.actionFromLine(line)

    ""

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

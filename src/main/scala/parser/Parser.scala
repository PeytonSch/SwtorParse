package parser

import UI.UICodeConfig
import UI.objects.ProgressBar.{progressBar, progressBarRect, progressBarText}
import Utils.Timer
import com.typesafe.config.ConfigFactory
import logger.{LogLevel, Logger}
import parsing.Actions.DefaultAction
import parsing.Actors.Actor
import parsing.Result.{Event, Result}
import parsing.Threat.ThreatValue
import parsing.Values.{NoValue, Value}
import parsing.subTypes.LogTimestamp
import parsing.FactoryClasses
import patterns.Actions.{Action, NoAction}
import patterns.LogInformation
import scalafx.application.Platform
import scalafx.scene.layout.VBox
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.Text

import scala.collection.mutable
import scala.io.Source

/**
 * This parser.Parser Class is intended to handle extracting data from logs. It is a WIP
 */
object Parser {

  val config = ConfigFactory.load()

  val factory = new FactoryClasses

  var lastReadLine = 0

  val combatInstanceLineIndexes: mutable.Map[Int,Int] = mutable.Map()
  var lastCombatEntered = 0
  var loginLine = 0


  //Timer estimations for progress bar
  var percent: Double = 0

  /**
   * Reset the parser, called when you load a new combat log
   */
  def resetParser(): Unit = {
    lastReadLine = 0
    loginLine = 0
    lastCombatEntered = 0
    combatInstanceLineIndexes.clear()
  }

  /**
   * This get new lines overrides the file path and uses unoptimized loading. It is currently used for test
   * and when we open a new file. This will need to be changed to use optimized loading when opening a new file
   */
  def getNewLines(path: String): IndexedSeq[LogInformation] = {
    getLinesFromFile(path)
  }

  /**
   * This getNewLines is for continually looking at a specific file
   */
  def getNewLines(): IndexedSeq[LogInformation] = {
    if(config.getString("RunMode.mode") == ("Staging")) {
      // this one is chunky chunky
//      getLinesFromFile(s"${UICodeConfig.logPath}combat_2022-02-20_20_26_07_955458.txt")
//      getLinesFromFile(s"${UICodeConfig.logPath}combat_2022-02-20_18_37_06_264936.txt")
      // This is a good group one, running around with Isaac
//      getLinesFromFile(s"${UICodeConfig.logPath}combat_2022-02-21_15_25_12_263261.txt")
      // this one is MASSIVE and from a raid
//      getLinesFromFile(s"${UICodeConfig.logPath}combat_2022-02-21_17_21_45_757025.txt")

      // live running, can delete
      Logger.trace(s"Getting lines from file: ${UICodeConfig.logPath}${UICodeConfig.logFile}")
      getLinesFromFile(s"${UICodeConfig.logPath}${UICodeConfig.logFile}")


    }
    else {
//      getLinesFromFile("SampleLogs/combat_group_2021-12-30_21_56_04_432352.txt")
//      getLinesFromFile("SampleLogs/combat_solo_2021-12-30_20_58_33_468342.txt")
       getLinesFromFile("SampleLogs/combat_2022-02-20_20_26_07_955458.txt")

    }
  }

  /**
   * This new lines uses the optimized loading for when we start the program, as well as ideally it gets implemented for switching files
   * @return
   */
  def getNewLinesInit(): IndexedSeq[LogInformation] = {
    if(config.getString("RunMode.mode") == ("Staging")) {
      // this one is chunky chunky
      //      getLinesFromFile(s"${UICodeConfig.logPath}combat_2022-02-20_20_26_07_955458.txt")
      //      getLinesFromFile(s"${UICodeConfig.logPath}combat_2022-02-20_18_37_06_264936.txt")
      // This is a good group one, running around with Isaac
      //      getLinesFromFile(s"${UICodeConfig.logPath}combat_2022-02-21_15_25_12_263261.txt")
      // this one is MASSIVE and from a raid
      //      getLinesFromFile(s"${UICodeConfig.logPath}combat_2022-02-21_17_21_45_757025.txt")

      // live running, can delete
      getLinesFromFileOptimizedInitialization(s"${UICodeConfig.logPath}${UICodeConfig.logFile}")


    }
    else {
      //      getLinesFromFile("SampleLogs/combat_group_2021-12-30_21_56_04_432352.txt")
      //      getLinesFromFile("SampleLogs/combat_solo_2021-12-30_20_58_33_468342.txt")
      getLinesFromFileOptimizedInitialization("SampleLogs/combat_2022-02-20_20_26_07_955458.txt")

    }
  }

  def parseRemaining(): IndexedSeq[LogInformation] = {
    parseRemaining(s"${UICodeConfig.logPath}${UICodeConfig.logFile}")
  }

  def parseRemaining(path: String): IndexedSeq[LogInformation] = {
    val lines = if (config.getString("RunMode.mode") == ("Staging")) {
      Source.fromFile(path, "ISO-8859-1").getLines.toIndexedSeq
    } else {
      Source.fromFile(path).getLines.toIndexedSeq
    }

    var instances: mutable.IndexedSeq[LogInformation] = mutable.IndexedSeq()
    // get all the sections of the combat log with combat that we havnt parsed yet
     for (key <- combatInstanceLineIndexes.keys.filter(_ != lastCombatEntered)) {
       instances = instances ++ parseLineRange(key,combatInstanceLineIndexes.get(key).get,lines)

     }

    instances.toIndexedSeq

  }

  def getLinesFromFile(path: String): Vector[LogInformation] = {
    // TODO: Can we grab only remaining lines somehow?
    // not sure why I need to do this and if I can remove it?
    val lines = if (config.getString("RunMode.mode") == ("Staging")) {
      Source.fromFile(path, "ISO-8859-1").getLines.toIndexedSeq
    } else {
      Source.fromFile(path).getLines.toIndexedSeq
    }
    Logger.trace(s"Found ${lines.size} lines to parse in file ${path}")


    //The timer starts right away, so we can estimate the progress bar based on the number of lines in the file
    // update the progress bar
//    if (percent < 100) percent = percent + (lines.length.toDouble / 1000)
////    Logger.highlight(s"Percent: ${(percent * 100).toInt}%")
//    val percentWindowLength = (percent * config.getInt("UI.General.prefWidth")).toInt
//    progressBarText.setText(s"Progress: ${(percent * 100).toInt}%")
//    progressBarRect.setWidth(percentWindowLength)




    // if there are no new read lines we dont need to do anything
    if (lastReadLine == lines.length - 1) {
      Logger.trace("No new read lines")
      Vector()
    } else {
      // we read to the 2nd to last line so that we avoid errors where we read a line that hasnt been completely written yet
      // TODO: Maybe add a check to see if the last line ends with newline? And if so read it entirely?
      val collected: Vector[LogInformation] = parseLineRange(lastReadLine,lines.length-2,lines)
//      val collected: IndexedSeq[LogInformation] = for (currentIndex <- Range(lastReadLine, lines.length - 1)) yield {
//        //println(s"Extracting ling ${currentIndex} from log")
//
//
//        val line = lines(currentIndex)
//        try {
//          /**
//           * Extract log information
//           */
//          val time: LogTimestamp = factory.timestampFromLine(line)
//          val performer: Actor = factory.performingActorFromLogLineString(line)
//          val target: Actor = factory.targetActorFromLogLineString(line)
//          val action: Action = factory.actionFromLine(line)
//          val result: Result = factory.resultFromLine(line)
//          // See if this line has a value associated with it
//          val resultValue: Value = factory.valueFromLine(line)
//          val threatValue: ThreatValue = factory.threatFromLine(line)
//
//          lastReadLine = currentIndex
//
//          new LogInformation(time, performer, target, action, result, resultValue, threatValue)
//        }
//          // TODO: This often seems to happen where we read a partial line as the game is still writing the file, how to get only entire lines?
//        catch {
//          case e: Throwable => {
//            Logger.error(s"Failed to Parse Line: ${line} \n Caught e: ${e}")
//
//            val time: LogTimestamp = factory.timestampFromLine(line)
//            val performer: Actor = factory.performingActorFromLogLineString(line)
//            val target: Actor = factory.targetActorFromLogLineString(line)
//            new LogInformation(time, performer, target, new NoAction, new Event("", "", "", ""), new NoValue, new ThreatValue(0))
//          }
//        }
//      }

      Logger.trace(s"Read ${collected.size-1} log lines this tick")

      collected
    }
  }


  def getLinesFromFileOptimizedInitialization(path: String): IndexedSeq[LogInformation] = {

    // TODO: Can we grab only remaining lines somehow?
    // not sure why I need to do this and if I can remove it? Might just have been from the testing logs
    val lines = if(config.getString("RunMode.mode") == ("Staging")) {
      Source.fromFile(path,"ISO-8859-1").getLines.toIndexedSeq
    } else {
      Source.fromFile(path).getLines.toIndexedSeq
    }
    Logger.trace(s"Found ${lines.size} lines to parse in file ${path}")

    /**
     * Create a map of where combat instances occur in the log, as well as get the latest combat to parse.
     * We also need to parse the login line to set the current player
     */
    if (lastReadLine == lines.length-1){
      Logger.trace("No new read lines")
      IndexedSeq()
    } else {
      for (currentIndex <- Range(lastReadLine,lines.length-1)) yield {
        val checkLine = lines(currentIndex)
        if (checkLine.contains("EnterCombat")){
//          Logger.highlight(s"Enter Combat at ${currentIndex}")
          lastCombatEntered = currentIndex
        } else if (checkLine.contains("ExitCombat")) {
//          Logger.highlight(s"Exit Combat at ${currentIndex}")
          combatInstanceLineIndexes(lastCombatEntered) = currentIndex
        } else if (loginLine == 0 && checkLine.contains("Login")) {
          loginLine = currentIndex
        }
      }
    }


    /**
     * Parse the most recent combat instance only right away
     * This will give the UI log information so we can start using it
     */
      // parse the login line first, it sets information in the controller
      val throwAway:Vector[LogInformation]  = parseLineRange(loginLine,loginLine+1,lines)
      val collected : Vector[LogInformation] = throwAway ++ parseLineRange(lastCombatEntered,combatInstanceLineIndexes.get(lastCombatEntered).get,lines)

    collected
  }

    def parseLineRange(start: Int, stop: Int, lines:IndexedSeq[String]): Vector[LogInformation] = {
      val range = Range(start,stop).toVector
      Timer.time(s"Parser Parse Line Range with ${stop-start} lines", {
      if (start == 0) {
        Logger.highlight(s"Loading Log File With ${stop+1} lines. Please note, at the moment, large files take awhile to initialize")
      }

      val collected : Vector[LogInformation] = for (currentIndex <- range) yield {

        if (currentIndex % 1000 ==0) {
          Logger.highlight(s"Progress: ${((currentIndex.toDouble/stop)*100).toInt}")
        }

        //println(s"Extracting ling ${currentIndex} from log")

        // update the progress bar
//        if(currentIndex % 1000 == 0 || currentIndex == lines.length - 1) {
//          val percent:Double = (currentIndex.toDouble / lines.length)
//          val percentWindowLength = (percent * config.getInt("UI.General.prefWidth")).toInt
//          Logger.highlight(s"Percent: ${(percent*100).toInt}%")
//          Platform.runLater(progressBarText.setText(s"Progress: ${(percent*100).toInt}% (${currentIndex}/${lines.length-1})"))
//          Platform.runLater(progressBarRect.setWidth(percentWindowLength))
//        }

        val line = lines(currentIndex)
        try {
          /**
           * Extract log information
           */
          val time: LogTimestamp = factory.timestampFromLine(line)
          val performer: Actor = factory.performingActorFromLogLineString(line)
          val target: Actor = factory.targetActorFromLogLineString(line)
          val action: Action = factory.actionFromLine(line)
          val result: Result = factory.resultFromLine(line)
          // See if this line has a value associated with it
          val resultValue: Value = factory.valueFromLine(line)
          val threatValue: ThreatValue = factory.threatFromLine(line)

//          val time: LogTimestamp = Timer.time("Parser Extract Timestamp",{factory.timestampFromLine(line)})
//          val performer: Actor = Timer.time("Parser Extract Performer",{factory.performingActorFromLogLineString(line)})
//          val target: Actor = Timer.time("Parser Extract Target",{factory.targetActorFromLogLineString(line)})
//          val action: Action = Timer.time("Parser Extract Action",{factory.actionFromLine(line)})
//          val result: Result = Timer.time("Parser Extract Result",{factory.resultFromLine(line)})
//          // See if this line has a value associated with it
//          val resultValue: Value = Timer.time("Parser Extract Result Value",{factory.valueFromLine(line)})
//          val threatValue: ThreatValue = Timer.time("Parser Extract Threat Value",{factory.threatFromLine(line)})

          lastReadLine = currentIndex

          new LogInformation(time, performer, target, action, result, resultValue, threatValue)
        }
          // TODO: This often seems to happen where we read a partial line as the game is still writing the file, how to get only entire lines?
        catch {
          case e: Throwable => {
            Logger.error(s"Failed to Parse Line: ${line} \n Caught e: ${e}")

            val time: LogTimestamp = factory.timestampFromLine(line)
            val performer: Actor = factory.performingActorFromLogLineString(line)
            val target: Actor = factory.targetActorFromLogLineString(line)
            new LogInformation(time, performer, target, new NoAction, new Event("","","",""), new NoValue, new ThreatValue(0))
          }
        }
      }
      collected
      }) // end timer
    }


}

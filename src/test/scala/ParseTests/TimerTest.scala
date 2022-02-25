package ParseTests

import Utils.Timer
import logger.Logger
import parser.Parser
import parser.Parser.{factory, lastReadLine}
import parsing.Actors.{Actor, NoneActor}
import parsing.Result.{Event, Result}
import parsing.Threat.ThreatValue
import parsing.Values.{NoValue, Value}
import parsing.subTypes.LogTimestamp
import patterns.Actions.{Action, NoAction}
import patterns.LogInformation

import scala.collection.immutable.Queue
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object TimerTest extends App {

  val playerLogLine = "[22:04:31.735] [@Heavy Sloth#689203382607232|(1.09,-123.36,-11.44,-2.26)|(2909/2909)] [Acolyte Henchman {379421705895936}:26518005413256|(0.32,-122.48,-11.44,150.00)|(0/90)] [Assault {898601647603712}] [ApplyEffect {836045448945477}: Damage {836045448945501}] (51 ~0 energy {836045448940874} -)"

  val range = 100000

  val logInfo = Parser.parseLineRange(0,1,IndexedSeq(playerLogLine))(0)

  println(logInfo)

  val lines: List[LogInformation] = for (i <- Range(0,range).toList) yield logInfo

  val linesVec : Vector[LogInformation] = lines.toVector

  val linesQ : Queue[LogInformation] = Timer.time("Converting lines to queue", {lines.to(collection.immutable.Queue)})

  val linesString: List[String] =  for (i <- Range(0,range).toList) yield playerLogLine

  val linesIndexedSeq: IndexedSeq[String] =  for (i <- Range(0,range).toIndexedSeq) yield playerLogLine


  // Indexed seq resulting from for yield
  val one: IndexedSeq[LogInformation] = Timer.time("Indexed seq from for yield", {
    for (i <- Range(0, range)) yield {
      logInfo
    }
  })

  // list resulting from for yield
  val six: List[LogInformation] = Timer.time("list from for yield with toList conversion", {
    for (i <- Range(0, range).toList) yield {
      logInfo
    }
  })

//  // list from for appending
//  var seven: ListBuffer[Int] = ListBuffer()
//  Timer.time("List Buffer from for appending with range", {
//    for (i <- Range(0,range)) {
//      seven += i
//    }
//  })

  // list from for appending from lines
  var eight: ListBuffer[LogInformation] = ListBuffer()
  Timer.time("List Buffer from for appending with lines", {
    for (i <- lines) {
      eight += i
    }
  })

  // vector from for yield
  val nine: Vector[LogInformation] = Timer.time("vector from for yield from linesVec", {
    for (i <- linesVec) yield {
      logInfo
    }
  })

  // q from for yield
  val ten: Queue[LogInformation] = Timer.time("q from for yield from linesQ", {
    for (i <- linesQ) yield {
      logInfo
    }
  })

//  // Indexed seq resulting from range
//  val two: IndexedSeq[Int] = Timer.time("Indexed seq from range", {Range(0,range) })

  // seq resulting from for yield
  val three: Seq[LogInformation] = Timer.time("seq from for yield", {
    for (i <- Range(0, range)) yield {
      logInfo
    }
  })


//  // seq resulting from range
//  val four: Seq[Int] = Timer.time("seq from range", {Range(0,range) })

  // Indexed seq from for appending
//  var five: mutable.IndexedSeq[Int] = mutable.IndexedSeq()
//  Timer.time("Indexed seq from for appending", {
//    for (i <- Range(0,range)) {
//      five = five :+ i
//    }
//  })



  val better1: IndexedSeq[LogInformation] = Timer.time("Indexed seq from better setup", {
    parseLineRangeTimerTestIndexedSeq(0,range,linesString)
  })

  val better2: Queue[LogInformation] = Timer.time("Queue from better setup", {
    parseLineRangeTimerTestQueue(0,range,linesIndexedSeq)
  })

  val better3: Vector[LogInformation] = Timer.time("Vector from for better setup", {
    parseLineRangeTimerTestVector(0,range,linesString)
  })



  /**
   * Parsers for more accurate tests
   */




  def parseLineRangeTimerTestIndexedSeq(start: Int, stop: Int, lines:List[String]) = {
    val range = Range(start,stop)

    val linez: IndexedSeq[String] = lines.toIndexedSeq
    Timer.time(s"Parser Parse Line Range with ${stop-start} lines", {
      if (start == 0) {
        Logger.highlight(s"Loading Log File With ${stop+1} lines. Please note, at the moment, large files take awhile to initialize")
      }

      val collected : IndexedSeq[LogInformation] = for (currentIndex <- range) yield {

        val line = linez(currentIndex)
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


  def parseLineRangeTimerTestQueue(start: Int, stop: Int, lines:IndexedSeq[String]): Queue[LogInformation] = {
    val range = Range(start,stop).to(collection.immutable.Queue)
    Timer.time(s"Parser Parse Line Range with ${stop-start} lines", {
      if (start == 0) {
        Logger.highlight(s"Loading Log File With ${stop+1} lines. Please note, at the moment, large files take awhile to initialize")
      }

      val collected : Queue[LogInformation] = for (currentIndex <- range) yield {

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



  def parseLineRangeTimerTestVector(start: Int, stop: Int, lines:List[String]): Vector[LogInformation] = {
    val range = Range(start,stop).toVector

    val linez: IndexedSeq[String] = lines.toIndexedSeq
    Timer.time(s"Parser Parse Line Range with ${stop-start} lines", {
      if (start == 0) {
        Logger.highlight(s"Loading Log File With ${stop+1} lines. Please note, at the moment, large files take awhile to initialize")
      }

      val collected : Vector[LogInformation] = for (currentIndex <- range) yield {

        val line = linez(currentIndex)
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

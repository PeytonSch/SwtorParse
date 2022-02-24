package logger

import com.typesafe.config.ConfigFactory
import logger.LogLevel._

/**
 * Custom build logging class, there are libraries that probably do this way better
 * but I wanted to make one and all I need is something simple so
 */
object Logger {

  val config = ConfigFactory.load()

  val configuredLogLevel = config.getString("Logging.level") match {
      case "Info" => Info
      case "Debug" => Debug
      case "Test" => Test
      case "Trace" => Trace
      case "info" => Info
      case "debug" => Debug
      case "test" => Test
      case "trace" => Trace
      case _ => Info
    }

  /**
   * You can use this print method or you can use logger.debug etc
   * @param statement
   * @param level
   */
//  def print(statement: String, level: LogLevel): Unit = {
//    // if level is info we always print
//    if (level == Info) {
//      println(Console.WHITE + statement)
//    }
//    // always print warn
//    else if (level == Warn) {
//      println(Console.YELLOW + statement)
//    }
//    // always print Error
//    else if (level == Error) {
//      println(Console.RED + statement)
//    }
//    // print debug on trace and debug
//    else if (level == Debug && (configuredLogLevel == Debug || configuredLogLevel == Trace) ) {
//      println(Console.CYAN + statement)
//    }
//    // print trace on trace only
//    else if (level == Trace && configuredLogLevel == Trace) {
//      println(Console.BLUE + statement)
//    }
//    // print test on test only
//    else if (level == Test && configuredLogLevel == Test) {
//      println(Console.BLUE + statement)
//    }
//
//  }

  def error(statement: String): Unit = {
    println(Console.RED + statement)
  }
  def warn(statement: String): Unit = {
    println(Console.YELLOW + statement)

  }
  def info(statement: String): Unit = {
    println(Console.WHITE + statement)
  }
  def debug(statement: String): Unit = {
    if (configuredLogLevel == Debug || configuredLogLevel == Trace || configuredLogLevel == Micro) {
      println(Console.CYAN + statement)
    }
  }
  def test(statement: String): Unit = {
    if (configuredLogLevel == Test) {
      println(Console.BLUE + statement)
    }
  }
  def trace(statement: String): Unit = {
    if (configuredLogLevel == Trace || configuredLogLevel == Micro) {
      println(Console.BLUE + statement)
    }
  }

  def highlight(statement:String):Unit = {
    println(Console.GREEN + statement)
  }

  def micro(statement: String): Unit = {
    if (configuredLogLevel == Micro) {
      println(Console.BLUE + statement)
    }
  }



}

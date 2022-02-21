package logger
/**
 * Hierarchy of log levels.
 * Error/Warn: Always Printed, used for coloring
 * Info: Log lines I in general will always want to print
 * Debug: Next level lower, try to keep these less cluttered but useful to turn on for debugging
 * Test: Logs related to testing
 * Trace: Trace level logs, may be very cluttered.
 */
object LogLevel extends Enumeration {
  type LogLevel = Value
  val Error, Warn, Info, Debug, Test, Trace = Value
}

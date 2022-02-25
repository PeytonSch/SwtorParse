package Utils

import logger.Logger

object Timer {

  def time[R](tag: String, block: => R): R = {
    val t0 = System.nanoTime()
    val result = block    // call-by-name
    val t1 = System.nanoTime()
    Logger.highlight(s"Timer: ${tag}: " + (t1 - t0).toDouble / 1000000000 + "seconds")
    result
  }

}

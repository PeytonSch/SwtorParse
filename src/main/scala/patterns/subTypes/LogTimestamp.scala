package parsing.subTypes

class LogTimestamp (
                   time : String
                   ) {
  override def toString: String = time

  def getSecondTotal(): Double = {
    val parts = time.split(":")
    var total : Double = 0;

    // get difference between hours
    total = total + ((parts(0).toInt * 3600) + (parts(1).toInt * 60) + parts(2).toDouble)
//    println(s"Got a total of ${total} from ${time}")
    total

  }

  def getTime() = time

  def - (t: LogTimestamp): Int = {
    if (t == null) {
      0
    } else {
      val current = this.getSecondTotal()
      val start = t.getSecondTotal()

      // if current < start we probably rolled the days over
      if (current < start) {
        val x = current+86400 - start
//        println(s"GOT VALUE TO ROUND ${x}")
        (current+86400 - start).round.toInt
      }
      else {
        (current - start).round.toInt
      }


    }
  }

}

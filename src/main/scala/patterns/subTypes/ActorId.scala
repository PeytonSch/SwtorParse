package parsing.subTypes

class ActorId( // These IDs are too Long to be Ints or Longs, they need to be strings
               typeId : String,
               instanceId : String
         ){

  override def toString: String = "[ Type ID: " + typeId + " Instance ID: " + instanceId + " ]"

  def getInstanceId() = instanceId

  // This is for comparing IDs and not objects
  def compare(that: Any) : Boolean = {
    try {
      that match {
        case that: ActorId => {
          if (this.instanceId == null || that.getInstanceId() == null) {
            false
          } else {
            this.instanceId == that.getInstanceId()
          }

        }
        case _ => false
      }
    }
    catch {
      case e:NullPointerException => false //do nothing
      case e:Throwable => {
        println(e)
        false
      }
    }
  }

}

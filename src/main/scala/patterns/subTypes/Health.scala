package patterns.subTypes

class Health (
             current : Int,
             max : Int
             ){

  override def toString: String = "[ Current Health: " + current + " Max Health: " + max + " ]"
}

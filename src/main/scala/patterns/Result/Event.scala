package parsing.Result

class Event (
              resultType : String,
              effectId : String,
              name : String,
              nameId : String
            ) extends Result {
   override def toString: String = s"[ Type: ${resultType} Name: ${name} ]"
  def getName = name
}

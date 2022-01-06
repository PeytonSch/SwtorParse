package patterns.Result

class RemoveEffect (
                     resultType : String,
                     effectId : String,
                     name : String,
                     nameId : String
                   ) extends Result {
  override def toString: String = s"[ Type: ${resultType} Name: ${name} ]"
}
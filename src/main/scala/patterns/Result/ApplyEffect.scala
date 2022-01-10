package parsing.Result

class ApplyEffect (
                  resultType : String,
                  effectId : String,
                  name : String,
                  nameId : String

                  ) extends Result {
  override def toString: String = s"[ Type: ${resultType} Name: ${name} ]"

  def getName(): String = name

}

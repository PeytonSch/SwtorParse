package patterns.Result

class ApplyEffect (
                  name : String,
                  effectId : String,
                  resultType : String,
                  resultTypeId : String

                  ) extends Result {
  override def toString: String = s"[ Name: ${name} ResultType: ${resultType} ]"

}

package patterns.Result

import parsing.Result.Result

class GenericResult (
                      resultType : String,
                      name : String,
                    ) extends Result {
  override def toString: String = s"[ Type: ${resultType} Name: ${name} ]"

}

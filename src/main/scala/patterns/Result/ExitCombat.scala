package patterns.Result

import parsing.Result.Result

class ExitCombat(
                  resultType : String,
                  effectId : String,
                  name : String,
                  nameId : String
                  ) extends Result {

  override def toString: String = s"[ Type: ${resultType} Name: ${name} ]"

}

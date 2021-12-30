package patterns

class Temp extends ValueType {

  override def getResult(): String = {
    "None"
  }

  override def getResultType(): String = "None"
  override def trimSpace(s:String): String = ""
}

package patterns

trait ValueType {
  def getResult(): String
  def getResultType(): String
  def trimSpace(s:String): String = {
      if (s(s.size-1) == ' ') {
        s.dropRight(1)
      } else {
        s
      }
    }
}

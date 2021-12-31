package patterns


/**
 * This is the ValueType trait. It is used by the Parser. Patterns that contain values should extend this
 * It will be used to define methods for extracting data and updating the GUI.
 *
 * This is very much a WIP
 */
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

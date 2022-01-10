package parsing.Values

class NoValue extends Value {

  override def getFullValue(): Int = 0

  override def getTotalAmountDiscounted(): Int = 0
  override def getTotalValue(): Int = 0

}

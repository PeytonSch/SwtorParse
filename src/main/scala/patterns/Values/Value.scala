package parsing.Values

trait Value {

  def getFullValue(): Int // this is the full value, no negations
  def getTotalValue(): Int // total value, after subtracting excess and negations
  def getTotalAmountDiscounted(): Int // this is the total amount that gets subtracted from full value

  def getValueType(): String // return the type of damage, like "energy"

}

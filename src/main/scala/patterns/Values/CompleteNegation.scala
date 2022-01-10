package parsing.Values

class CompleteNegation (
                         negationType: String, // Ex: "miss" or "deflect"
                         negationTypeId: String
                       ) extends Value {

  override def getFullValue(): Int = 0
  override def getTotalAmountDiscounted(): Int = 0
  override def getTotalValue(): Int = 0

}

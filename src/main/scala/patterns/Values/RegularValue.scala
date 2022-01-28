package parsing.Values

class RegularValue(
                    baseValue: Int,
                    crit: Boolean,
                    excess: Int,
                    valueType: String,
                    valueTypeId: String,
                    partialNegation: PartialNegation
                  ) extends Value {

  def getBaseValue() : Int = baseValue
  override def getCrit(): Boolean = crit
  def getExcess(): Int = excess
  override def getValueType(): String = valueType
  def getValueTypeId(): String = valueTypeId
  def getPartialNegation(): PartialNegation = partialNegation


  override def getTotalAmountDiscounted(): Int = (excess + partialNegation.getNegatedAmount)
  override def getFullValue(): Int = baseValue
  override def getTotalValue(): Int = baseValue - (excess + partialNegation.getNegatedAmount)

  def packagedNoNegationForTests() : (Int,Boolean,Int,String,String) = {
    (baseValue,crit,excess,valueType,valueTypeId)
  }

}

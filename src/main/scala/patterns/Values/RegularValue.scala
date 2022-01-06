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
  def getCrit(): Boolean = crit
  def getExcess(): Int = excess
  def getValueType(): String = valueType
  def getValueTypeId(): String = valueTypeId
  def getPartialNegation(): PartialNegation = partialNegation

  def packagedNoNegationForTests() : (Int,Boolean,Int,String,String) = {
    (baseValue,crit,excess,valueType,valueTypeId)
  }

}

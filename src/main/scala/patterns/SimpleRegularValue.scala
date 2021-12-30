package patterns

class SimpleRegularValue(data: Array[String]) extends ValueType {

  val timeStamp = data(0)
  val performer = data(1)
  val target = data(2)
  val abilityName = data(3)
  val abilityId = data(4)
  val result = data(5)
  val resultId = data(6)
  val resultType = data(7)
  val resultTypeId = data(8)
  val resultValue = data(9)

  override def getResult(): String = trimSpace(result)

  override def getResultType(): String = trimSpace(resultType)



}

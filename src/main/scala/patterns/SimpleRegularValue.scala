package patterns

/**
 * This is a simple regular value. It corresponds to log lines like this:
 * [@Chatoz] [@Ilumsharpshoota] [Kolto Probe {814832605462528}] [ApplyEffect {836045448945477}: Heal {836045448945500}] (2624)
 *
 * they may only be heals received?
 *
 * @param data - This array of strings contains log elements
 *
 * This should extend the ValueType Trait and override the necessary functions
 */

class SimpleRegularValue(data: Array[String]) extends ValueType {

  /**
   * Log Elements corresponding to their position in the passed in data array
   */
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

  // I was having an issue with spaces, so I created trim space in the ValueType trait.
  override def getResult(): String = trimSpace(result)

  override def getResultType(): String = trimSpace(resultType)



}

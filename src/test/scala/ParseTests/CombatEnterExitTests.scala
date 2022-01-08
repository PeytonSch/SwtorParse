package ParseTests

import org.scalatest.flatspec.AnyFlatSpec
import parsing.FactoryClasses
import patterns.Result.{EnterCombat, ExitCombat}

class CombatEnterExitTests extends AnyFlatSpec{

  val combatEnteredLine = "[22:06:41.678] [@Heavy Sloth#689203382607232|(45.15,-239.37,-12.49,100.68)|(2909/2909)] [] [] [Event {836045448945472}: EnterCombat {836045448945489}]"
  val combatExitedLine = "[22:06:29.565] [@Heavy Sloth#689203382607232|(66.58,-221.82,-11.33,-0.75)|(2909/2909)] [] [] [Event {836045448945472}: ExitCombat {836045448945490}]"

  val factory = new FactoryClasses

  val combatEnteredTest = factory.resultFromLine(combatEnteredLine)
  val combatExitedTest = factory.resultFromLine(combatExitedLine)

  "Combat status" should "return correctly" in {
    assert(combatExitedTest.isInstanceOf[ExitCombat])
    assert(combatEnteredTest.isInstanceOf[EnterCombat])
  }

}

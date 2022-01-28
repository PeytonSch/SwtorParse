package CombatActorInstanceTests

import Combat.CombatActorInstance
import org.scalatest.flatspec.AnyFlatSpec

class CombatActorInstanceHealingTakenTests extends AnyFlatSpec{

//  val controller : Controller = new Controller()
//
//  val parser : Parser = new Parser()
//
//  val parseTestLines = parser.getNewLines("SampleLogs/ForTests/SingleCombat.txt")
//
//  controller.parseLatest(parseTestLines)

  val testCombatActor = new CombatActorInstance()

  val testCombatActorBlank = new CombatActorInstance()

  "New Combat Actor" should "have empty healing taken map" in {
    assert(testCombatActorBlank.gethealingDoneTimeSeries().size == 0)
    assert(testCombatActorBlank.gethealingTakenStats().size == 0)
  }

  "Combat Actors" should "update damage taken maps accordingly" in {
    testCombatActor.updateHealingTaken(10,1,"","Source1")
    testCombatActor.updateHealingTaken(10,1,"","Source2")
    testCombatActor.updateHealingTaken(10,2,"","Source3")

    assert(testCombatActor.getHealingTaken() == 30)

    assert(testCombatActor.gethealingTakenStats().size == 1)
    assert(testCombatActor.gethealingTakenStats()("").size == 3)

    assert(testCombatActor.gethealingTakenStats()("")("Source1") == 10)
    assert(testCombatActor.gethealingTakenStats()("")("Source3") == 10)
    assert(testCombatActor.gethealingTakenStats()("")("Source2") == 10)

    assert(testCombatActor.getHealingTakenPerSecond() == 10.0)

  }


}

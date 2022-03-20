package CombatActorInstanceTests

import Combat.CombatActorInstance
import org.scalatest.flatspec.AnyFlatSpec

class CombatActorInstanceHealingDoneTests extends AnyFlatSpec{


  // TODO: These should be much more robust

  val testCombatActor = new CombatActorInstance()

  val testCombatActorBlank = new CombatActorInstance()

  "New Combat Actor" should "have empty healing map" in {
    assert(testCombatActorBlank.gethealingDoneTimeSeries().size == 0)
  }

  "Combat Actors" should "update healing maps accordingly" in {
    testCombatActor.updateHealingDone(10,1, "", "Source1",true,"target")
    testCombatActor.updateHealingDone(10,1, "", "Source2",true,"target")
    testCombatActor.updateHealingDone(10,2, "","Source3",true,"target")

    assert(testCombatActor.gethealingDoneTimeSeries().size == 2)
    assert(testCombatActor.gethealingDoneTimeSeries()(1) == 20)
    assert(testCombatActor.gethealingDoneTimeSeries()(2) == 10)

    assert(testCombatActor.getHealingDone() == 30)
    assert(testCombatActor.getHealingDonePerSecond() == 10)


    assert(testCombatActor.getHealingDoneStats().size == 1)

    assert(testCombatActor.getHealingDoneStats()("")("Source1") == 10)
    assert(testCombatActor.getHealingDoneStats()("")("Source2") == 10)
    assert(testCombatActor.getHealingDoneStats()("")("Source3") == 10)
  }




}

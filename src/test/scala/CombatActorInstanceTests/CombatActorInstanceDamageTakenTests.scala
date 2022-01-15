package CombatActorInstanceTests

import Combat.CombatActorInstance
import org.scalatest.flatspec.AnyFlatSpec

class CombatActorInstanceDamageTakenTests extends AnyFlatSpec{

//  val controller : Controller = new Controller()
//
//  val parser : Parser = new Parser()
//
//  val parseTestLines = parser.getNewLines("SampleLogs/ForTests/SingleCombat.txt")
//
//  controller.parseLatest(parseTestLines)

  val testCombatActor = new CombatActorInstance()

  val testCombatActorBlank = new CombatActorInstance()

  "New Combat Actor" should "have empty damage taken map" in {
    assert(testCombatActorBlank.getDamageDoneTimeSeries().size == 0)
    assert(testCombatActorBlank.getDamageTakenStats().size == 0)
  }

  "Combat Actors" should "update damage taken maps accordingly" in {
    testCombatActor.updateDamageTaken(10,1,"Internal","Source1")
    testCombatActor.updateDamageTaken(10,1,"Physical","Source2")
    testCombatActor.updateDamageTaken(10,2,"Internal","Source3")

    assert(testCombatActor.getDamageTypeTaken().size == 2)
    assert(testCombatActor.getDamageTypeTaken()("Internal") == 20)
    assert(testCombatActor.getDamageTypeTaken()("Physical") == 10)
    assert(testCombatActor.getDamageTaken() == 30)

    assert(testCombatActor.getDamageTakenStats().size == 2)
    assert(testCombatActor.getDamageTakenStats()("Internal").size == 2)
    assert(testCombatActor.getDamageTakenStats()("Physical").size == 1)

    assert(testCombatActor.getDamageTakenStats()("Internal")("Source1") == 10)
    assert(testCombatActor.getDamageTakenStats()("Internal")("Source3") == 10)
    assert(testCombatActor.getDamageTakenStats()("Physical")("Source2") == 10)

  }




}

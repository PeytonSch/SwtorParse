package CombatActorInstanceTests

import Combat.CombatActorInstance
import Controller.Controller
import org.scalatest.flatspec.AnyFlatSpec
import parser.Parser
import parsing.subTypes.LogTimestamp

class CombatActorInstanceDamageTests extends AnyFlatSpec{

//  val controller : Controller = new Controller()
//
//  val parser : Parser = new Parser()
//
//  val parseTestLines = parser.getNewLines("SampleLogs/ForTests/SingleCombat.txt")
//
//  controller.parseLatest(parseTestLines)

  val testCombatActor = new CombatActorInstance()

  val testCombatActorBlank = new CombatActorInstance()

  "New Combat Actor" should "have empty damage map" in {
    assert(testCombatActorBlank.getDamageDoneTimeSeries().size == 0)
  }

  "Combat Actors" should "update damage maps accordingly" in {
    testCombatActor.updateDamageDone(10,1, "Internal", "Source1")
    testCombatActor.updateDamageDone(10,1, "Internal", "Source2")
    testCombatActor.updateDamageDone(10,2, "Elemental","Source3")

    assert(testCombatActor.getDamageDoneTimeSeries().size == 2)
    assert(testCombatActor.getDamageDoneTimeSeries()(1) == 20)
    assert(testCombatActor.getDamageDoneTimeSeries()(2) == 10)

    assert(testCombatActor.getDamageDone() == 30)

    assert(testCombatActor.getDamageTypeDone().size == 2)
    assert(testCombatActor.getDamageTypeDone()("Internal") == 20)
    assert(testCombatActor.getDamageTypeDone()("Elemental") == 10)

    assert(testCombatActor.getDamageDoneStats().size == 2)
    assert(testCombatActor.getDamageDoneStats()("Internal").size == 2)
    assert(testCombatActor.getDamageDoneStats()("Elemental").size == 1)

    assert(testCombatActor.getDamageDoneStats()("Internal")("Source1") == 10)
    assert(testCombatActor.getDamageDoneStats()("Internal")("Source2") == 10)
    assert(testCombatActor.getDamageDoneStats()("Elemental")("Source3") == 10)
  }




}

package FullCombatLogTests

import Controller.Controller
import org.scalatest.flatspec.AnyFlatSpec
import parser.Parser

class SimpleCombatLogTests extends AnyFlatSpec{

  val controller : Controller = new Controller()

  val parser : Parser = new Parser()

  val parseTestLines = parser.getNewLines("SampleLogs/ForTests/SingleCombat.txt")

  /**
   * These tests will all run after exectution of the loop before,
   * think of them as testing the final state
   */
  "Simple Parser" should "Receive correct number of lines to parse" in {
      assert(parseTestLines.length == 252)
  }

  "Simple Parser" should "have found 1 combat instance" in {
    assert(controller.getAllCombatInstances().size == 1)
  }

  "Simple Parser" should "have found x Actors in this combat" in {
    //controller.getAllCombatInstances()(0).getCombatActors().foreach(println)
    assert(controller.getAllCombatInstances()(0).getCombatActors().size == 6)
  }

  "Controller" should "extract player name correctly" in {
    assert(controller.getPlayerToonName() == "Heavy Sloth")
  }

  "Controller" should "add up total damage done correctly from each Actor" in {
    assert(controller.getAllCombatInstances()(0).getCombatActorByIdString(("[ Type ID: 3266932513964032 Instance ID: 28040000023320 ]")).getDamageDone() == 449)
    assert(controller.getAllCombatInstances()(0).getCombatActorByIdString(("[ Type ID: 3266941103898624 Instance ID: 28040000035026 ]")).getDamageDone() == 2199)
    assert(controller.getAllCombatInstances()(0).getCombatActorByIdString(("[ Type ID: 3266932513964032 Instance ID: 28040000034858 ]")).getDamageDone() == 3198)
    assert(controller.getAllCombatInstances()(0).getCombatActorByIdString(("[ Type ID: 3915326546771968 Instance ID: 28040000040093 ]")).getDamageDone() == 1146)
    assert(controller.getAllCombatInstances()(0).getCombatActorByIdString(("[ Type ID: Heavy Sloth Instance ID: Heavy Sloth ]")).getDamageDone() == 30762)
  }

  "Controller" should "add up damage per second correctly from each Actor" in {
    assert(controller.getAllCombatInstances()(0).getCombatActorByIdString(("[ Type ID: 3266932513964032 Instance ID: 28040000023320 ]")).getDamagePerSecond() == 89)
    assert(controller.getAllCombatInstances()(0).getCombatActorByIdString(("[ Type ID: 3266941103898624 Instance ID: 28040000035026 ]")).getDamagePerSecond() == 183)
    assert(controller.getAllCombatInstances()(0).getCombatActorByIdString(("[ Type ID: 3266932513964032 Instance ID: 28040000034858 ]")).getDamagePerSecond() == 290)
    assert(controller.getAllCombatInstances()(0).getCombatActorByIdString(("[ Type ID: 3915326546771968 Instance ID: 28040000040093 ]")).getDamagePerSecond() == 88)
    assert(controller.getAllCombatInstances()(0).getCombatActorByIdString(("[ Type ID: Heavy Sloth Instance ID: Heavy Sloth ]")).getDamagePerSecond() == 2563)
  }

  "Controller" should "get total damage done correctly for player actor" in {
    assert(controller.getAllCombatInstances()(0).getPlayerInCombatActor().getDamageDone() == 30762)
  }

  "Parser" should "get total damage taken correctly for each actor" in {
    // TODO: These values are not checked
    assert(controller.getAllCombatInstances()(0).getCombatActorByIdString(("[ Type ID: 3266932513964032 Instance ID: 28040000023320 ]")).getDamageTaken() == 9279)
    assert(controller.getAllCombatInstances()(0).getCombatActorByIdString(("[ Type ID: 3266941103898624 Instance ID: 28040000035026 ]")).getDamageTaken() == 5180)
    assert(controller.getAllCombatInstances()(0).getCombatActorByIdString(("[ Type ID: 3266932513964032 Instance ID: 28040000034858 ]")).getDamageTaken() == 7876)
    assert(controller.getAllCombatInstances()(0).getCombatActorByIdString(("[ Type ID: 3915326546771968 Instance ID: 28040000040093 ]")).getDamageTaken() == 207)
    assert(controller.getAllCombatInstances()(0).getCombatActorByIdString(("[ Type ID: Heavy Sloth Instance ID: Heavy Sloth ]")).getDamageTaken() == 5639)
  }

  "Parser" should "get damage taken per second correctly for each actor" in {
    // TODO: These values are not checked
    assert(controller.getAllCombatInstances()(0).getCombatActorByIdString(("[ Type ID: 3266932513964032 Instance ID: 28040000023320 ]")).getDamageTakenPerSecond() == 1325.0)
    assert(controller.getAllCombatInstances()(0).getCombatActorByIdString(("[ Type ID: 3266941103898624 Instance ID: 28040000035026 ]")).getDamageTakenPerSecond() == 863)
    assert(controller.getAllCombatInstances()(0).getCombatActorByIdString(("[ Type ID: 3266932513964032 Instance ID: 28040000034858 ]")).getDamageTakenPerSecond() == 605)
    assert(controller.getAllCombatInstances()(0).getCombatActorByIdString(("[ Type ID: 3915326546771968 Instance ID: 28040000040093 ]")).getDamageTakenPerSecond() == 20)
    assert(controller.getAllCombatInstances()(0).getCombatActorByIdString(("[ Type ID: Heavy Sloth Instance ID: Heavy Sloth ]")).getDamageTakenPerSecond() == 469)
  }

  "Parser" should "get total healing done correctly for each actor" in {
    // TODO: These values are not checked
    assert(controller.getAllCombatInstances()(0).getCombatActorByIdString(("[ Type ID: 3266932513964032 Instance ID: 28040000023320 ]")).getHealingDone() == 0)
    assert(controller.getAllCombatInstances()(0).getCombatActorByIdString(("[ Type ID: 3266941103898624 Instance ID: 28040000035026 ]")).getHealingDone() == 0)
    assert(controller.getAllCombatInstances()(0).getCombatActorByIdString(("[ Type ID: 3266932513964032 Instance ID: 28040000034858 ]")).getHealingDone() == 0)
    assert(controller.getAllCombatInstances()(0).getCombatActorByIdString(("[ Type ID: 3915326546771968 Instance ID: 28040000040093 ]")).getHealingDone() == 12677) // this is my companion
    assert(controller.getAllCombatInstances()(0).getCombatActorByIdString(("[ Type ID: Heavy Sloth Instance ID: Heavy Sloth ]")).getHealingDone() == 0)
  }

  "Parser" should "get healing per second correctly for each actor" in {
    // TODO: These values are not checked
    assert(controller.getAllCombatInstances()(0).getCombatActorByIdString(("[ Type ID: 3266932513964032 Instance ID: 28040000023320 ]")).getHealingDonePerSecond() == 0)
    assert(controller.getAllCombatInstances()(0).getCombatActorByIdString(("[ Type ID: 3266941103898624 Instance ID: 28040000035026 ]")).getHealingDonePerSecond() == 0)
    assert(controller.getAllCombatInstances()(0).getCombatActorByIdString(("[ Type ID: 3266932513964032 Instance ID: 28040000034858 ]")).getHealingDonePerSecond() == 0)
    assert(controller.getAllCombatInstances()(0).getCombatActorByIdString(("[ Type ID: 3915326546771968 Instance ID: 28040000040093 ]")).getHealingDonePerSecond() == 1056) // this is my companion
    assert(controller.getAllCombatInstances()(0).getCombatActorByIdString(("[ Type ID: Heavy Sloth Instance ID: Heavy Sloth ]")).getHealingDonePerSecond() == 0)
  }

  "Parser" should "get combat time correctly" in {
    assert(controller.getAllCombatInstances()(0).combatTimeSeconds == 12)

  }


  controller.parseLatest(parseTestLines)
}

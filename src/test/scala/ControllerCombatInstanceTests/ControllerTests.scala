package ControllerCombatInstanceTests

import Combat.CombatInstance
import Controller.Controller
import org.scalatest.flatspec.AnyFlatSpec
import parser.Parser
import parsing.Actors.NoneActor
import parsing.Result.ApplyEffect
import patterns.Actions.SafeLogin
import patterns.Result.{EnterCombat, ExitCombat}

class ControllerTests extends AnyFlatSpec{

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

  "Controller" should "add up total damage correctly from each Actor" in {
    assert(controller.getAllCombatInstances()(0).getCombatActorByIdString(("[ Type ID: 3266932513964032 Instance ID: 28040000023320 ]")).getDamageDone() == 449)
    assert(controller.getAllCombatInstances()(0).getCombatActorByIdString(("[ Type ID: 3266941103898624 Instance ID: 28040000035026 ]")).getDamageDone() == 2199)
    assert(controller.getAllCombatInstances()(0).getCombatActorByIdString(("[ Type ID: 3266932513964032 Instance ID: 28040000034858 ]")).getDamageDone() == 3198)
    assert(controller.getAllCombatInstances()(0).getCombatActorByIdString(("[ Type ID: 3915326546771968 Instance ID: 28040000040093 ]")).getDamageDone() == 654)
    assert(controller.getAllCombatInstances()(0).getCombatActorByIdString(("[ Type ID: Heavy Sloth Instance ID: Heavy Sloth ]")).getDamageDone() == 22288)
  }

//  "Controller" should "get total damage correctly for each actor" in {
//    assert(controller.getAllCombatInstances()(0).getCombatDamage())
//  }


  controller.parseLatest(parseTestLines)


}

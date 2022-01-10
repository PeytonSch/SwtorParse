package ControllerCombatInstanceTests

import Combat.CombatInstance
import Controller.Controller
import org.scalatest.flatspec.AnyFlatSpec
import parser.Parser
import parsing.Actors.NoneActor
import parsing.Result.ApplyEffect
import patterns.Actions.SafeLogin
import patterns.Result.{EnterCombat, ExitCombat}

class ControllerIterativeTests extends AnyFlatSpec{

  val controller : Controller = new Controller()

  val parser : Parser = new Parser()

  val parseTestLines = parser.getNewLines("SampleLogs/ForTests/SingleCombat.txt")


  /**
   * The tests in here are not nested in AnyFlatSpec because they need to be eveluated
   * as the parameters change, not after running. The tests will be aborted if any of
   * these fail as a result. Not ideal but gives better coverage
   */

  for (logInfo <- parseTestLines) {

    /**
     * Check for Entering or Exiting Combat
     */
    // Check to see if we entered or exit combat
    if(logInfo.getResult().isInstanceOf[EnterCombat]) {
      controller.startNewCombat()
      assert(controller.getCurrentCombat() != null)
      assert(controller.getCurrentCombat().isInstanceOf[CombatInstance])

    } else if (logInfo.getResult().isInstanceOf[ExitCombat]) {
      controller.endCombat()
      assert(controller.getCurrentCombat() == null)
      assert(controller.getAllCombatInstances().size == 1)
    }

    // Check for login action
    if (logInfo.getAction().isInstanceOf[SafeLogin]){
      controller.setPlayerToon(logInfo.getPerformer().getName())
      assert(controller.getPlayerToonName() != "")
    }

    // if we are currently in combat
    if (controller.currentCombat != null) {

      // Make sure the actor and target are in the combat actors set
      controller.appendToCombatActors(logInfo.getPerformer())
      if (!logInfo.getPerformer().isInstanceOf[NoneActor]){
        // This test no longer is all that applicable with the introduciton of CombatActorInstances
        //  assert(controller.getLastCurrentCombatActor().toString() == logInfo.getPerformer().toString)
      }
      controller.appendToCombatActors(logInfo.getTarget())
      if (!logInfo.getTarget().isInstanceOf[NoneActor]){
        // This test no longer is all that applicable with the introduciton of CombatActorInstances
//        assert(controller.getLastCurrentCombatActor().toString() == logInfo.getTarget().toString)
      }

      // see if the Result is an ApplyEffect and see if its name is Damage
      if (logInfo.getResult().isInstanceOf[ApplyEffect] && logInfo.getResult().asInstanceOf[ApplyEffect].getName() == "Damage") {
        controller.getCurrentCombat().addDamageToCurrentCombat(logInfo)
      }

    }


  }


}

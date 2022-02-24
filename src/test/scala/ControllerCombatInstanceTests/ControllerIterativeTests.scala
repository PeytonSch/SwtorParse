//package ControllerCombatInstanceTests
//
//import Combat.CombatInstance
//import Controller.Controller
//import org.scalatest.flatspec.AnyFlatSpec
//import parser.Parser
//import parsing.Actors.NoneActor
//import parsing.Result.ApplyEffect
//import patterns.Actions.SafeLogin
//import patterns.Result.{EnterCombat, ExitCombat}
//
//class ControllerIterativeTests extends AnyFlatSpec{
//
//  Parser.resetParser()
//
//  val parseTestLines = Parser.getNewLines("SampleLogs/ForTests/SingleCombat.txt")
//
//
//  /**
//   * The tests in here are not nested in AnyFlatSpec because they need to be eveluated
//   * as the parameters change, not after running. The tests will be aborted if any of
//   * these fail as a result. Not ideal but gives better coverage
//   */
//
//  for (logInfo <- parseTestLines) {
//
//    /**
//     * Check for Entering or Exiting Combat
//     */
//    // Check to see if we entered or exit combat
//    if(logInfo.getResult().isInstanceOf[EnterCombat]) {
//      Controller.startNewCombat(logInfo)
//      assert(Controller.getCurrentCombat() != null)
//      assert(Controller.getCurrentCombat().isInstanceOf[CombatInstance])
//
//    } else if (logInfo.getResult().isInstanceOf[ExitCombat]) {
//      Controller.endCombat()
//      assert(Controller.getCurrentCombat() == null)
//      assert(Controller.getAllCombatInstances().size == 1)
//    }
//
//    // Check for login action
//    if (logInfo.getAction().isInstanceOf[SafeLogin]){
//      Controller.setPlayerToon(logInfo.getPerformer().getName())
//      assert(Controller.getPlayerToonName() != "")
//    }
//
//    // if we are currently in combat
//    if (Controller.currentCombat != null) {
//
//      // Make sure the actor and target are in the combat actors set
//      Controller.appendToCombatActors(logInfo.getPerformer())
//      if (!logInfo.getPerformer().isInstanceOf[NoneActor]){
//        // This test no longer is all that applicable with the introduciton of CombatActorInstances
//        //  assert(Controller.getLastCurrentCombatActor().toString() == logInfo.getPerformer().toString)
//      }
//      Controller.appendToCombatActors(logInfo.getTarget())
//      if (!logInfo.getTarget().isInstanceOf[NoneActor]){
//        // This test no longer is all that applicable with the introduciton of CombatActorInstances
////        assert(Controller.getLastCurrentCombatActor().toString() == logInfo.getTarget().toString)
//      }
//
//      // see if the Result is an ApplyEffect and see if its name is Damage
//      if (logInfo.getResult().isInstanceOf[ApplyEffect] && logInfo.getResult().asInstanceOf[ApplyEffect].getName() == "Damage") {
//        Controller.getCurrentCombat().addDamageToCurrentCombat(logInfo)
//      }
//
//    }
//
//
//  }
//
//
//}

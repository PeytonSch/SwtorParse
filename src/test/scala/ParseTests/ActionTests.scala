package ParseTests

import org.scalatest.flatspec.AnyFlatSpec
import parsing.Actions.DefaultAction
import parsing.FactoryClasses
import patterns.Actions.{NoAction, SafeLogin}
import patterns.Result.{EnterCombat, ExitCombat}

class ActionTests extends AnyFlatSpec{

  val loginAction = "[21:57:07.765] [@Heavy Sloth#689203382607232|(-388.22,19.78,94.98,-21.95)|(44307/44307)] [=] [Safe Login {973870949466112}] [ApplyEffect {836045448945477}: Safe Login Immunity {973870949466372}]"
  val noAction = "[22:06:29.565] [@Heavy Sloth#689203382607232|(66.58,-221.82,-11.33,-0.75)|(2909/2909)] [] [] [Event {836045448945472}: ExitCombat {836045448945490}]"
  val defaultAction = "[21:57:10.047] [@Heavy Sloth#689203382607232/Arcann {3915326546771968}:28040000128828|(-390.07,19.03,94.98,-111.95)|(42695/42695)] [@Heavy Sloth#689203382607232|(-388.22,19.78,94.98,-21.95)|(46271/46271)] [Mending {3590163162726400}] [ApplyEffect {836045448945477}: Mending {3590163162726400}]"

  val factory = new FactoryClasses

  val loginTest = factory.actionFromLine(loginAction)
  val noActionTest = factory.actionFromLine(noAction)
  val defaultActionTest = factory.actionFromLine(defaultAction)

  "Action types" should "return correctly" in {

    //println(noActionTest.toString)

    assert(noActionTest.isInstanceOf[NoAction])
    assert(loginTest.isInstanceOf[SafeLogin])
    assert(defaultActionTest.isInstanceOf[DefaultAction])
  }

}

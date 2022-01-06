package ParseTests

import org.scalatest.flatspec.AnyFlatSpec
import parsing.FactoryClasses
import parsing.Result.{ApplyEffect, Event}
import patterns.Result.AreaEntered

class ResultTests extends AnyFlatSpec {

  val playerLogLine = "[22:04:31.735] [@Heavy Sloth#689203382607232|(1.09,-123.36,-11.44,-2.26)|(2909/2909)] [Acolyte Henchman {379421705895936}:26518005413256|(0.32,-122.48,-11.44,150.00)|(0/90)] [Assault {898601647603712}] [ApplyEffect {836045448945477}: Damage {836045448945501}] (51 ~0 energy {836045448940874} -)"
  val companionLogLine = "[22:04:03.036] [@Heavy Sloth#689203382607232/Arcann {3915326546771968}:26518005410002|(-56.35,-60.31,-0.57,-85.17)|(2878/2944)] [=] [ {4196681264398336}] [ApplyEffect {836045448945477}: Unnatural Might {4196681264398641}]"
  val npcLogLine = "[22:04:30.903] [Acolyte Henchman {379421705895936}:26518005413256|(0.32,-122.48,-11.44,150.00)|(0/90)] [] [] [Event {836045448945472}: TargetCleared {836045448953669}]"
  val areaEnteredLine = "[22:09:10.592] [@Heavy Sloth#689203382607232|(373.97,241.99,10.26,178.46)|(360708/377823)] [] [] [AreaEntered {836045448953664}: Rishi {833571547775718}] (HE600) <v7.0.0b>"


  val factory = new FactoryClasses

  val baseInformationPlayerLogLine = factory.resultFromLine(playerLogLine)
  val baseInformationCompanionLogLine = factory.resultFromLine(companionLogLine)
  val baseInformationNpcLogLing = factory.resultFromLine(npcLogLine)
  val areaEnteredTest = factory.resultFromLine(areaEnteredLine)

  "Result From Line" should "be correctly parsed" in {
    assert(baseInformationPlayerLogLine.asInstanceOf[ApplyEffect].toString == "[ Type: ApplyEffect Name: Damage ]")
    assert(baseInformationCompanionLogLine.toString == "[ Type: ApplyEffect Name: Unnatural Might ]")
    assert(baseInformationNpcLogLing.asInstanceOf[Event].toString == "[ Type: Event Name: TargetCleared ]")
    assert(areaEnteredTest.isInstanceOf[AreaEntered])
  }

}

package ParseTests

import org.scalatest.flatspec.AnyFlatSpec
import parsing.FactoryClasses

class TargetTests extends AnyFlatSpec{

  val playerLogLine = "[22:04:31.735] [@Heavy Sloth#689203382607232|(1.09,-123.36,-11.44,-2.26)|(2909/2909)] [Acolyte Henchman {379421705895936}:26518005413256|(0.32,-122.48,-11.44,150.00)|(0/90)] [Assault {898601647603712}] [ApplyEffect {836045448945477}: Damage {836045448945501}] (51 ~0 energy {836045448940874} -)"
  val companionLogLine = "[22:04:03.036] [@Heavy Sloth#689203382607232/Arcann {3915326546771968}:26518005410002|(-56.35,-60.31,-0.57,-85.17)|(2878/2944)] [=] [ {4196681264398336}] [ApplyEffect {836045448945477}: Unnatural Might {4196681264398641}]"
  val npcLogLine = "[22:04:30.903] [Acolyte Henchman {379421705895936}:26518005413256|(0.32,-122.48,-11.44,150.00)|(0/90)] [] [] [Event {836045448945472}: TargetCleared {836045448953669}]"

  val factory = new FactoryClasses

  val baseInformationPlayerLogLine = factory.targetActorFromLogLineString(playerLogLine)
  val baseInformationCompanionLogLine = factory.targetActorFromLogLineString(companionLogLine)
  val baseInformationNpcLogLing = factory.targetActorFromLogLineString(npcLogLine)

  "Target information" should "extract Target name correctly" in {
    assert(baseInformationPlayerLogLine.getName() == "Acolyte Henchman")
    assert(baseInformationNpcLogLing.getName() == "")
    assert(baseInformationCompanionLogLine.getName() == "Arcann")
  }

  "Target information" should "extract Target Health correctly" in {
    assert(baseInformationPlayerLogLine.getHealth().toString == "[ Current Health: 0 Max Health: 90 ]")
    assert(baseInformationNpcLogLing.getHealth().toString == "[ Current Health: 0 Max Health: 0 ]")
    assert(baseInformationCompanionLogLine.getHealth().toString == "[ Current Health: 2878 Max Health: 2944 ]")
  }

}

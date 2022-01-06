package ParseTests

import org.scalatest.flatspec.AnyFlatSpec
import parsing.Actors.{Companion, Npc}
import parsing.FactoryClasses

class TimeStampTests extends AnyFlatSpec {

  val playerLogLine = "[22:04:31.735] [@Heavy Sloth#689203382607232|(1.09,-123.36,-11.44,-2.26)|(2909/2909)] [Acolyte Henchman {379421705895936}:26518005413256|(0.32,-122.48,-11.44,150.00)|(0/90)] [Assault {898601647603712}] [ApplyEffect {836045448945477}: Damage {836045448945501}] (51 ~0 energy {836045448940874} -)"
  val companionLogLine = "[22:04:03.036] [@Heavy Sloth#689203382607232/Arcann {3915326546771968}:26518005410002|(-56.35,-60.31,-0.57,-85.17)|(2878/2944)] [=] [ {4196681264398336}] [ApplyEffect {836045448945477}: Unnatural Might {4196681264398641}]"
  val npcLogLine = "[22:04:30.903] [Acolyte Henchman {379421705895936}:26518005413256|(0.32,-122.48,-11.44,150.00)|(0/90)] [] [] [Event {836045448945472}: TargetCleared {836045448953669}]"

  val factory = new FactoryClasses

  val baseInformationPlayerLogLine = factory.timestampFromLine(playerLogLine)
  val baseInformationCompanionLogLine = factory.timestampFromLine(companionLogLine)
  val baseInformationNpcLogLing = factory.timestampFromLine(npcLogLine)



  "Base information" should "extract timestamps correctly" in {
    assert(baseInformationPlayerLogLine.toString == "22:04:31.735")
    assert(baseInformationNpcLogLing.toString == "22:04:30.903")
    assert(baseInformationCompanionLogLine.toString == "22:04:03.036")
  }


}

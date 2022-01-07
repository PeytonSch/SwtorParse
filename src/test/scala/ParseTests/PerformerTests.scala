package ParseTests

import org.scalatest.flatspec.AnyFlatSpec
import parsing.Actors.{Companion, NoneActor, Npc}
import parsing.FactoryClasses

class PerformerTests extends AnyFlatSpec{


  val playerLogLine = "[22:04:31.735] [@Heavy Sloth#689203382607232|(1.09,-123.36,-11.44,-2.26)|(2909/2909)] [Acolyte Henchman {379421705895936}:26518005413256|(0.32,-122.48,-11.44,150.00)|(0/90)] [Assault {898601647603712}] [ApplyEffect {836045448945477}: Damage {836045448945501}] (51 ~0 energy {836045448940874} -)"
  val companionLogLine = "[22:04:03.036] [@Heavy Sloth#689203382607232/Arcann {3915326546771968}:26518005410002|(-56.35,-60.31,-0.57,-85.17)|(2878/2944)] [=] [ {4196681264398336}] [ApplyEffect {836045448945477}: Unnatural Might {4196681264398641}]"
  val npcLogLine = "[22:04:30.903] [Acolyte Henchman {379421705895936}:26518005413256|(0.32,-122.48,-11.44,150.00)|(0/90)] [] [] [Event {836045448945472}: TargetCleared {836045448953669}]"

  val noPerformer = "[22:05:29.820] [] [@Heavy Sloth#689203382607232|(28.42,-173.66,-12.49,6.00)|(2909/2909)] [Protective Barrier {4238475591155712}] [RemoveEffect {836045448945478}: Protective Barrier {4238475591155712}]"

  val factory = new FactoryClasses

  val baseInformationPlayerLogLine = factory.performingActorFromLogLineString(playerLogLine)
  val baseInformationCompanionLogLine = factory.performingActorFromLogLineString(companionLogLine)
  val baseInformationNpcLogLing = factory.performingActorFromLogLineString(npcLogLine)
  val noPerformerTest = factory.performingActorFromLogLineString(noPerformer)

  "Extractors" should "return NoneActor correctly" in {
    assert(noPerformerTest.isInstanceOf[NoneActor])
  }

  "Extractors" should "extract player name correctly" in {
    assert(baseInformationPlayerLogLine.getName() == "Heavy Sloth")
    assert(baseInformationNpcLogLing.getName() == "Acolyte Henchman")
    assert(baseInformationCompanionLogLine.getName() == "Arcann")
  }

  "Base information" should "extract positions correctly" in {
    assert(baseInformationPlayerLogLine.getPosition().toString == "[ x_dir: 1.09 y_dir: -123.36 z_dir: -11.44 facing: -2.26 ]")
    assert(baseInformationNpcLogLing.getPosition().toString == "[ x_dir: 0.32 y_dir: -122.48 z_dir: -11.44 facing: 150.0 ]")
    assert(baseInformationCompanionLogLine.getPosition().toString == "[ x_dir: -56.35 y_dir: -60.31 z_dir: -0.57 facing: -85.17 ]")
  }

  "Base information" should "extract health correctly" in {
    assert(baseInformationPlayerLogLine.getHealth().toString == "[ Current Health: 2909 Max Health: 2909 ]")
    assert(baseInformationNpcLogLing.getHealth().toString == "[ Current Health: 0 Max Health: 90 ]")
    assert(baseInformationCompanionLogLine.getHealth().toString == "[ Current Health: 2878 Max Health: 2944 ]")
  }

  "Base information" should "extract npc and companion IDs correctly" in {
    assert(baseInformationCompanionLogLine.asInstanceOf[Companion].getId.toString == ("[ Type ID: 3915326546771968 Instance ID: 26518005410002 ]"))
    assert(baseInformationNpcLogLing.asInstanceOf[Npc].getId().toString == "[ Type ID: 379421705895936 Instance ID: 26518005413256 ]")
  }

}

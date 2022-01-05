package ParseTests

import org.scalatest.flatspec.AnyFlatSpec
import patterns.Actors.{Companion, Npc}
import patterns.FactoryClasses

class BaseInformationTests extends AnyFlatSpec {

  val playerLogLine = "[22:04:31.735] [@Heavy Sloth#689203382607232|(1.09,-123.36,-11.44,-2.26)|(2909/2909)] [Acolyte Henchman {379421705895936}:26518005413256|(0.32,-122.48,-11.44,150.00)|(0/90)] [Assault {898601647603712}] [ApplyEffect {836045448945477}: Damage {836045448945501}] (51 ~0 energy {836045448940874} -)"
  val companionLogLine = "[22:04:03.036] [@Heavy Sloth#689203382607232/Arcann {3915326546771968}:26518005410002|(-56.35,-60.31,-0.57,-85.17)|(2878/2944)] [=] [ {4196681264398336}] [ApplyEffect {836045448945477}: Unnatural Might {4196681264398641}]"
  val npcLogLine = "[22:04:30.903] [Acolyte Henchman {379421705895936}:26518005413256|(0.32,-122.48,-11.44,150.00)|(0/90)] [] [] [Event {836045448945472}: TargetCleared {836045448953669}]"

  val factory = new FactoryClasses

  val baseInformationPlayerLogLine = factory.baseInformationFromLine(playerLogLine)
  val baseInformationCompanionLogLine = factory.baseInformationFromLine(companionLogLine)
  val baseInformationNpcLogLing = factory.baseInformationFromLine(npcLogLine)

  "Base information" should "extract player name correctly" in {
    assert(baseInformationPlayerLogLine.getActor().getName() == "Heavy Sloth")
    assert(baseInformationNpcLogLing.getActor().getName() == "Acolyte Henchman")
    assert(baseInformationCompanionLogLine.getActor().getName() == "Arcann")
  }

  "Base information" should "extract timestamps correctly" in {
    assert(baseInformationPlayerLogLine.getTimestamp().toString == "22:04:31.735")
    assert(baseInformationNpcLogLing.getTimestamp().toString == "22:04:30.903")
    assert(baseInformationCompanionLogLine.getTimestamp().toString == "22:04:03.036")
  }

  "Base information" should "extract positions correctly" in {
    assert(baseInformationPlayerLogLine.getActor().getPosition().toString == "[ x_dir: 1.09 y_dir: -123.36 z_dir: -11.44 facing: -2.26 ]")
    assert(baseInformationNpcLogLing.getActor().getPosition().toString == "[ x_dir: 0.32 y_dir: -122.48 z_dir: -11.44 facing: 150.0 ]")
    assert(baseInformationCompanionLogLine.getActor().getPosition().toString == "[ x_dir: -56.35 y_dir: -60.31 z_dir: -0.57 facing: -85.17 ]")
  }

  "Base information" should "extract health correctly" in {
    assert(baseInformationPlayerLogLine.getActor().getHealth().toString == "[ Current Health: 2909 Max Health: 2909 ]")
    assert(baseInformationNpcLogLing.getActor().getHealth().toString == "[ Current Health: 0 Max Health: 90 ]")
    assert(baseInformationCompanionLogLine.getActor().getHealth().toString == "[ Current Health: 2878 Max Health: 2944 ]")
  }

  "Base information" should "extract npc and companion IDs correctly" in {
    assert(baseInformationCompanionLogLine.getActor().asInstanceOf[Companion].getId.toString == ("[ Type ID: 3915326546771968 Instance ID: 26518005410002 ]"))
    assert(baseInformationNpcLogLing.getActor().asInstanceOf[Npc].getId().toString == "[ Type ID: 379421705895936 Instance ID: 26518005413256 ]")
  }

}

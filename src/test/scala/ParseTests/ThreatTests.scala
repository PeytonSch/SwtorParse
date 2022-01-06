package ParseTests

import org.scalatest.flatspec.AnyFlatSpec
import patterns.FactoryClasses

class ThreatTests extends AnyFlatSpec{

  val noThreat1 = "[22:04:31.735] [@Heavy Sloth#689203382607232|(1.09,-123.36,-11.44,-2.26)|(2909/2909)] [Acolyte Henchman {379421705895936}:26518005413256|(0.32,-122.48,-11.44,150.00)|(0/90)] [Assault {898601647603712}] [ApplyEffect {836045448945477}: Damage {836045448945501}] (51 ~0 energy {836045448940874} -)"
  val noThreat2 = "[22:04:03.036] [@Heavy Sloth#689203382607232/Arcann {3915326546771968}:26518005410002|(-56.35,-60.31,-0.57,-85.17)|(2878/2944)] [=] [ {4196681264398336}] [ApplyEffect {836045448945477}: Unnatural Might {4196681264398641}]"
  val noThreat3 = "[22:04:30.903] [Acolyte Henchman {379421705895936}:26518005413256|(0.32,-122.48,-11.44,150.00)|(0/90)] [] [] [Event {836045448945472}: TargetCleared {836045448953669}]"

  val threat1 = "[21:05:46.233] [@Heavy Sloth#689203382607232|(-607.66,226.28,11.53,1.48)|(46271/46271)] [Dread Host Slaver {3266941103898624}:28040000048729|(-609.43,223.91,11.26,-120.22)|(0/6740)] [Force Scream {998270658675065}] [ApplyEffect {836045448945477}: Damage {836045448945501}] (4708* ~3496 kinetic {836045448940873}) <4708>"
  val threat2 = "[21:05:46.225] [@Heavy Sloth#689203382607232/Arcann {3915326546771968}:28040000040093|(-611.39,224.82,11.21,-64.94)|(44109/44109)] [@Heavy Sloth#689203382607232|(-607.66,226.28,11.53,1.48)|(46271/46271)] [Protective Barrier {4238475591155712}] [ApplyEffect {836045448945477}: Heal {836045448945500}] (2597* ~563) <112>"
  val threat3 = "[20:59:52.340] [@Heavy Sloth#689203382607232/Arcann {3915326546771968}:28040000040093|(-609.50,223.78,11.27,119.73)|(44109/44109)] [Dread Host Soldier {3266932513964032}:28040000034858|(-611.39,224.86,11.22,-105.21)|(73/7950)] [Melee Attack {813625719652352}] [ApplyEffect {836045448945477}: Damage {836045448945501}] (462 energy {836045448940874} -shield {836045448945509} (492 absorbed {836045448945511})) <185>"


  val factory = new FactoryClasses

  val noThreatTest1 = factory.threatFromLine(noThreat1)
  val noThreatTest2 = factory.threatFromLine(noThreat2)
  val noThreatTest3 = factory.threatFromLine(noThreat3)

  val threatTest1 = factory.threatFromLine(threat1)
  val threatTest2 = factory.threatFromLine(threat2)
  val threatTest3 = factory.threatFromLine(threat3)

  "Lines without threat" should "report 0 threat" in {
    assert(threatTest1.getValue() == 4708)
    assert(threatTest2.getValue() == 112)
    assert(threatTest3.getValue() == 185)
  }

  "Lines with threat" should "report correct threat value" in {
    assert(noThreatTest1.getValue() == 0)
    assert(noThreatTest2.getValue() == 0)
    assert(noThreatTest3.getValue() == 0)
  }

}

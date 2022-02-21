package ParseTests

import org.scalatest.flatspec.AnyFlatSpec
import parsing.FactoryClasses
import parsing.Values.{CompleteNegation, NoValue, PartialNegation, RegularValue}

class ValueTests extends AnyFlatSpec{

  val nonCritOver = "[22:04:31.735] [@Heavy Sloth#689203382607232|(1.09,-123.36,-11.44,-2.26)|(2909/2909)] [Acolyte Henchman {379421705895936}:26518005413256|(0.32,-122.48,-11.44,150.00)|(0/90)] [Assault {898601647603712}] [ApplyEffect {836045448945477}: Damage {836045448945501}] (51 ~0 energy {836045448940874} -)"
  val noValueLog1 = "[22:04:03.036] [@Heavy Sloth#689203382607232/Arcann {3915326546771968}:26518005410002|(-56.35,-60.31,-0.57,-85.17)|(2878/2944)] [=] [ {4196681264398336}] [ApplyEffect {836045448945477}: Unnatural Might {4196681264398641}]"
  val noValueLog2 = "[22:04:30.903] [Acolyte Henchman {379421705895936}:26518005413256|(0.32,-122.48,-11.44,150.00)|(0/90)] [] [] [Event {836045448945472}: TargetCleared {836045448953669}]"
  val noValueLog3 = "[20:36:20.832] [@Echoyxe#689140100750244|(-6.07,191.94,4.21,-2.03)|(397145/397145)] [Aberrant Guardian {3295232053477376}:10771000189930|(7.59,179.37,4.21,163.16)|(2683125/2683125)] [] [Event {836045448945472}: Taunt {836045448945488}] <12>"
  val missLogLine = "[22:05:32.451] [Acolyte Henchman {379421705895936}:26518005416704|(67.84,-162.62,-11.45,92.27)|(130/130)] [@Igrin#689797178977446|(64.01,-162.46,-11.44,-102.52)|(1879/1999)] [Ranged Attack {813449625993216}] [ApplyEffect {836045448945477}: Damage {836045448945501}] (0 -miss {836045448945502}) <1>"
  val critLogLine = "[22:05:46.237] [@Igrin#689797178977446|(53.46,-188.57,-12.50,23.77)|(1999/1999)] [Competing Acolyte {379408820994048}:26518005417167|(52.57,-189.84,-12.50,-148.47)|(0/240)] [Smash {807801743998976}] [ApplyEffect {836045448945477}: Damage {836045448945501}] (199* kinetic {836045448940873}) <199>"
  val healLine = "[22:05:55.233] [@Igrin#689797178977446/Vette {290296839536640}:26518005402831|(54.08,-187.91,-12.50,-84.34)|(2100/2100)] [@Igrin#689797178977446|(65.76,-189.12,-12.30,-90.10)|(1999/1999)] [Kolto Shell {4238290907561984}] [ApplyEffect {836045448945477}: Heal {836045448945500}] (63 ~33) <11>"
  val areaEnteredLine = "[22:09:10.592] [@Heavy Sloth#689203382607232|(373.97,241.99,10.26,178.46)|(360708/377823)] [] [] [AreaEntered {836045448953664}: Rishi {833571547775718}] (HE600) <v7.0.0b>"
  val negationLine = "[20:59:52.340] [@Heavy Sloth#689203382607232/Arcann {3915326546771968}:28040000040093|(-609.50,223.78,11.27,119.73)|(44109/44109)] [Dread Host Soldier {3266932513964032}:28040000034858|(-611.39,224.86,11.22,-105.21)|(73/7950)] [Melee Attack {813625719652352}] [ApplyEffect {836045448945477}: Damage {836045448945501}] (462 energy {836045448940874} -shield {836045448945509} (492 absorbed {836045448945511})) <185>"

  val factory = new FactoryClasses

  val nonCritOverTest = factory.valueFromLine(nonCritOver)
  val noValueLogTest1 = factory.valueFromLine(noValueLog1)
  val noValueLogTest2 = factory.valueFromLine(noValueLog2)
  val noValueLogTest3 = factory.valueFromLine(noValueLog3)
  val missTest = factory.valueFromLine(missLogLine)
  val critTest = factory.valueFromLine(critLogLine)
  val healTest = factory.valueFromLine(healLine)
  val negationTest = factory.valueFromLine(negationLine)
  val areaEnteredTest = factory.valueFromLine(areaEnteredLine)

  "No value log lines" should "return no value" in {
    assert(noValueLogTest1.isInstanceOf[NoValue])
    assert(noValueLogTest2.isInstanceOf[NoValue])
    assert(areaEnteredTest.isInstanceOf[NoValue])
    assert(noValueLogTest3.isInstanceOf[NoValue])
  }

  "Lines with Regular values" should "return instances of regular value" in {
    assert(nonCritOverTest.isInstanceOf[RegularValue])
    assert(healTest.isInstanceOf[RegularValue])
    assert(negationTest.isInstanceOf[RegularValue])
  }

  "Complete Negation Lines" should "return instance of CompleteNegation class" in {
    assert(missTest.isInstanceOf[CompleteNegation])
  }

  "Values" should "return correct base values" in {
    assert(nonCritOverTest.asInstanceOf[RegularValue].getBaseValue() == 51)
    assert(critTest.asInstanceOf[RegularValue].getBaseValue() == 199)
    assert(healTest.asInstanceOf[RegularValue].getBaseValue() == 63)
    assert(negationTest.asInstanceOf[RegularValue].getBaseValue() == 462)
  }

  "Crit Values" should "return correctcrit status" in {
    assert(critTest.asInstanceOf[RegularValue].getCrit() == true)
    assert(nonCritOverTest.asInstanceOf[RegularValue].getCrit() == false)
    assert(negationTest.asInstanceOf[RegularValue].getCrit() == false)
  }

  "Excess values" should "return correctly" in {
    assert(nonCritOverTest.asInstanceOf[RegularValue].getExcess() == 0)
    assert(critTest.asInstanceOf[RegularValue].getExcess() == 0)
    assert(healTest.asInstanceOf[RegularValue].getExcess() == 33)
    assert(negationTest.asInstanceOf[RegularValue].getExcess() == 0)
  }

  "Value types" should "return correctly" in {
    assert(nonCritOverTest.asInstanceOf[RegularValue].getValueType() == "energy")
    assert(critTest.asInstanceOf[RegularValue].getValueType() == "kinetic")
    assert(healTest.asInstanceOf[RegularValue].getValueType() == "")
    assert(negationTest.asInstanceOf[RegularValue].getValueType() == "energy")
  }

  "Packaged Regular Values" should "have the correct information for all parts" in {

    assert(nonCritOverTest.asInstanceOf[RegularValue].packagedNoNegationForTests() == (51,false,0,"energy","836045448940874"))
    assert(critTest.asInstanceOf[RegularValue].packagedNoNegationForTests() == (199,true,0,"kinetic","836045448940873"))
    assert(healTest.asInstanceOf[RegularValue].packagedNoNegationForTests() == (63,false,33,"",""))
    assert(negationTest.asInstanceOf[RegularValue].packagedNoNegationForTests() == (462,false,0,"energy","836045448940874"))

  }

  "Partial Negations" should "return correctly" in {
    assert(nonCritOverTest.asInstanceOf[RegularValue].getPartialNegation().getValues() == ("","",0,"",""))
    assert(critTest.asInstanceOf[RegularValue].getPartialNegation().getValues() == ("","",0,"",""))
    assert(healTest.asInstanceOf[RegularValue].getPartialNegation().getValues() == ("","",0,"",""))
    assert(negationTest.asInstanceOf[RegularValue].getPartialNegation().getValues() == ("shield","836045448945509",492,"absorbed","836045448945511"))
  }





}

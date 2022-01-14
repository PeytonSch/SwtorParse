package ControllerCombatInstanceTests

import Controller.Controller
import org.scalatest.flatspec.AnyFlatSpec
import parser.Parser
import parsing.subTypes.LogTimestamp

import java.time.{LocalDate, LocalTime}

class CombatTimeStampTests extends AnyFlatSpec{

  val controller : Controller = new Controller()

  val parser : Parser = new Parser()

  val parseTestLines = parser.getNewLines("SampleLogs/ForTests/SingleCombat.txt")

  controller.parseLatest(parseTestLines)


  /**
   * As of creating this test file, it has to do with some more finite features of combat instances that
   * do not go through the controller. This will probably be pretty minimal, I am initially using it
   * to do some timestamp testing.
   */

  "Timestamps" should "get differences correctly" in {
    val time1 : LogTimestamp = new LogTimestamp("20:59:30.659")
    val time2 : LogTimestamp = new LogTimestamp("20:59:31.659")
    val time3 : LogTimestamp = new LogTimestamp("20:59:31.000")
    val time4 : LogTimestamp = new LogTimestamp("21:01:31.500")
    val time5 : LogTimestamp = new LogTimestamp("00:15:20.231")

    assert(time2 - time1 == 1) // exactly 1 second
    assert(time3 - time1 == 0) // Round Down to 0 seconds
    assert(time4 - time1 == 121) // Round up to nearest second, then new minute and hour
    assert(11749.572.round.toInt == 11750)
    assert(time5 - time1 == 11750)

  }

  "Timestamps" should "get total seconds correctly" in {
    val time1 : LogTimestamp = new LogTimestamp("20:59:30.659")
    val time2 : LogTimestamp = new LogTimestamp("20:59:31.659")
    val time3 : LogTimestamp = new LogTimestamp("20:59:31.000")
    val time4 : LogTimestamp = new LogTimestamp("21:01:31.500")

    assert(time1.getSecondTotal() == 75570.659)
    assert(time2.getSecondTotal() == 75571.659)
    assert(time3.getSecondTotal() == 75571.000)
    assert(time4.getSecondTotal() == 75691.500)

  }

  //"G:\Program Files\Java\jdk-16.0.2\bin\java.exe" -server -Xmx1536M -Dsbt.supershell=false -Didea.managed=true -Dfile.encoding=UTF-8 "-Didea.installation.dir=C:\Program Files\JetBrains\IntelliJ IDEA 2021.1.1" -Dsbt.log.noformat=true -jar C:\Users\Peyton\AppData\Roaming\JetBrains\IntelliJIdea2021.1\plugins\Scala\launcher\sbt-launch.jar early(addPluginSbtFile=\"\"\"C:\Users\Peyton\AppData\Local\Temp\idea1.sbt\"\"\") "; set ideaPort in Global := 64526 ; idea-shell"




}

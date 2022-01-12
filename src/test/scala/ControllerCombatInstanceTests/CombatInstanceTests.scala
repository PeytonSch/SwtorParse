package ControllerCombatInstanceTests

import Controller.Controller
import org.scalatest.flatspec.AnyFlatSpec
import parser.Parser

import java.time.{LocalDate, LocalTime}

class CombatInstanceTests extends AnyFlatSpec{

  val controller : Controller = new Controller()

  val parser : Parser = new Parser()

  val parseTestLines = parser.getNewLines("SampleLogs/ForTests/SingleCombat.txt")

  controller.parseLatest(parseTestLines)


  /**
   * As of creating this test file, it has to do with some more finite features of combat instances that
   * do not go through the controller. This will probably be pretty minimal, I am initially using it
   * to do some timestamp testing.
   */

  "Timestamps" should "parse to seconds" in {
    val time = controller.getAllCombatInstances()(0).startTimeStamp
//    println("localtime: " +localTime)
    assert(time == LocalTime.parse("20:59:40.780"))

    // prove out how converting to seconds works
    assert(time.toSecondOfDay - LocalTime.parse("20:58:40.780").toSecondOfDay == 60)
    val timeFromStartOfDay = time.toSecondOfDay
    val dayFromEpoch =  LocalDate.now().atStartOfDay().toEpochSecond(java.time.ZoneOffset.UTC)
    val instantFromEpoch = timeFromStartOfDay + dayFromEpoch
    val instant : java.time.Instant = java.time.Instant.ofEpochSecond(instantFromEpoch)
    println(s"Time from start of day ${timeFromStartOfDay}")
    println(s"Time from Epoch ${dayFromEpoch}")
    println(s"Instant: ${instant}")

    //val asInstant = java.time.Instant.ofEpochSecond()

  }

  //"G:\Program Files\Java\jdk-16.0.2\bin\java.exe" -server -Xmx1536M -Dsbt.supershell=false -Didea.managed=true -Dfile.encoding=UTF-8 "-Didea.installation.dir=C:\Program Files\JetBrains\IntelliJ IDEA 2021.1.1" -Dsbt.log.noformat=true -jar C:\Users\Peyton\AppData\Roaming\JetBrains\IntelliJIdea2021.1\plugins\Scala\launcher\sbt-launch.jar early(addPluginSbtFile=\"\"\"C:\Users\Peyton\AppData\Local\Temp\idea1.sbt\"\"\") "; set ideaPort in Global := 64526 ; idea-shell"




}

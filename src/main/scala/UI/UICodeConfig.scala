package UI

import com.typesafe.config.ConfigFactory
import eu.hansolo.tilesfx.Tile
import javafx.scene.paint.Color


/**
 * This class may or may not end up being removed. Right now its just to abstract this
 * color stuff out of the ElementLoader. We can set other code - based configs here maybe.
 *
 * Additionally, we want to use MJPs java preferences so users can set colors anyways, so...
 */
object UICodeConfig {

  val config = ConfigFactory.load()
  val random = scala.util.Random

  var logPath = config.getString("Paths.combatLogDir")


  def randomColor(): javafx.scene.paint.Color = {
    val select = random.nextInt(5)

    select match {
      case 0 => Tile.LIGHT_GREEN
      case 1 => Tile.ORANGE
      case 2 => Tile.BLUE
      case 3 => Tile.LIGHT_RED
      case 4 => Tile.GRAY
      case _ => println(s"Random Color Error: ${select} is out of range"); Tile.GREEN
    }

  }

  def colorLoader(color: String): javafx.scene.paint.Color = {
    color match {
      case "LIGHT_GREEN" => Tile.LIGHT_GREEN
      case "ORANGE" => Tile.ORANGE
      case "BLUE" => Tile.BLUE
      case "LIGHT_RED" => Tile.LIGHT_RED
      case "GRAY" => Tile.GRAY
      case _ => println(s"Color Not Yet Added to colorLoader: ${color}"); Tile.GREEN
    }

  }

  // Colors by type from config
  val internalColor: Color = if (config.hasPath("UI.Colors.DamageTypes.internal")){
    colorLoader(config.getString("UI.Colors.DamageTypes.internal"))
  } else {colorLoader("")}
  val kineticColor: Color = if (config.hasPath("UI.Colors.DamageTypes.kinetic")){
    colorLoader(config.getString("UI.Colors.DamageTypes.kinetic"))
  } else {colorLoader("")}
  val energyColor: Color = if (config.hasPath("UI.Colors.DamageTypes.energy")){
    colorLoader(config.getString("UI.Colors.DamageTypes.energy"))
  } else {colorLoader("")}
  val elementalColor: Color = if (config.hasPath("UI.Colors.DamageTypes.elemental")){
    colorLoader(config.getString("UI.Colors.DamageTypes.elemental"))
  } else {colorLoader("")}
  val regularColor: Color = if (config.hasPath("UI.Colors.DamageTypes.regular")){
    colorLoader(config.getString("UI.Colors.DamageTypes.regular"))
  } else {colorLoader("")}

}

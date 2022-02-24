package UI.objects

import scalafx.scene.control.{Menu, MenuItem}

object Menus {

  val combatInstanceMenu = new Menu("Combat Instances")

  //Make all the menus
  val menu1 = new Menu("File")
  menu1.items = List(new MenuItem("Choose Log Directory..."), new MenuItem("Open Recent Log Directory..."))
  val menu2 = new Menu("Options")
  val menu3 = new Menu("View")
  val menu4 = new Menu("Help")

  val fileMenu = new Menu("Log Files")


}

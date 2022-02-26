package UI.objects

import UI.{ElementLoader, UICodeConfig}
import Utils.{FileHelper, PathLoader}
import scalafx.scene.control.{Menu, MenuItem}

import scalafx.Includes._

object Menus {

  val combatInstanceMenu = new Menu("Combat Instances")

  val recentDirMenu = new Menu("Open Recent Log Directory...")
  loadRecentDirMenu()


  // recent dir menus
  var fileMenuItems: List[MenuItem] = List(new MenuItem("Choose Log Directory..."))
  fileMenuItems = fileMenuItems :+ recentDirMenu

  //Make all the menus
  val menu1 = new Menu("File")
  menu1.items = fileMenuItems
//    List(
//    new MenuItem("Choose Log Directory..."),
//    new MenuItem("Open Recent Log Directory..."),
//  )
  val menu2 = new Menu("Options")
  val menu3 = new Menu("View")
  val menu4 = new Menu("Help")

  val fileMenu = new Menu("Log Files")


  def loadRecentDirMenu(): Unit = {
    recentDirMenu.items = for (path <- PathLoader.getPaths()) yield {
      val item: MenuItem = new MenuItem(path)
      item.setOnAction(ElementLoader.loadNewDirectoryActionEvent(path))
      item
    }
  }


}

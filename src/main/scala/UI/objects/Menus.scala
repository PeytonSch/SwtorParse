package UI.objects

import UI.{ElementLoader, UICodeConfig}
import Utils.{FileHelper, PathLoader}
import javafx.event.{ActionEvent, EventHandler}
import scalafx.scene.control.{Menu, MenuBar, MenuItem}
import scalafx.Includes._

object Menus {

  import scalafx.Includes._

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

//  fileMenu.setOnAction(new EventHandler[ActionEvent] {
//    override def handle(event: ActionEvent): Unit = {
//      println("Clicked Menu")
//      //ElementLoader.loadLogFileMenu()
//    }
//  })


  def loadRecentDirMenu(): Unit = {
    recentDirMenu.items = for (path <- PathLoader.getPaths()) yield {
      val item: MenuItem = new MenuItem(path)
      item.setOnAction(ElementLoader.loadNewDirectoryActionEvent(path))
      item
    }
  }



  //Create blank menubar
  val mainMenuBar = new MenuBar()

  //add the menus to the menubar
  mainMenuBar.getMenus().addAll(menu1, menu2, menu3, menu4, fileMenu, combatInstanceMenu)


}

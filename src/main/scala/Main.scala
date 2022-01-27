import Controller.Controller
import UI.{ElementLoader, GuiTiles}
import com.typesafe.config.ConfigFactory
import eu.hansolo.tilesfx.Tile
import parser.Parser
import scalafx.animation.AnimationTimer
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.geometry.Insets
import scalafx.scene.{Parent, PerspectiveCamera, Scene}
import scalafx.scene.control.{Button, CheckBox, Label, Menu, MenuBar, MenuItem, ScrollPane, Tab, TabPane}
import scalafx.scene.layout.{Background, BackgroundFill, CornerRadii, GridPane, VBox}
import scalafx.scene.paint._
import scalafx.stage.{DirectoryChooser, FileChooser}
import scalafx.event.ActionEvent
import scalafx.Includes._

import java.io.File
import eu.hansolo.tilesfx.chart.ChartData
import eu.hansolo.tilesfx.skins.LeaderBoardItem
import eu.hansolo.tilesfx.tools.TreeNode
import javafx.event.{Event, EventHandler}
import parsing.Actors.Player
import scalafx.scene.control.ScrollPane.ScrollBarPolicy

import java.util.prefs.{Preferences, PreferencesFactory}
import scala.collection.mutable
import scala.collection.mutable.ListBuffer


/**
 * ScalaFX applications can extend JFXApp3 to create properly initialized JavaFX applications.
 * On the back end JFXApp3 first calls javafx.application.Application.launch
 * then executes body of its constructor when javafx.application.Application.start(primaryStage:Stage) is called
 */
object Main extends JFXApp3 {


  /**
   * This start() Method is essentially this start of our application, you can think of this as the main function
   */
  override def start(): Unit = {

    val controller = new Controller()
    val elementLoader = new ElementLoader()


    //Initialize Java Preferences object
    val prefs: Preferences = Preferences.userNodeForPackage(this.getClass())

    //Example code for working with Java Preferences API (assuming prefs is the instance of the Preference class)
    //Set a preference value: prefs.put("key", "value")
    //Get a preference value: prefs.get("key", "default value")
    //Print all the valid keys in this node: prefs.keys().foreach(println)
    //Remove a key: prefs.remove("key")
    //Force changes to be updated in the preferences storage: prefs.flush()
    //MJP

    //Example code for getting directory from preferences instead. ("./SampleLogs") is a default value if key: "PARSE_LOG_DIR" is not found
    //val files = FileHelper.getListOfFiles(prefs.get("PARSE_LOG_DIR", "./SampleLogs"))

    val files = FileHelper.getListOfFiles("./SampleLogs")

    // Tiles is all of the tiles in the UI. Contained and managed in a UI.GuiTiles class
    val tiles : GuiTiles = new GuiTiles()

    // Set the last Timer Call to the current system time. This is a var so it can be updated. It controls the UI
    // Refresh rate, checking the time against the last time and the execution rate.
    var lastTimerCall = System.nanoTime()
    val program_execution_rate : Long = 200_000_000L


    // This parser class is used to pass logs. This is more in here as a test and not fully implemented.
    val parser : Parser = new Parser

    // Init the controller
    controller.parseLatest(parser.getNewLines())




    /** Everything in here is ran on the timer interval */
    val timer : AnimationTimer = AnimationTimer(t => {
      val now = System.nanoTime()
      if (now > lastTimerCall + program_execution_rate) {
        lastTimerCall = now

        /**
         * This returns all lines from the log that are new this tick.
         * It returns them as instances of LogInformation
         * */
        val result = parser.getNewLines()

        // if there are new lines to parse
        if (result.size != 0) controller.parseLatest(result)

        elementLoader.performTickUpdateLiveParsing(controller, tiles)

      }
    })

    val tabbedPane = new TabPane()
    tabbedPane.setId("tabbedPane")
    val parentPane = new VBox()
    parentPane.setId("parentVbox")
    val overViewTab = new Tab
    overViewTab.setClosable(false)
    overViewTab.setText("Overview")
    val dpsTab = new Tab
    dpsTab.setClosable(false)
    dpsTab.setText("DAMAGE DONE")
    val hpsTab = new Tab
    hpsTab.setClosable(false)
    hpsTab.setText("HEALING DONE")
    val dtpsTab = new Tab
    dtpsTab.setClosable(false)
    dtpsTab.setText("DAMAGE TAKEN")
    val htpsTab = new Tab
    htpsTab.setClosable(false)
    htpsTab.setText("HEALING TAKEN")
    tabbedPane.tabs = List(overViewTab,dpsTab,hpsTab,dtpsTab,htpsTab)


    // A stage is like the window we create for the GUI
    val stage = new PrimaryStage()

    // This can be though of as like a layout
    val pane = new GridPane()

    // These variables are to make adjusting the grid easier
    val mainMenuRow = 0
    val menuRow = mainMenuRow + 1
    val mainRow1 = menuRow + 1
    val mainRowSpan = 2
    val mainRow2 = mainRow1 + mainRowSpan

    // Main Menu Bar

    //Make all the menus
    val menu1 = new Menu("File")
    menu1.items = List(new MenuItem("Choose Log Directory..."), new MenuItem("Open Recent Log Directory..."))
    val menu2 = new Menu("Options")
    val menu3 = new Menu("View")
    val menu4 = new Menu("Help")

    // File select
    var fileBuffer = new ListBuffer[MenuItem]()
    for (i <- 0 until files.length){
      fileBuffer += new MenuItem(files(i).getAbsolutePath().split('\\').last)
    }

    val fileMenu = new Menu("Log Files")
    fileMenu.items = fileBuffer.toList

    val combatInstanceMenu = new Menu("Combat Instances")
    var combatInstanceBuffer = new ListBuffer[MenuItem]()
    for (combatInstance <- controller.getAllCombatInstances()){
//      println(s"Got combat instance: ${combatInstance}")
      var item = new MenuItem(combatInstance.getNameFromActors)
      item.setOnAction(elementLoader.combatInstanceChangeMenuAction(controller, tiles))
      combatInstanceBuffer += item
    }
    combatInstanceMenu.items = combatInstanceBuffer.toList




    //Create blank menubar
    val mainMenuBar = new MenuBar()

    //add the menus to the menubar
    mainMenuBar.getMenus().addAll(menu1, menu2, menu3, menu4, fileMenu, combatInstanceMenu)

    //add the menubar to the pane
    //pane.add(mainMenuBar, 0, mainMenuRow, 10, 1)

    //style of the menu bar and menus
    //mainMenuBar.setBackground(background)
    //menu1.setStyle("-fx-font-size: 2.4em; -fx-background-color: #2a2a2a; -fx-text-fill: white")
    //menu2.setStyle("-fx-font-size: 2.4em; -fx-background-color: #2a2a2a; -fx-text-fill: white")
    //menu3.setStyle("-fx-font-size: 2.4em; -fx-background-color: #2a2a2a; -fx-text-fill: white")
    //menu4.setStyle("-fx-font-size: 2.4em; -fx-background-color: #2a2a2a; -fx-text-fill: white")

    //create a DirectoryChooser object
    val directoryChooser = new DirectoryChooser {
      title = "Open Resource File"
    }

    //open file explorer when MenuItem("Choose Log File...") is clicked
    //Not exactly sure how we want to handle userData at the moment, just printing out the chosen directory for now
    menu1.items(0).onActionProperty() = (e: ActionEvent) => {

      val selectedDirectory: File = directoryChooser.showDialog(stage)


      if (selectedDirectory != null) {
          val dirPath = selectedDirectory.getAbsolutePath()
          prefs.put("PARSE_LOG_DIR", dirPath)
//          println(s"Added the selected directory: \"$dirPath\"" + " to your user preferences.")
      }
    }

    //End of Main Menu Bar code

    // The Interface Pane handles some checkboxes and stuff for quickly accessed items. This will probably be remade later
//    val interfacePane = new GridPane()
//    interfacePane.setBackground(tiles.background)
//    val checkBox1 = new CheckBox("Raid DPS    ")
//    val checkBox2 = new CheckBox("Raid HPS    ")
//    val checkBox3 = new CheckBox("Raid Threat ")
//    val checkBox4 = new CheckBox("Raid Timers ")
//    val checkBox5 = new CheckBox("Raid Challenges")
//
//    checkBox1.setStyle("-fx-font-size: 2.4em; -fx-background-color: #2a2a2a; -fx-text-fill: white")
//    checkBox2.setStyle("-fx-font-size: 2.4em; -fx-background-color: #2a2a2a; -fx-text-fill: white")
//    checkBox3.setStyle("-fx-font-size: 2.4em; -fx-background-color: #2a2a2a; -fx-text-fill: white")
//    checkBox4.setStyle("-fx-font-size: 2.4em; -fx-background-color: #2a2a2a; -fx-text-fill: white")
//    checkBox5.setStyle("-fx-font-size: 2.4em; -fx-background-color: #2a2a2a; -fx-text-fill: white")
//
//    interfacePane.add(checkBox1, 0, 0)
//    interfacePane.add(checkBox2, 0, 1)
//    interfacePane.add(checkBox3, 1, 0)
//    interfacePane.add(checkBox4, 1, 1)
//    interfacePane.add(checkBox5, 0, 2, 2, 1)
//
//
//    pane.add(interfacePane, 1, menuRow, 1, 1)


    /**
     * Here is where we add all the main tiles from the tiles manager class
     * */

    pane.add(tiles.statusTile, 2, menuRow, 5, 1)

    //Main Row 1
    pane.add(tiles.leaderBoardTile, 7, mainRow1, 1, mainRowSpan)
    pane.add(tiles.stackedArea, 1, mainRow1, 5, mainRowSpan)
    pane.add(tiles.radarChartTile2, 6, mainRow1, 1, mainRowSpan)


    pane.add(tiles.personalStatsScrollPane, 0, menuRow, 1, mainRowSpan + 1)
    //pane.add(tiles.personalStatsBarChart, 7, mainRow1, 1, mainRowSpan + 1)

//    //Main Row 2
    pane.add(tiles.damageDoneSourceTile, 0, mainRow2, 3, 1)
    pane.add(tiles.damageTakenSourceTile, 3, mainRow2, 3, 1)
    pane.add(tiles.damageFromTypeIndicator, 6, mainRow2, 1, 1)

//    dpsTab.onSelectionChanged = (v:Event) => {
//      dpsTab.setContent(tiles.stackedArea)
//    }
//    overViewTab.onSelectionChanged = (v:Event) => {
//      overViewTab.setContent(pane)
//    }

    pane.setHgap(5)
    pane.setVgap(5)

    // Set the preferred size of the window
    pane.setPrefSize(1500, 800)
    pane.setBackground(tiles.background)
    tiles.stackedArea.setBackground(tiles.background)

    val camera = new PerspectiveCamera()
    camera.setFieldOfView(10)

    //val root : javafx.scene.Parent = FXMLLoader.load(getClass().getResource("/Application.fxml"))

    // add the pane to a scene and give it a camera
    parentPane.children = List(mainMenuBar,tabbedPane)
    overViewTab.content = pane
    val scene = new Scene(parentPane)
    scene.getStylesheets().add("Chart.css")

    scene.setCamera(camera)

    // This is the title of the window
    stage.setTitle("ELITE RAIDING PARSER")
    stage.setScene(scene)
    stage.show()

    // Start the timer to run the timer things, or the main program loop
    timer.start()
    System.out.println("Timer Started")

  }

  /**
   * This method is called when you close the program.
   */
  override def stopApp(): Unit = {
    println("Stopping App")
    //timer.stop()
  }

}
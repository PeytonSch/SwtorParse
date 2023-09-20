import Controller.Controller
import UI.objects.ProgressBar.progressBar
import UI.overlays.BasicTimers
import UI.{ElementLoader, Tiles, UICodeConfig}
import com.typesafe.config.ConfigFactory
import eu.hansolo.tilesfx.Tile
import scalafx.animation.AnimationTimer
import scalafx.application.{JFXApp3, Platform}
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.geometry.Insets
import scalafx.scene.{Parent, PerspectiveCamera, Scene}
import scalafx.scene.control.{Button, CheckBox, Label, Menu, MenuBar, MenuItem, ScrollPane, Tab, TabPane}
import scalafx.scene.layout.{Background, BackgroundFill, CornerRadii, GridPane, Priority, VBox}
import scalafx.scene.paint._
import scalafx.stage.{DirectoryChooser, FileChooser, Stage}
import scalafx.event.ActionEvent
import scalafx.Includes._
import java.io.File

import eu.hansolo.tilesfx.chart.ChartData
import eu.hansolo.tilesfx.skins.LeaderBoardItem
import eu.hansolo.tilesfx.tools.TreeNode
import javafx.event.{Event, EventHandler}
import logger.Logger
import logger.LogLevel._
import parsing.Actors.Player
import scalafx.scene.control.ScrollPane.ScrollBarPolicy
import java.util.prefs.{Preferences, PreferencesFactory}

import UI.MainStage.mainStage
import UI.MenuBar.CustomMenuBar
import UI.tabs.{CustomTabs, Settings}
import Utils.Config.settings
import Utils.{Config, FileHelper}
import parser.Parser



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

    val files: List[File] = if(Config.config.getString("RunMode.mode") == ("Staging")){
      Logger.info("Running in Staging mode")
      FileHelper.getListOfFiles(UICodeConfig.logPath)
    } else {
      Logger.info("Running in developer mode")
      FileHelper.getListOfFiles("./SampleLogs")
    }

    // Set the last Timer Call to the current system time. This is a var so it can be updated. It controls the UI
    // Refresh rate, checking the time against the last time and the execution rate.
    var lastTimerCall = System.nanoTime()
    val program_execution_rate : Long = Config.config.getLong("UI.General.tickRate")
    val timerRate : Long = Config.config.getLong("UI.General.timerRate")


    // This Parser class is used to pass logs. This is more in here as a test and not fully implemented.
    // TODO: This needs to be initialized after the UI starts without breaking the UI
    // Init the Controller
//    Controller.parseLatest(Parser.getNewLines())


    // A stage is like the window we create for the GUI
    mainStage = new PrimaryStage()
//    val dpsOverlay = new PrimaryStage()

    val parentPane = new VBox()
    parentPane.fillWidth = true
    parentPane.setId("parentVbox")


    val camera = new PerspectiveCamera()
    camera.setFieldOfView(10)

    val scene = new Scene(parentPane)
    scene.getStylesheets().add("Chart.css")

    scene.setCamera(camera)

    // This is the title of the window
    mainStage.setTitle("ELITE RAIDING PARSER")
    mainStage.setScene(scene)



    // Main Menu Bar

    //Make all the menus
//    val menu1 = new Menu("File")
//    menu1.items = List(new MenuItem("Choose Log Directory..."), new MenuItem("Open Recent Log Directory..."))
//    val menu2 = new Menu("Options")
//    val menu3 = new Menu("View")
//    val menu4 = new Menu("Help")


    /**
     * Select combat instance
     */
//    val combatInstanceMenu = new Menu("Combat Instances")
    // move to Async Loading
//    ElementLoader.loadCombatInstanceMenu(tiles, combatInstanceMenu)


    /**
     * Select Combat File
     */
    ElementLoader.loadLogFileMenu()


    //add the menubar to the pane
    //pane.add(mainMenuBar, 0, mainMenuRow, 10, 1)

    //style of the menu bar and menus
    //mainMenuBar.setBackground(background)
    //menu1.setStyle("-fx-font-size: 2.4em; -fx-background-color: #2a2a2a; -fx-text-fill: white")
    //menu2.setStyle("-fx-font-size: 2.4em; -fx-background-color: #2a2a2a; -fx-text-fill: white")
    //menu3.setStyle("-fx-font-size: 2.4em; -fx-background-color: #2a2a2a; -fx-text-fill: white")
    //menu4.setStyle("-fx-font-size: 2.4em; -fx-background-color: #2a2a2a; -fx-text-fill: white")

//    //create a DirectoryChooser object
//    val directoryChooser = new DirectoryChooser {
//      title = "Open Resource File"
//    }
//
//    //open file explorer when MenuItem("Choose Log File...") is clicked
//    //Not exactly sure how we want to handle userData at the moment, just printing out the chosen directory for now
//    menu1.items(0).onActionProperty() = (e: ActionEvent) => {
//      val selectedDirectory: File = directoryChooser.showDialog(stage)
//      if (selectedDirectory != null) {
//          val dirPath = selectedDirectory.getAbsolutePath()
//          ElementLoader.loadNewDirectory(dirPath)
//
//      }
//    }

    //End of Main Menu Bar code

    // The Interface Pane handles some checkboxes and stuff for quickly accessed items. This will probably be remade later
//    val interfacePane = new GridPane()
//    interfacePane.setBackground(Tiles.background)
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

    //val root : javafx.scene.Parent = FXMLLoader.load(getClass().getResource("/Application.fxml"))

    // add the pane to a scene and give it a camera
    parentPane.children = List(CustomMenuBar.addToUI,CustomTabs.addToUI)


    mainStage.show()

    /**
     * Timer Code
     */
    var lastTimerOverlayTimerCall = System.nanoTime()
    /** Everything in here is ran on the timer interval */
    val timer : AnimationTimer = AnimationTimer(t => {
      val now = System.nanoTime()

      // TODO: This may need to be removed, it is originally made for testing timers
      // we may want to keep this so we can make timers smoother though
      if (settings.getBoolean("basicTimerOverlayEnabled",false) && now > lastTimerOverlayTimerCall + timerRate && UICodeConfig.logFile != "") {
        lastTimerOverlayTimerCall = now
        BasicTimers.refresh()
      }

      if (now > lastTimerCall + program_execution_rate && UICodeConfig.logFile != "") {
        lastTimerCall = now

        /**
         * This returns all lines from the log that are new this tick.
         * It returns them as instances of LogInformation
         * */
        val result = Parser.getNewLines()

//        Logger.highlight(s"We have ${result.size} lines to parse this tick")

        // if there are new lines to parse
        // TODO: This might need to be > 1, I think we get a result of 1 often and we do an update when we dont need to
        if (result.size > 1) {
          Logger.trace(s"Timer Loop: Result size == ${result.length}, performing parseLatest result and live parsing tick update")
          Controller.parseLatest(result)
          Logger.debug("Finished Controller Parsing, performing tick update for element loader")
          ElementLoader.performTickUpdateLiveParsing()
          Logger.debug("Finished element loaded tick update")

        }


      }
    })

    if (Config.config.getBoolean("General.startWithLog") && Config.config.getBoolean("Performance.performanceLoadingEnabled")) {
      Logger.debug("Running Asynchronous Initialization")
      // Run the optimized initialization
      Platform.runLater(ElementLoader.initAsynchronously(timer))

      // Load the remaining Combat Instances in the background
      Platform.runLater(ElementLoader.initRemainingAsynchronously(timer))
    } else if  (Config.config.getBoolean("General.startWithLog")) {
      Logger.debug("Running None - Asynchronous Initialization, start with logging")
      Controller.parseLatest(Parser.getNewLines())
      timer.start()
    }
    // TODO: This is a placeholder, need to keep the timer from parsing and start it later
    else {
      Logger.debug("Running Non - Asynchronous Initialization, start without logging")
      Logger.trace("Timer starting")
      timer.start()
    }



  }

  /**
   * This method is called when you close the program.
   */
  override def stopApp(): Unit = {
    Logger.debug("Stopping App")
//    timer.stop()
  }

}
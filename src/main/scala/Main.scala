import Controller.Controller
import com.typesafe.config.ConfigFactory
import eu.hansolo.tilesfx.Tile
import parser.Parser
import scalafx.animation.AnimationTimer
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.geometry.Insets
import scalafx.scene.{Parent, PerspectiveCamera, Scene}
import scalafx.scene.control.{Button, CheckBox, Menu, MenuBar, MenuItem, Tab, TabPane}
import scalafx.scene.layout.{Background, BackgroundFill, CornerRadii, GridPane, VBox}
import scalafx.scene.paint._
import scalafx.stage.{DirectoryChooser, FileChooser}
import scalafx.event.ActionEvent
import scalafx.Includes._

import java.io.File
import java.nio.file.Paths
import java.nio.file.Files
import java.time.Instant
import eu.hansolo.tilesfx.chart.ChartData
import eu.hansolo.tilesfx.skins.LeaderBoardItem
import eu.hansolo.tilesfx.tools.TreeNode
import javafx.event.{Event, EventHandler}
import javafx.fxml.FXMLLoader
import parsing.Actors.Player
import parsing.Result.ApplyEffect
import patterns.Actions.SafeLogin
import patterns.LogInformation
import patterns.Result.{EnterCombat, ExitCombat}
import scalafx.scene.input.MouseEvent

import scala.collection.IterableOnce.iterableOnceExtensionMethods
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

    val config = ConfigFactory.load()

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

    // Tiles is all of the tiles in the UI. Contained and managed in a GuiTiles class
    val tiles : GuiTiles = new GuiTiles()

    // Set the last Timer Call to the current system time. This is a var so it can be updated. It controls the UI
    // Refresh rate, checking the time against the last time and the execution rate.
    var lastTimerCall = System.nanoTime()
    val program_execution_rate : Long = 200_000_000L

    // This can be used to generate random numbers
    val random = scala.util.Random


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





        /**
         * All this code is essentially updating the UI with Mock Data
         * */

        /** Timer Ran Code */
        if (tiles.statusTile.getLeftValue() > 1000) { tiles.statusTile.setLeftValue(0); }
        if (tiles.statusTile.getMiddleValue() > 1000) { tiles.statusTile.setMiddleValue(0); }
        if (tiles.statusTile.getRightValue() > 1000) { tiles.statusTile.setRightValue(0); }
        tiles.statusTile.setLeftValue(tiles.statusTile.getLeftValue() + random.nextInt(4))
        tiles.statusTile.setMiddleValue(tiles.statusTile.getMiddleValue() + random.nextInt(3))
        tiles.statusTile.setRightValue(tiles.statusTile.getRightValue() + random.nextInt(3))

//        tiles.leaderBoardTile.getLeaderBoardItems().get(random.nextInt(3)).setValue(random.nextDouble() * 80)
        //tiles.timelineTile.addChartData(new ChartData("", random.nextDouble() * 300 + 50, Instant.now()));
        //tiles.timelineTile.calcAutoScale()
        // if the current combat is not null, set to show player damage
        if (controller.getCurrentCombat() != null) {
          // TODO: This needs to have a static graph if the combat is complete
          //tiles.timelineTile.addChartData(new ChartData(controller.getCurrentPlayerDamage(),java.time.Instant.now()))
        }
        //tiles.timelineTile.setMaxTimePeriod(java.time.Duration.ofSeconds(900))

        /** Radar Percentiles Chart */
        tiles.chartData1.setValue(random.nextDouble() * 50)
        tiles.chartData2.setValue(random.nextDouble() * 50)
        tiles.chartData3.setValue(random.nextDouble() * 50)
        tiles.chartData4.setValue(random.nextDouble() * 50)
        tiles.chartData5.setValue(random.nextDouble() * 50)
        tiles.chartData6.setValue(random.nextDouble() * 50)
        tiles.chartData7.setValue(random.nextDouble() * 50)
        tiles.chartData8.setValue(random.nextDouble() * 50)

        /** Right side bar chart for personal stats*/
        //tiles.barChartTile.getBarChartItems().get(random.nextInt(8)).setValue(random.nextDouble() * 800);
        //tiles.barChartTile.getBarChartItems().get(2).setValue(5000)


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

    // Things should be in dark-mode always
    val backgroundFill = new BackgroundFill(Color.web("#2a2a2a"), CornerRadii.Empty, Insets.Empty)
    val backgroundFillArray = Array(backgroundFill)
    val background = new Background(backgroundFillArray)

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

    // TODO: Just about everything in here probably needs to be extracted out so that it can run in the main loop
    val menuAction = (event: ActionEvent) => {
      //println(s"You clicked ${event.getTarget.asInstanceOf[javafx.scene.control.MenuItem].getText}")

      // set the current combat instance
      controller
        .setCurrentCombatInstance(controller.
          getCombatInstanceById(event.getTarget.asInstanceOf[javafx.scene.control.MenuItem].getText))

      /**
       * Main DPS Chart
       */

      // clear out and add all of the combat instance data to the chart
      val damageTimeSeries =  controller.getCurrentCombat().getPlayerInCombatActor().getDamageDoneTimeSeries()
      val damagePerSecondTimeSeries = controller.getCurrentCombat().getPlayerInCombatActor().getDamagePerSecondTimeSeries()
//      println(s"Current combat has a saved time series of ${damageTimeSeries.size} elements")
      tiles.lineChartSeries.getData.removeAll()
      tiles.barChartSeries.getData.removeAll()
      tiles.barChartSeries.data = damageTimeSeries.toSeq.map(x => (x._1.toString(),x._2)).map(tiles.toCatagoryChartData)
      tiles.lineChartSeries.data = damagePerSecondTimeSeries.toSeq.map(x => (x._1.toString(),x._2)).map(tiles.toCatagoryChartData)
//      println(s"Got max value of ${damageTimeSeries.valuesIterator.max}")
      tiles.yAxis.setUpperBound(damageTimeSeries.valuesIterator.max)

      /**
       * Helper function to get inner ring to add ability damage to.
       * This is used for both damage taken from source and
       * damage done from source ability.
       * @param name
       * @return
       */
      def getCorrectChild(name : String, from : String): TreeNode[ChartData] = {
        if (from == "dtps") {
          val root : java.util.List[TreeNode[ChartData]] = tiles.dtpstree.getAll
          for (i <- 0 until root.size()){
            if(root.get(i).getItem.getName == name) return root.get(i)
          }
        }
        else {
          val root : java.util.List[TreeNode[ChartData]] = tiles.damageDoneTree.getAll
          for (i <- 0 until root.size()){
            if(root.get(i).getItem.getName == name) return root.get(i)
          }
        }
        // this is a backup, probably shouldn't happen
        println("Error, returning root tree, this should not happen")
        tiles.dtpstree
      }

      /**
       * * * * * * Damage Done Section * * * * *
       *
       * Update the base types for the damage taken pie chart
       */

      /** TODO: This and the damage taken section have a lot of repeat code, can probably factor some of this
       * out into functions. Both here and in the CombatActorInstance functions
       */


      // remove the all old data for both tiles
      tiles.damageDoneTree.removeAllNodes()

      // Colors by type from config
      val internalColor: Color = if (config.hasPath("UI.Colors.DamageTypes.internal")){
        tiles.colorLoader(config.getString("UI.Colors.DamageTypes.internal"))
      } else {tiles.colorLoader("")}
      val kineticColor: Color = if (config.hasPath("UI.Colors.DamageTypes.kinetic")){
        tiles.colorLoader(config.getString("UI.Colors.DamageTypes.kinetic"))
      } else {tiles.colorLoader("")}
      val energyColor: Color = if (config.hasPath("UI.Colors.DamageTypes.energy")){
        tiles.colorLoader(config.getString("UI.Colors.DamageTypes.energy"))
      } else {tiles.colorLoader("")}
      val elementalColor: Color = if (config.hasPath("UI.Colors.DamageTypes.elemental")){
        tiles.colorLoader(config.getString("UI.Colors.DamageTypes.elemental"))
      } else {tiles.colorLoader("")}
      val regularColor: Color = if (config.hasPath("UI.Colors.DamageTypes.regular")){
        tiles.colorLoader(config.getString("UI.Colors.DamageTypes.regular"))
      } else {tiles.colorLoader("")}

      for (types <- controller.getCurrentCombat().getPlayerInCombatActor().getDamageTypeDone()) {
        // TODO: Need to make sure you have ALL the damage types here or they wont show
        types._1 match {
          case "internal" => {
            new TreeNode(new ChartData("Internal", types._2, internalColor), tiles.damageDoneTree);
          }
          case "kinetic" => {
            new TreeNode(new ChartData("Kinetic", types._2, kineticColor), tiles.damageDoneTree);
          }
          case "energy" => {
            new TreeNode(new ChartData("Energy", types._2, energyColor), tiles.damageDoneTree);
          }
          case "elemental" => {
            new TreeNode(new ChartData("Elemental", types._2, elementalColor), tiles.damageDoneTree);
          }
          case "No Type" =>
          case x => {
            println(s"Got Unknown Damage type: ${x}")
            new TreeNode(new ChartData("Regular", types._2, regularColor), tiles.damageDoneTree);
          }
        }
      }

      /**
       * Update the damage done from source tile ability data
       */

      for (types <- controller.getCurrentCombat().getPlayerInCombatActor().getDamageDoneStats()) {
        types._1 match {
          case "internal" => {
            for (ability <- types._2) {
              new TreeNode(new ChartData(ability._1, ability._2, internalColor), getCorrectChild("Internal","dps"));
            }
          }
          case "kinetic" => {
            for (ability <- types._2) {
              new TreeNode(new ChartData(ability._1, ability._2, kineticColor), getCorrectChild("Kinetic","dps"));
            }
          }
          case "energy" => {
            for (ability <- types._2) {
              new TreeNode(new ChartData(ability._1, ability._2, energyColor), getCorrectChild("Energy","dps"));
            }
          }
          case "elemental" => {
            for (ability <- types._2) {
              new TreeNode(new ChartData(ability._1, ability._2, elementalColor), getCorrectChild("Elemental","dps"));
            }
          }
          case "No Type" =>
          case x => {
            for (ability <- types._2) {
              new TreeNode(new ChartData(ability._1, ability._2, regularColor), getCorrectChild("Regular","dps"));
            }
          }

        }
      }


      /**
       * * * * * * Damage Taken Section * * * * *
       *
       * Update the damage types indicator as well as the base types for the damage taken pie chart
       */

      // remove the all old data for both tiles
      tiles.dtpstree.removeAllNodes()
      tiles.damageFromTypeIndicator.clearChartData()

      for (types <- controller.getCurrentCombat().getPlayerInCombatActor().getDamageTypeTaken()) {
        // TODO: Need to make sure you have ALL the damage types here or they wont show
        types._1 match {
          case "internal" => {
            tiles.damageFromTypeIndicator.addChartData(new ChartData("Internal",types._2,internalColor))
            new TreeNode(new ChartData("Internal", types._2, internalColor), tiles.dtpstree);
          }
          case "kinetic" => {
            tiles.damageFromTypeIndicator.addChartData(new ChartData("Kinetic",types._2,kineticColor))
            new TreeNode(new ChartData("Kinetic", types._2, kineticColor), tiles.dtpstree);
          }
          case "energy" => {
            tiles.damageFromTypeIndicator.addChartData(new ChartData("Energy",types._2,energyColor))
            new TreeNode(new ChartData("Energy", types._2, energyColor), tiles.dtpstree);
          }
          case "elemental" => {
            tiles.damageFromTypeIndicator.addChartData(new ChartData("Elemental",types._2,elementalColor))
            new TreeNode(new ChartData("Elemental", types._2, elementalColor), tiles.dtpstree);
          }
          case "No Type" =>
          case x => {
            println(s"Got Unknown Damage type: ${x}")
            tiles.damageFromTypeIndicator.addChartData(new ChartData("Regular",types._2,regularColor))
            new TreeNode(new ChartData("Regular", types._2, regularColor), tiles.dtpstree);
          }
        }
      }

      /**
       * Update the damage taken from source tile ability data
       */

      for (types <- controller.getCurrentCombat().getPlayerInCombatActor().getDamageTakenStats()) {
        types._1 match {
          case "internal" => {
            for (ability <- types._2) {
              new TreeNode(new ChartData(ability._1, ability._2, internalColor), getCorrectChild("Internal","dtps"));
            }
          }
          case "kinetic" => {
            for (ability <- types._2) {
              new TreeNode(new ChartData(ability._1, ability._2, kineticColor), getCorrectChild("Kinetic","dtps"));
            }
          }
          case "energy" => {
            for (ability <- types._2) {
              new TreeNode(new ChartData(ability._1, ability._2, energyColor), getCorrectChild("Energy","dtps"));
            }
          }
          case "elemental" => {
            for (ability <- types._2) {
              new TreeNode(new ChartData(ability._1, ability._2, elementalColor), getCorrectChild("Elemental","dtps"));
            }
          }
          case "No Type" =>
          case x => {
            for (ability <- types._2) {
              new TreeNode(new ChartData(ability._1, ability._2, regularColor), getCorrectChild("Regular","dtps"));
            }
          }

        }
      }

      /**
       * Update the leaderboard tile with all player damage
       *
       *  we do not clear leaderboard items, and create new items, instead we iterate through them setting new values
       * there are 24 by default, for the max swtor group size. Only set the number visible that
       * correspond to the number of players in this combat. We have to do it this way because of something
       * with how the leaderboard tile works
       */

      for (index <- 0 until tiles.leaderBoardItems.size()){
        tiles.leaderBoardItems.get(index).setValue(0)
        tiles.leaderBoardItems.get(index).setName("")
        tiles.leaderBoardItems.get(index).setVisible(false)
      }

      // get all the combat Actors
      val combatActors = controller.getCurrentCombat().getCombatActors()
      val players = (for (actor <- combatActors) yield actor.getActor()).filter(_.isInstanceOf[Player])
      var lastUpdatedIndex = 0
      for (index <- 0 until players.length){
        // need to relate the player actor instance to the combat actor instance
        // then we need to get the damage done and order them
        val combatInstanceActor = controller.getCurrentCombat().getCombatActorByIdString(players(index).getId().toString)
        // TODO: I cannot get this to set the name to save my life, help!
        tiles.leaderBoardItems.get(index).setValue(combatInstanceActor.getDamageDone())
        tiles.leaderBoardItems.get(index).setName(combatInstanceActor.getActor().getName())
        tiles.leaderBoardItems.get(index).getChartData.setName(combatInstanceActor.getActor().getName())
        tiles.leaderBoardItems.get(index).setVisible(true)
      }






    }

    val combatInstanceMenu = new Menu("Combat Instances")
    var combatInstanceBuffer = new ListBuffer[MenuItem]()
    for (combatInstance <- controller.getAllCombatInstances()){
//      println(s"Got combat instance: ${combatInstance}")
      var item = new MenuItem(combatInstance.getNameFromActors)
      item.setOnAction(menuAction)
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
    val interfacePane = new GridPane()
    interfacePane.setBackground(background)
    val checkBox1 = new CheckBox("Raid DPS    ")
    val checkBox2 = new CheckBox("Raid HPS    ")
    val checkBox3 = new CheckBox("Raid Threat ")
    val checkBox4 = new CheckBox("Raid Timers ")
    val checkBox5 = new CheckBox("Raid Challenges")

    checkBox1.setStyle("-fx-font-size: 2.4em; -fx-background-color: #2a2a2a; -fx-text-fill: white")
    checkBox2.setStyle("-fx-font-size: 2.4em; -fx-background-color: #2a2a2a; -fx-text-fill: white")
    checkBox3.setStyle("-fx-font-size: 2.4em; -fx-background-color: #2a2a2a; -fx-text-fill: white")
    checkBox4.setStyle("-fx-font-size: 2.4em; -fx-background-color: #2a2a2a; -fx-text-fill: white")
    checkBox5.setStyle("-fx-font-size: 2.4em; -fx-background-color: #2a2a2a; -fx-text-fill: white")

    interfacePane.add(checkBox1, 0, 0)
    interfacePane.add(checkBox2, 0, 1)
    interfacePane.add(checkBox3, 1, 0)
    interfacePane.add(checkBox4, 1, 1)
    interfacePane.add(checkBox5, 0, 2, 2, 1)


    pane.add(interfacePane, 1, menuRow, 1, 1)


    /**
     * Here is where we add all the main tiles from the tiles manager class
     * */

    pane.add(tiles.statusTile, 3, menuRow, 4, 1)
    pane.add(tiles.switchTile, 7, menuRow, 1, 1)

    //Main Row 1
    pane.add(tiles.leaderBoardTile, 0, mainRow1, 1, mainRowSpan)
    pane.add(tiles.stackedArea, 1, mainRow1, 5, mainRowSpan)
    pane.add(tiles.radarChartTile2, 6, mainRow1, 1, mainRowSpan)
    pane.add(tiles.barChartTile, 7, mainRow1, 1, mainRowSpan + 1)

//    //Main Row 2
    pane.add(tiles.damageDoneSourceTile, 0, mainRow2, 3, 1)
    pane.add(tiles.damageTakenSourceTile, 3, mainRow2, 3, 1)
    pane.add(tiles.damageFromTypeIndicator, 6, mainRow2, 1, 1)

    dpsTab.onSelectionChanged = (v:Event) => {
      dpsTab.setContent(tiles.stackedArea)
    }
    overViewTab.onSelectionChanged = (v:Event) => {
      overViewTab.setContent(pane)
    }

    pane.setHgap(5)
    pane.setVgap(5)

    // Set the preferred size of the window
    pane.setPrefSize(1500, 800)
    pane.setBackground(background)
    tiles.stackedArea.setBackground(background)

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
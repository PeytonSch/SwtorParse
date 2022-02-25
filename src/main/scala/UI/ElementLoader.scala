package UI

import Controller.Controller
import UI.overlays.Overlays
import UI.overlays.Overlays.{groupDamagePane, groupHealingPane}
import com.typesafe.config.ConfigFactory
import eu.hansolo.tilesfx.chart.ChartData
import eu.hansolo.tilesfx.skins.BarChartItem
import eu.hansolo.tilesfx.tools.TreeNode
import parsing.Actors.{Companion, Player}
import scalafx.event.ActionEvent
import javafx.scene.paint.Color
import logger.LogLevel.Info
import logger.Logger
import scalafx.geometry.Pos
import scalafx.scene.control.{Menu, MenuItem}
import scalafx.scene.layout.StackPane
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.Text

import scala.collection.mutable.ListBuffer
import scalafx.Includes._
import scalafx.animation.AnimationTimer
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.application.Platform
import scalafx.scene.Scene
import UI.objects.Menus._
import parser.Parser

import java.io.File

/**
 * Element loader is for loading data into the UI charts and graphs etc.
 * It will be called on refreshes and loading new combats
 */
object ElementLoader {

  val config = ConfigFactory.load()

  // This can be used to generate random numbers
  val random = scala.util.Random

  /**
   * Attempt at defining things to load asyncronously
   */
    def initAsynchronously(timer: AnimationTimer): Unit = {
      // we obviously want to parse asyncronously, this is the big time killer
      Logger.trace("Initializing Lines Async")
      Controller.parseLatest(Parser.getNewLinesInit())
      // we then need to load everything after that depends on the parse results
      loadCombatInstanceMenu()

      // Load the UI to the combat we just loaded
      // set the current combat instance
      Controller
        .setCurrentCombatInstance(Controller.getAllCombatInstances()(0))

      refreshUI()

      //once the UI is set, because we clicked on a past combat instance, set current combat to null
      Controller.endCombat()


      timer.start()
      Logger.info("Timer Started")
    }


  /**
   * Attempt at defining remaining combat instances to load asyncronously
   */
  def initRemainingAsynchronously(timer: AnimationTimer): Unit = {
    // we obviously want to parse asyncronously, this is the big time killer
    Logger.trace("Initializing Lines Async")
    Controller.parseLatest(Parser.parseRemaining())
    // we then need to load everything after that depends on the parse results
    loadCombatInstanceMenu()
    Logger.info("Remaining Combat Instances Loaded")
  }

  /**
   * This is refreshes the combat instances in the combat instance menu
   */
  def loadCombatInstanceMenu(): Unit ={
    var combatInstanceBuffer = new ListBuffer[MenuItem]()
    for (combatInstance <- Controller.getAllCombatInstances()){
      Logger.trace(s"Got combat instance: ${combatInstance}")
      var item = new MenuItem(combatInstance.getNameFromActors)
      item.setOnAction(combatInstanceChangeMenuAction())
      combatInstanceBuffer += item
    }
    combatInstanceMenu.items = combatInstanceBuffer.toList
  }

  def loadLogFileMenu():Unit = {
    val files: List[File] = FileHelper.getListOfFiles(UICodeConfig.logPath)
    var fileBuffer = new ListBuffer[MenuItem]()
    for (i <- 0 until files.length){
      var item = new MenuItem(files(i).getAbsolutePath().split('\\').last)
      item.setOnAction(loadNewCombatFile())
      fileBuffer += item
    }
    fileMenu.items = fileBuffer.toList.reverse

  }

  def clearUI(): Unit = {
    Tiles.overviewLineChartSeries.getData.removeAll()
    Tiles.overviewBarChartSeries.getData.removeAll()
    Tiles.damageTakenLineChartSeries.getData.removeAll()
    Tiles.damageTakenBarChartSeries.getData.removeAll()
    Tiles.damageDoneTree.removeAllNodes()
    Tiles.overviewDtpstree.removeAllNodes()
    Tiles.overviewDamageFromTypeIndicator.clearChartData()
    Tiles.damageTakenDtpstree.removeAllNodes()
    Tiles.damageTakenDamageFromTypeIndicator.clearChartData()

    // Overlays
    Overlays.personalDamageOverlay.clearChartData()
    Overlays.personalHealingOverlay.clearChartData()
    Overlays.personalDamageTakenOverlay.clearChartData()
    Overlays.groupDamagePane.getChildren.clear()
    Overlays.groupHealingPane.getChildren.clear()


    for (index <- 0 until Tiles.leaderBoardItems.size()){
      Tiles.leaderBoardItems.get(index).setValue(0)
      Tiles.leaderBoardItems.get(index).setName("")
      Tiles.leaderBoardItems.get(index).setVisible(false)
    }

    /**
     * Personal Stats
     */
    //DPS
    Tiles.percentileDps.setValue(0)
    Tiles.personalStatsDpsValue.setText("_")
    Tiles.personalStatsTotalDamageValue.setText("_")

    //HPS
    Tiles.percentileHps.setValue(0)
    Tiles.personalStatsHpsValue.setText("_")
    Tiles.personalStatsTotalHealingValue.setText("_")

    //DTPS
    Tiles.percentileDtps.setValue(0)
    Tiles.personalStatsDtpsValue.setText("_")
    Tiles.personalStatsTotalDamageTakenValue.setText("_")

    //HTPS
    Tiles.percentileHtps.setValue(0)
    Tiles.personalStatsHtpsValue.setText("_")
    Tiles.personalStatsTotalHealingTakenValue.setText("_")

    //Threat
    Tiles.percentileThreat.setValue(0)
    Tiles.personalStatsThreatValue.setText("_")
    Tiles.personalStatsThreatPerSecondValue.setText("_")

    //Crit
    Tiles.percentileCrit.setValue(0)
    Tiles.personalStatsCritValue.setText("_")

    //Apm
    Tiles.percentileApm.setValue(0)
    Tiles.personalStatsApmValue.setText("_")

    //Time
    Tiles.personalStatsTimeValue.setText("_")
  }


  def refreshUI( ): Unit = {
    /**
     * Update the main dps chart
     */
    updateMainDpsChart()

    /**
     * Damage Done By Source
     */
    updateDamageDoneBySource()

    /**
     * Update Damage Taken By Source
     */
    updateDamageTakenBySource()


    /**
     * Update Leader Board
     */
    updateLeaderBoard()

    /**
     * Update Personal Stats
     */
    updatePersonalStats()

    /**
     * Damage Taken Tab Chart
     */
    updateDamageTakenChart()

    /**
     * Update Overlays
     */
    updateOverlays()
  }

  /**
   * This function is called from the menu item when a new combat instance is selected.
   * It may need to change when we move changing combat instances out of the menu bar.
   */
  def combatInstanceChangeMenuAction( ): ActionEvent => Unit = (event: ActionEvent) => {

    // set the current combat instance
    Controller
      .setCurrentCombatInstance(Controller.
        getCombatInstanceById(event.getTarget.asInstanceOf[javafx.scene.control.MenuItem].getText))

    refreshUI()

    //once the UI is set, because we clicked on a past combat instance, set current combat to null
    Controller.endCombat()

  }

  def loadNewCombatFile(): ActionEvent => Unit = (event: ActionEvent) => {
    Controller.resetController()
    Parser.resetParser()
    val file = event.getTarget.asInstanceOf[javafx.scene.control.MenuItem].getText
    val path = s"${UICodeConfig.logPath}${file}"
    UICodeConfig.logFile = file
    Logger.info(s"Loading new log ${path}")
//    Platform.runLater(LoadingScreen.beginLoading())
    Logger.debug("Begin Parsing")
    Controller.parseLatest(Parser.getNewLines(path))
    Logger.debug("End Parsing")

    combatInstanceMenu.getItems.clear()

    loadCombatInstanceMenu()

  }


  /**
   * This function is for updating the UI during live parsing,
   * it is called in the timer loop
   */
    // TODO: This is all junk right now, doesnt do anything, just sample code.
  def performTickUpdateLiveParsing() = {


    /**
     * All this code is essentially updating the UI with Mock Data
     * */

    /** Timer Ran Code */
    if (Tiles.statusTile.getLeftValue() > 1000) { Tiles.statusTile.setLeftValue(0); }
    if (Tiles.statusTile.getMiddleValue() > 1000) { Tiles.statusTile.setMiddleValue(0); }
    if (Tiles.statusTile.getRightValue() > 1000) { Tiles.statusTile.setRightValue(0); }
    Tiles.statusTile.setLeftValue(Tiles.statusTile.getLeftValue() + random.nextInt(4))
    Tiles.statusTile.setMiddleValue(Tiles.statusTile.getMiddleValue() + random.nextInt(3))
    Tiles.statusTile.setRightValue(Tiles.statusTile.getRightValue() + random.nextInt(3))

    //        Tiles.leaderBoardTile.getLeaderBoardItems().get(random.nextInt(3)).setValue(random.nextDouble() * 80)
    //Tiles.timelineTile.addChartData(new ChartData("", random.nextDouble() * 300 + 50, Instant.now()));
    //Tiles.timelineTile.calcAutoScale()
    // if the current combat is not null, set to show player damage
    if (Controller.getCurrentCombat() != null) {
      // TODO: This needs to have a static graph if the combat is complete
      //Tiles.timelineTile.addChartData(new ChartData(Controller.getCurrentPlayerDamage(),java.time.Instant.now()))
      refreshUI()
    }
    //Tiles.timelineTile.setMaxTimePeriod(java.time.Duration.ofSeconds(900))

    /** Radar Percentiles Chart */
//    Tiles.percentileDps.setValue(random.nextDouble() * 50)
//    Tiles.percentileHps.setValue(random.nextDouble() * 50)
//    Tiles.percentileDtps.setValue(random.nextDouble() * 50)
//    Tiles.percentileHtps.setValue(random.nextDouble() * 50)
//    Tiles.percentileThreat.setValue(random.nextDouble() * 50)
//    Tiles.percentileCrit.setValue(random.nextDouble() * 50)
//    Tiles.percentileApm.setValue(random.nextDouble() * 50)
//    Tiles.percentileTime.setValue(random.nextDouble() * 50)

    /** Right side bar chart for personal stats*/
    //Tiles.barChartTile.getBarChartItems().get(random.nextInt(8)).setValue(random.nextDouble() * 800);
    //Tiles.barChartTile.getBarChartItems().get(2).setValue(5000)


  }


  def updatePersonalStats( ) = {
    // we want to pad the strings to have leading spaces and a total of n chars
    val padding = 10

    // Values
    val dps = Controller.getCurrentCombat().getPlayerInCombatActor().getDamagePerSecond
    val damage = Controller.getCurrentCombat().getPlayerInCombatActor().getDamageDone()
    val hps = Controller.getCurrentCombat().getPlayerInCombatActor().getHealingDonePerSecond()
    val healing = Controller.getCurrentCombat().getPlayerInCombatActor().getHealingDone()
    val damageTaken = Controller.getCurrentCombat().getPlayerInCombatActor().getDamageTaken()
    val dtps = Controller.getCurrentCombat().getPlayerInCombatActor().getDamageTakenPerSecond()
    val time = Controller.getCurrentCombat().combatTimeSeconds
    val htps = Controller.getCurrentCombat().getPlayerInCombatActor().getHealingTakenPerSecond()
    val healingTaken = Controller.getCurrentCombat().getPlayerInCombatActor().getHealingTaken()
    val threat = Controller.getCurrentCombat().getPlayerInCombatActor().getThreatDone()
    val tps = Controller.getCurrentCombat().getPlayerInCombatActor().getThreatDonePerSecond()
    val crit = Controller.getCurrentCombat().getPlayerInCombatActor().getCritDamagePercent() * 100
    val apm = Controller.getCurrentCombat().getPlayerInCombatActor().getApm()

    def makePercentile(value: Double): String = {
      if (value.toString.length >= 4) {
        value.toString.dropRight(value.toString.length-4)
      } else {
        value.toString
      }
    }

    // TODO: All Percentile Metrics Need to be calculated somehow

    //DPS
    Tiles.percentileDps.setValue(dps * 0.004)
    Tiles.personalStatsDpsValue.setText(dps.toString.reverse.padTo(padding,' ').reverse)
    Tiles.personalStatsTotalDamageValue.setText(damage.toString.reverse.padTo(padding,' ').reverse)

    //HPS
    Tiles.percentileHps.setValue(hps * 0.003)
    Tiles.personalStatsHpsValue.setText(hps.toString.reverse.padTo(padding,' ').reverse)
    Tiles.personalStatsTotalHealingValue.setText(healing.toString.reverse.padTo(padding,' ').reverse)

    //DTPS
    Tiles.percentileDtps.setValue(dtps * 0.005)
    Tiles.personalStatsDtpsValue.setText(dtps.toString.reverse.padTo(padding,' ').reverse)
    Tiles.personalStatsTotalDamageTakenValue.setText(damageTaken.toString.reverse.padTo(padding,' ').reverse)

    //HTPS
    Tiles.percentileHtps.setValue(htps * 0.005)
    Tiles.personalStatsHtpsValue.setText(htps.toString.reverse.padTo(padding,' ').reverse)
    Tiles.personalStatsTotalHealingTakenValue.setText(healingTaken.toString.reverse.padTo(padding,' ').reverse)

    //Threat
    Tiles.percentileThreat.setValue(tps * 0.003)
    Tiles.personalStatsThreatValue.setText(threat.toString.reverse.padTo(padding,' ').reverse)
    Tiles.personalStatsThreatPerSecondValue.setText(tps.toString.reverse.padTo(padding,' ').reverse)

    //Crit
    Tiles.percentileCrit.setValue(crit)
    Tiles.personalStatsCritValue.setText(makePercentile(crit).reverse.padTo(padding,' ').reverse)

    //Apm
    Tiles.percentileApm.setValue(apm * .04)
    Tiles.personalStatsApmValue.setText(makePercentile(apm).reverse.padTo(padding,' ').reverse)

    //Time
    Tiles.personalStatsTimeValue.setText(time.toString.reverse.padTo(padding,' ').reverse)


  }

  /**
   * Main DPS Chart
   */
  def updateMainDpsChart() = {
    // clear out and add all of the combat instance data to the chart
    val damageTimeSeries =  Controller.getCurrentCombat().getPlayerInCombatActor().getDamageDoneTimeSeries()
    val damagePerSecondTimeSeries = Controller.getCurrentCombat().getPlayerInCombatActor().getDamagePerSecondTimeSeries()
    //      println(s"Current combat has a saved time series of ${damageTimeSeries.size} elements")
    Tiles.overviewLineChartSeries.getData.removeAll()
    Tiles.overviewBarChartSeries.getData.removeAll()
    Tiles.overviewBarChartSeries.data = damageTimeSeries.toSeq.map(x => (x._1.toString(),x._2)).map(Tiles.toCatagoryChartData)
    Tiles.overviewLineChartSeries.data = damagePerSecondTimeSeries.toSeq.map(x => (x._1.toString(),x._2)).map(Tiles.toCatagoryChartData)
    //      println(s"Got max value of ${damageTimeSeries.valuesIterator.max}")
    try {
      Tiles.overviewChartYAxis.setUpperBound(damageTimeSeries.valuesIterator.max)
    }
    catch {
      case e: java.lang.UnsupportedOperationException => Logger.debug("No damage done, unable to perform max to set axis of damageTakenChart")
      case e: Throwable => Logger.error(s"Error trying to set damageTaken chart axis: ${e}")
    }
  }

  /**
   * Update Damage Taken Line/Bar Chart
   */
  def updateDamageTakenChart() = {
    // clear out and add all of the combat instance data to the chart
    val damageTimeSeries =  Controller.getCurrentCombat().getPlayerInCombatActor().getDamageTakenTimeSeries()
    val damagePerSecondTimeSeries = Controller.getCurrentCombat().getPlayerInCombatActor().getDamageTakenPerSecondTimeSeries()
    //      println(s"Current combat has a saved time series of ${damageTimeSeries.size} elements")
    Tiles.damageTakenLineChartSeries.getData.removeAll()
    Tiles.damageTakenBarChartSeries.getData.removeAll()
    Tiles.damageTakenBarChartSeries.data = damageTimeSeries.toSeq.map(x => (x._1.toString(),x._2)).map(Tiles.toCatagoryChartData)
    Tiles.damageTakenLineChartSeries.data = damagePerSecondTimeSeries.toSeq.map(x => (x._1.toString(),x._2)).map(Tiles.toCatagoryChartData)
    //      println(s"Got max value of ${damageTimeSeries.valuesIterator.max}")
    try {
      Tiles.damageTakenChartYAxis.setUpperBound(damageTimeSeries.valuesIterator.max)
    }
    catch {
      case e: java.lang.UnsupportedOperationException => Logger.debug("No damage taken, unable to perform max to set axis of damageTakenChart")
      case e: Throwable => Logger.error(s"Error trying to set damageTaken chart axis: ${e}")
    }
  }

  def updateDamageDoneBySource() = {
    /**
     * * * * * * Damage Done Section * * * * *
     *
     * Update the base types for the damage taken pie chart
     */

    /** TODO: This and the damage taken section have a lot of repeat code, can probably factor some of this
     * out into functions. Both here and in the CombatActorInstance functions
     */


    // remove the all old data for both tiles
    Tiles.damageDoneTree.removeAllNodes()



    for (types <- Controller.getCurrentCombat().getPlayerInCombatActor().getDamageTypeDone()) {
      // TODO: Need to make sure you have ALL the damage types here or they wont show
      types._1 match {
        case "internal" => {
          new TreeNode(new ChartData("Internal", types._2, UICodeConfig.internalColor), Tiles.damageDoneTree);
        }
        case "kinetic" => {
          new TreeNode(new ChartData("Kinetic", types._2, UICodeConfig.kineticColor), Tiles.damageDoneTree);
        }
        case "energy" => {
          new TreeNode(new ChartData("Energy", types._2, UICodeConfig.energyColor), Tiles.damageDoneTree);
        }
        case "elemental" => {
          new TreeNode(new ChartData("Elemental", types._2, UICodeConfig.elementalColor), Tiles.damageDoneTree);
        }
        case "No Type" =>
        case x => {
          if (x != "-)") Logger.warn(s"Got Unknown Damage type: ${x}")
          new TreeNode(new ChartData("Regular", types._2, UICodeConfig.regularColor), Tiles.damageDoneTree);
        }
      }
    }

    /**
     * Update the damage done from source tile ability data
     */

    for (types <- Controller.getCurrentCombat().getPlayerInCombatActor().getDamageDoneStats()) {
      types._1 match {
        case "internal" => {
          for (ability <- types._2) {
            new TreeNode(new ChartData(ability._1, ability._2, UICodeConfig.internalColor), getCorrectChild("Internal","dps"));
          }
        }
        case "kinetic" => {
          for (ability <- types._2) {
            new TreeNode(new ChartData(ability._1, ability._2, UICodeConfig.kineticColor), getCorrectChild("Kinetic","dps"));
          }
        }
        case "energy" => {
          for (ability <- types._2) {
            new TreeNode(new ChartData(ability._1, ability._2, UICodeConfig.energyColor), getCorrectChild("Energy","dps"));
          }
        }
        case "elemental" => {
          for (ability <- types._2) {
            new TreeNode(new ChartData(ability._1, ability._2, UICodeConfig.elementalColor), getCorrectChild("Elemental","dps"));
          }
        }
        case "No Type" =>
        case x => {
          for (ability <- types._2) {
            new TreeNode(new ChartData(ability._1, ability._2, UICodeConfig.regularColor), getCorrectChild("Regular","dps"));
          }
        }

      }
    }

  }

  /**
   * * * * * * Damage Taken Section * * * * *
   *
   * Update the damage types indicator as well as the base types for the damage taken pie chart thing
   */

  def updateDamageTakenBySource( ) = {
    // remove the all old data for both tiles
    Tiles.overviewDtpstree.removeAllNodes()
    Tiles.overviewDamageFromTypeIndicator.clearChartData()
    Tiles.damageTakenDtpstree.removeAllNodes()
    Tiles.damageTakenDamageFromTypeIndicator.clearChartData()


    /**
     * Overview damage taken types
     */
    for (types <- Controller.getCurrentCombat().getPlayerInCombatActor().getDamageTypeTaken()) {
      // TODO: Need to make sure you have ALL the damage types here or they wont show
      types._1 match {
        case "internal" => {
          Tiles.overviewDamageFromTypeIndicator.addChartData(new ChartData("Internal",types._2,UICodeConfig.internalColor))
          new TreeNode(new ChartData("Internal", types._2, UICodeConfig.internalColor), Tiles.overviewDtpstree);
        }
        case "kinetic" => {
          Tiles.overviewDamageFromTypeIndicator.addChartData(new ChartData("Kinetic",types._2,UICodeConfig.kineticColor))
          new TreeNode(new ChartData("Kinetic", types._2, UICodeConfig.kineticColor), Tiles.overviewDtpstree);
        }
        case "energy" => {
          Tiles.overviewDamageFromTypeIndicator.addChartData(new ChartData("Energy",types._2,UICodeConfig.energyColor))
          new TreeNode(new ChartData("Energy", types._2, UICodeConfig.energyColor), Tiles.overviewDtpstree);
        }
        case "elemental" => {
          Tiles.overviewDamageFromTypeIndicator.addChartData(new ChartData("Elemental",types._2,UICodeConfig.elementalColor))
          new TreeNode(new ChartData("Elemental", types._2, UICodeConfig.elementalColor), Tiles.overviewDtpstree);
        }
        case "No Type" =>
        case x => {
          if (x != "-)") Logger.warn(s"Got Unknown Damage type: ${x}")
          Tiles.overviewDamageFromTypeIndicator.addChartData(new ChartData("Regular",types._2,UICodeConfig.regularColor))
          new TreeNode(new ChartData("Regular", types._2, UICodeConfig.regularColor), Tiles.overviewDtpstree);
        }
      }
    }

    /**
     * Update the damage taken from source tile ability data
     */

    for (types <- Controller.getCurrentCombat().getPlayerInCombatActor().getDamageTakenStats()) {
      types._1 match {
        case "internal" => {
          for (ability <- types._2) {
            new TreeNode(new ChartData(ability._1, ability._2, UICodeConfig.internalColor), getCorrectChild("Internal","dtps"));
          }
        }
        case "kinetic" => {
          for (ability <- types._2) {
            new TreeNode(new ChartData(ability._1, ability._2, UICodeConfig.kineticColor), getCorrectChild("Kinetic","dtps"));
          }
        }
        case "energy" => {
          for (ability <- types._2) {
            new TreeNode(new ChartData(ability._1, ability._2, UICodeConfig.energyColor), getCorrectChild("Energy","dtps"));
          }
        }
        case "elemental" => {
          for (ability <- types._2) {
            new TreeNode(new ChartData(ability._1, ability._2, UICodeConfig.elementalColor), getCorrectChild("Elemental","dtps"));
          }
        }
        case "No Type" =>
        case x => {
          for (ability <- types._2) {
            new TreeNode(new ChartData(ability._1, ability._2, UICodeConfig.regularColor), getCorrectChild("Regular","dtps"));
          }
        }

      }
    }

    /**
     * Damage Taken Tab Types Wheel
     */
    for (types <- Controller.getCurrentCombat().getPlayerInCombatActor().getDamageTypeTaken()) {
      // TODO: Need to make sure you have ALL the damage types here or they wont show
      types._1 match {
        case "internal" => {
          Tiles.damageTakenDamageFromTypeIndicator.addChartData(new ChartData("Internal",types._2,UICodeConfig.internalColor))
          new TreeNode(new ChartData("Internal", types._2, UICodeConfig.internalColor), Tiles.damageTakenDtpstree);
        }
        case "kinetic" => {
          Tiles.damageTakenDamageFromTypeIndicator.addChartData(new ChartData("Kinetic",types._2,UICodeConfig.kineticColor))
          new TreeNode(new ChartData("Kinetic", types._2, UICodeConfig.kineticColor), Tiles.damageTakenDtpstree);
        }
        case "energy" => {
          Tiles.damageTakenDamageFromTypeIndicator.addChartData(new ChartData("Energy",types._2,UICodeConfig.energyColor))
          new TreeNode(new ChartData("Energy", types._2, UICodeConfig.energyColor), Tiles.damageTakenDtpstree);
        }
        case "elemental" => {
          Tiles.damageTakenDamageFromTypeIndicator.addChartData(new ChartData("Elemental",types._2,UICodeConfig.elementalColor))
          new TreeNode(new ChartData("Elemental", types._2, UICodeConfig.elementalColor), Tiles.damageTakenDtpstree);
        }
        case "No Type" =>
        case x => {
          if (x != "-)") Logger.warn(s"Got Unknown Damage type: ${x}")
          Tiles.damageTakenDamageFromTypeIndicator.addChartData(new ChartData("Regular",types._2,UICodeConfig.regularColor))
          new TreeNode(new ChartData("Regular", types._2, UICodeConfig.regularColor), Tiles.damageTakenDtpstree);
        }
      }
    }

    /**
     * Damage Taken Tab from source tile ability data
     */

    for (types <- Controller.getCurrentCombat().getPlayerInCombatActor().getDamageTakenStats()) {
      types._1 match {
        case "internal" => {
          for (ability <- types._2) {
            new TreeNode(new ChartData(ability._1, ability._2, UICodeConfig.internalColor), getCorrectChild("Internal","dtps"));
          }
        }
        case "kinetic" => {
          for (ability <- types._2) {
            new TreeNode(new ChartData(ability._1, ability._2, UICodeConfig.kineticColor), getCorrectChild("Kinetic","dtps"));
          }
        }
        case "energy" => {
          for (ability <- types._2) {
            new TreeNode(new ChartData(ability._1, ability._2, UICodeConfig.energyColor), getCorrectChild("Energy","dtps"));
          }
        }
        case "elemental" => {
          for (ability <- types._2) {
            new TreeNode(new ChartData(ability._1, ability._2, UICodeConfig.elementalColor), getCorrectChild("Elemental","dtps"));
          }
        }
        case "No Type" =>
        case x => {
          for (ability <- types._2) {
            new TreeNode(new ChartData(ability._1, ability._2, UICodeConfig.regularColor), getCorrectChild("Regular","dtps"));
          }
        }

      }
    }



  }

  /**
   * Helper function to get inner ring to add ability damage to.
   * This is used for both damage taken from source and
   * damage done from source ability.
   * @param name
   * @return
   */
    // TODO: Need to update this for the new tabs
  def getCorrectChild(name : String, from : String): TreeNode[ChartData] = {
    if (from == "dtps") {
      val root : java.util.List[TreeNode[ChartData]] = Tiles.overviewDtpstree.getAll
      for (i <- 0 until root.size()){
        if(root.get(i).getItem.getName == name) return root.get(i)
      }
    }
    else {
      val root : java.util.List[TreeNode[ChartData]] = Tiles.damageDoneTree.getAll
      for (i <- 0 until root.size()){
        if(root.get(i).getItem.getName == name) return root.get(i)
      }
    }
    // this is a backup, probably shouldn't happen
      Logger.error("Error, returning root tree, this should not happen")
    Tiles.overviewDtpstree
  }

  def updateLeaderBoard( ) = {
    /**
     * Update the leaderboard tile with all player damage
     *
     *  we do not clear leaderboard items, and create new items, instead we iterate through them setting new values
     * there are 24 by default, for the max swtor group size. Only set the number visible that
     * correspond to the number of players in this combat. We have to do it this way because of something
     * with how the leaderboard tile works
     */

    for (index <- 0 until Tiles.leaderBoardItems.size()){
      Tiles.leaderBoardItems.get(index).setValue(0)
      Tiles.leaderBoardItems.get(index).setName("")
      Tiles.leaderBoardItems.get(index).setVisible(false)
    }

    // get all the combat Actors
    val combatActors = Controller.getCurrentCombat().getCombatActors()
    val players = (for (actor <- combatActors) yield actor.getActor()).filter(_.isInstanceOf[Player])
    var lastUpdatedIndex = 0
    for (index <- 0 until players.length){
      // need to relate the player actor instance to the combat actor instance
      // then we need to get the damage done and order them
      val combatInstanceActor = Controller.getCurrentCombat().getCombatActorByIdString(players(index).getId().toString)
      // TODO: I cannot get this to set the name to save my life, help!
      Tiles.leaderBoardItems.get(index).setValue(combatInstanceActor.getDamagePerSecond())
      Tiles.leaderBoardItems.get(index).setName(combatInstanceActor.getActor().getName())
      Tiles.leaderBoardItems.get(index).getChartData.setName(combatInstanceActor.getActor().getName())
      Tiles.leaderBoardItems.get(index).setVisible(true)
    }
  }


  def updateOverlays( ): Unit = {

    /**
     * Clear Data
     */
    Overlays.personalDamageOverlay.clearChartData()
    Overlays.personalHealingOverlay.clearChartData()
    Overlays.personalDamageTakenOverlay.clearChartData()
    Overlays.groupDamagePane.getChildren.clear()
    Overlays.groupHealingPane.getChildren.clear()


    /**
     * Update Overlay Your Damage Done
     */

    Overlays.personalDamageOverlay.setTitle(s"Dps: ${Controller.getCurrentCombat().getPlayerInCombatActor().getDamagePerSecond()}")
    for (damageTypeDone <- Controller.getCurrentCombat().getPlayerInCombatActor().getDamageDone1DStats()) {
      for (damageSource <- damageTypeDone._2.keys) {
        val value = Controller.getCurrentCombat().getPlayerInCombatActor().getDamageDone1DStats().get("").get(damageSource)
        Overlays.personalDamageOverlay.addChartData(new ChartData(damageSource,value,UICodeConfig.randomColor()))
      }
    }
//    Overlays.personalDamageOverlay.clearChartData()
//    Overlays.personalDamageOverlay.setTitle(s"DPS: ${Controller.getCurrentCombat().getPlayerInCombatActor().getDamagePerSecond()}")
//    for (damageTypeDone <- Controller.getCurrentCombat().getPlayerInCombatActor().getDamageTypeDone()) {
//      damageTypeDone._1 match {
//        case "internal" => {
//          Overlays.personalDamageOverlay.addChartData(new ChartData(damageTypeDone._1,damageTypeDone._2,uiCodeConfig.internalColor))        }
//        case "kinetic" => {
//          Overlays.personalDamageOverlay.addChartData(new ChartData(damageTypeDone._1,damageTypeDone._2,uiCodeConfig.kineticColor))
//        }
//        case "energy" => {
//          Overlays.personalDamageOverlay.addChartData(new ChartData(damageTypeDone._1,damageTypeDone._2,uiCodeConfig.energyColor))
//        }
//        case "elemental" => {
//          Overlays.personalDamageOverlay.addChartData(new ChartData(damageTypeDone._1,damageTypeDone._2,uiCodeConfig.elementalColor))
//        }
//        case "No Type" =>
//        case x => {
//          println(s"Got Unknown Damage type: ${x}")
//          Overlays.personalDamageOverlay.addChartData(new ChartData(damageTypeDone._1,damageTypeDone._2,uiCodeConfig.internalColor))
//        }
//      }
//    }

    /**
     * Update Overlay Your Healing Done
     */

    Overlays.personalHealingOverlay.setTitle(s"Hps: ${Controller.getCurrentCombat().getPlayerInCombatActor().getHealingDonePerSecond()}")
    for (healingTypeDone <- Controller.getCurrentCombat().getPlayerInCombatActor().getHealingDoneStats()) {
      for (healSource <- healingTypeDone._2.keys) {
        val healValue = Controller.getCurrentCombat().getPlayerInCombatActor().getHealingDoneStats().get("").get(healSource)
        Overlays.personalHealingOverlay.addChartData(new ChartData(healSource,healValue,UICodeConfig.randomColor()))
      }
    }


    /**
     * Update Overlay Your Damage Taken
     */

    Overlays.personalDamageTakenOverlay.setTitle(s"Dtps: ${Controller.getCurrentCombat().getPlayerInCombatActor().getDamageTakenPerSecond()}")
    for (damageTypeTaken <- Controller.getCurrentCombat().getPlayerInCombatActor().getDamageTaken1DStats()) {
      for (damageSource <- damageTypeTaken._2.keys) {
        val value = Controller.getCurrentCombat().getPlayerInCombatActor().getDamageTaken1DStats().get("").get(damageSource)
        Overlays.personalDamageTakenOverlay.addChartData(new ChartData(damageSource,value,UICodeConfig.randomColor()))
      }
    }


    /**
     * Update Overlay Group Damage Done
     */

    // what actor has done the most damage this tick?
    var maxDamage = 1
    var totalDamage = 1
    for (actor <- Controller.getCurrentCombat().getCombatActors()) {
      totalDamage = totalDamage + actor.getDamageDone()
      if (actor.getDamageDone() > maxDamage) maxDamage = actor.getDamageDone()
    }
    if (totalDamage > 1) totalDamage = totalDamage - 1

    val sortedByDamageDone = Controller.getCurrentCombat().getCombatActors().sortWith(_.getDamageDone() > _.getDamageDone()).filter(_.getDamageDone() > 0)

    for (actor <- sortedByDamageDone) {
      val stacked = new StackPane()
      val text = new Text()
      val percentMaxFill: Int = ((actor.getDamageDone().toDouble / maxDamage) * 200).toInt
      val percentMax: Int = ((actor.getDamageDone().toDouble / totalDamage) * 100).toInt
      text.setText(actor.getActor().getName() + ": " + actor.getDamagePerSecond() + " (" + percentMax + "%)")
      val rect = Rectangle(percentMaxFill,30)
      val backgroundRect = Rectangle(600, 30)
      if(actor.getActorType() == "Player"){
        rect.setStyle("-fx-fill: #FF3633; -fx-stroke: black; -fx-stroke-width: 2;")
      }
      else if (actor.getActorType() == "Companion") {
        rect.setStyle("-fx-fill: #B93DFF; -fx-stroke: black; -fx-stroke-width: 2;")
      }
      else {
        rect.setStyle("-fx-fill: #4C3DFF; -fx-stroke: black; -fx-stroke-width: 2;")
      }
      backgroundRect.setStyle("-fx-fill: #FF908D; -fx-stroke: black; -fx-stroke-width: 2;")
      stacked.getChildren.addAll(backgroundRect,rect,text)
      stacked.setAlignment(Pos.CenterLeft)
      groupDamagePane.getChildren.add(stacked)
    }


    /**
     * Update Overlay Group Healing Done
     */

    // what actor has done the most Healing this tick?
    var maxHealing = 1
    var totalHealing = 1
    for (actor <- Controller.getCurrentCombat().getCombatActors()) {
      totalHealing = totalHealing + actor.getHealingDone()
      if (actor.getHealingDone() > maxHealing) maxHealing = actor.getHealingDone()
    }
    if(totalHealing > 1) totalHealing = totalHealing - 1

    val sortedByHealingDone = Controller.getCurrentCombat().getCombatActors().sortWith(_.getHealingDone() > _.getHealingDone()).filter(_.getHealingDone() > 0)

    for (actor <- sortedByHealingDone) {
      val stacked = new StackPane()
      val text = new Text()
      val percentMaxFill: Int = ((actor.getHealingDone().toDouble / maxHealing) * 200).toInt
      val percentMax: Int = ((actor.getHealingDone().toDouble / totalHealing) * 100).toInt
      text.setText(actor.getActor().getName() + ": " + actor.getHealingDonePerSecond() + " (" + percentMax + "%)")
      val rect = Rectangle(percentMaxFill,30)
      val backgroundRect = Rectangle(600, 30)
      backgroundRect.setStyle("-fx-fill: #48FF80; -fx-stroke: black; -fx-stroke-width: 2;")
      if(actor.getActorType() == "Player"){
        rect.setStyle("-fx-fill: #5CFF47; -fx-stroke: black; -fx-stroke-width: 2;")
      }
      else if (actor.getActorType() == "Companion") {
        rect.setStyle("-fx-fill: #B93DFF; -fx-stroke: black; -fx-stroke-width: 2;")
      }
      else {
        rect.setStyle("-fx-fill: #4C3DFF; -fx-stroke: black; -fx-stroke-width: 2;")
      }
      stacked.getChildren.addAll(backgroundRect,rect,text)
      stacked.setAlignment(Pos.CenterLeft)
      groupHealingPane.getChildren.add(stacked)
    }


  }



}

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
import parser.Parser
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

import java.io.File

/**
 * Element loader is for loading data into the UI charts and graphs etc.
 * It will be called on refreshes and loading new combats
 */
class ElementLoader {

  val config = ConfigFactory.load()

  // This can be used to generate random numbers
  val random = scala.util.Random

  /**
   * Attempt at defining things to load asyncronously
   */
    def initAsynchronously(controller: Controller, parser: Parser, tiles: GuiTiles, combatInstanceMenu: Menu, timer: AnimationTimer): Unit = {
      // we obviously want to parse asyncronously, this is the big time killer
      Logger.trace("Initializing Lines Async")
      controller.parseLatest(parser.getNewLinesInit())
      // we then need to load everything after that depends on the parse results
      loadCombatInstanceMenu(controller,tiles, combatInstanceMenu)

      // Load the UI to the combat we just loaded
      // set the current combat instance
      controller
        .setCurrentCombatInstance(controller.getAllCombatInstances()(0))

      refreshUI(controller,tiles)

      //once the UI is set, because we clicked on a past combat instance, set current combat to null
      controller.endCombat()


      timer.start()
      Logger.info("Timer Started")
    }


  /**
   * Attempt at defining remaining combat instances to load asyncronously
   */
  def initRemainingAsynchronously(controller: Controller, parser: Parser, tiles: GuiTiles, combatInstanceMenu: Menu, timer: AnimationTimer): Unit = {
    // we obviously want to parse asyncronously, this is the big time killer
    Logger.trace("Initializing Lines Async")
    controller.parseLatest(parser.parseRemaining())
    // we then need to load everything after that depends on the parse results
    loadCombatInstanceMenu(controller,tiles, combatInstanceMenu)
    Logger.info("Remaining Combat Instances Loaded")
  }

  /**
   * This is refreshes the combat instances in the combat instance menu
   */
  def loadCombatInstanceMenu(controller: Controller, tiles: GuiTiles,combatInstanceMenu: Menu): Unit ={
    var combatInstanceBuffer = new ListBuffer[MenuItem]()
    for (combatInstance <- controller.getAllCombatInstances()){
      Logger.trace(s"Got combat instance: ${combatInstance}")
      var item = new MenuItem(combatInstance.getNameFromActors)
      item.setOnAction(combatInstanceChangeMenuAction(controller, tiles))
      combatInstanceBuffer += item
    }
    combatInstanceMenu.items = combatInstanceBuffer.toList
  }

  def loadLogFileMenu(controller: Controller, tiles: GuiTiles,parser:Parser,fileMenu: Menu,combatInstanceMenu: Menu):Unit = {
    val files: List[File] = FileHelper.getListOfFiles(UICodeConfig.logPath)
    var fileBuffer = new ListBuffer[MenuItem]()
    for (i <- 0 until files.length){
      var item = new MenuItem(files(i).getAbsolutePath().split('\\').last)
      item.setOnAction(loadNewCombatFile(controller, tiles,parser,combatInstanceMenu))
      fileBuffer += item
    }
    fileMenu.items = fileBuffer.toList.reverse

  }

  def clearUI(controller: Controller,tiles: GuiTiles): Unit = {
    tiles.overviewLineChartSeries.getData.removeAll()
    tiles.overviewBarChartSeries.getData.removeAll()
    tiles.damageTakenLineChartSeries.getData.removeAll()
    tiles.damageTakenBarChartSeries.getData.removeAll()
    tiles.damageDoneTree.removeAllNodes()
    tiles.overviewDtpstree.removeAllNodes()
    tiles.overviewDamageFromTypeIndicator.clearChartData()
    tiles.damageTakenDtpstree.removeAllNodes()
    tiles.damageTakenDamageFromTypeIndicator.clearChartData()

    // Overlays
    Overlays.personalDamageOverlay.clearChartData()
    Overlays.personalHealingOverlay.clearChartData()
    Overlays.personalDamageTakenOverlay.clearChartData()
    Overlays.groupDamagePane.getChildren.clear()
    Overlays.groupHealingPane.getChildren.clear()


    for (index <- 0 until tiles.leaderBoardItems.size()){
      tiles.leaderBoardItems.get(index).setValue(0)
      tiles.leaderBoardItems.get(index).setName("")
      tiles.leaderBoardItems.get(index).setVisible(false)
    }

    /**
     * Personal Stats
     */
    //DPS
    tiles.percentileDps.setValue(0)
    tiles.personalStatsDpsValue.setText("_")
    tiles.personalStatsTotalDamageValue.setText("_")

    //HPS
    tiles.percentileHps.setValue(0)
    tiles.personalStatsHpsValue.setText("_")
    tiles.personalStatsTotalHealingValue.setText("_")

    //DTPS
    tiles.percentileDtps.setValue(0)
    tiles.personalStatsDtpsValue.setText("_")
    tiles.personalStatsTotalDamageTakenValue.setText("_")

    //HTPS
    tiles.percentileHtps.setValue(0)
    tiles.personalStatsHtpsValue.setText("_")
    tiles.personalStatsTotalHealingTakenValue.setText("_")

    //Threat
    tiles.percentileThreat.setValue(0)
    tiles.personalStatsThreatValue.setText("_")
    tiles.personalStatsThreatPerSecondValue.setText("_")

    //Crit
    tiles.percentileCrit.setValue(0)
    tiles.personalStatsCritValue.setText("_")

    //Apm
    tiles.percentileApm.setValue(0)
    tiles.personalStatsApmValue.setText("_")

    //Time
    tiles.personalStatsTimeValue.setText("_")
  }


  def refreshUI(controller: Controller, tiles: GuiTiles): Unit = {
    /**
     * Update the main dps chart
     */
    updateMainDpsChart(controller, tiles)

    /**
     * Damage Done By Source
     */
    updateDamageDoneBySource(controller, tiles)

    /**
     * Update Damage Taken By Source
     */
    updateDamageTakenBySource(controller,tiles)


    /**
     * Update Leader Board
     */
    updateLeaderBoard(controller,tiles)

    /**
     * Update Personal Stats
     */
    updatePersonalStats(controller,tiles)

    /**
     * Damage Taken Tab Chart
     */
    updateDamageTakenChart(controller,tiles)

    /**
     * Update Overlays
     */
    updateOverlays(controller,tiles)
  }

  /**
   * This function is called from the menu item when a new combat instance is selected.
   * It may need to change when we move changing combat instances out of the menu bar.
   */
  def combatInstanceChangeMenuAction(controller: Controller, tiles: GuiTiles): ActionEvent => Unit = (event: ActionEvent) => {

    // set the current combat instance
    controller
      .setCurrentCombatInstance(controller.
        getCombatInstanceById(event.getTarget.asInstanceOf[javafx.scene.control.MenuItem].getText))

    refreshUI(controller,tiles)

    //once the UI is set, because we clicked on a past combat instance, set current combat to null
    controller.endCombat()

  }

  def loadNewCombatFile(controller: Controller, tiles: GuiTiles, parser: Parser, combatInstanceMenu: Menu): ActionEvent => Unit = (event: ActionEvent) => {
    controller.resetController()
    parser.resetParser()
    val file = event.getTarget.asInstanceOf[javafx.scene.control.MenuItem].getText
    val path = s"${UICodeConfig.logPath}${file}"
    UICodeConfig.logFile = file
    Logger.info(s"Loading new log ${path}")
//    Platform.runLater(LoadingScreen.beginLoading())
    Logger.debug("Begin Parsing")
    controller.parseLatest(parser.getNewLines(path))
    Logger.debug("End Parsing")

    combatInstanceMenu.getItems.clear()

    loadCombatInstanceMenu(controller,tiles, combatInstanceMenu)

  }


  /**
   * This function is for updating the UI during live parsing,
   * it is called in the timer loop
   */
    // TODO: This is all junk right now, doesnt do anything, just sample code.
  def performTickUpdateLiveParsing(controller: Controller,tiles: GuiTiles) = {


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
      refreshUI(controller,tiles)
    }
    //tiles.timelineTile.setMaxTimePeriod(java.time.Duration.ofSeconds(900))

    /** Radar Percentiles Chart */
//    tiles.percentileDps.setValue(random.nextDouble() * 50)
//    tiles.percentileHps.setValue(random.nextDouble() * 50)
//    tiles.percentileDtps.setValue(random.nextDouble() * 50)
//    tiles.percentileHtps.setValue(random.nextDouble() * 50)
//    tiles.percentileThreat.setValue(random.nextDouble() * 50)
//    tiles.percentileCrit.setValue(random.nextDouble() * 50)
//    tiles.percentileApm.setValue(random.nextDouble() * 50)
//    tiles.percentileTime.setValue(random.nextDouble() * 50)

    /** Right side bar chart for personal stats*/
    //tiles.barChartTile.getBarChartItems().get(random.nextInt(8)).setValue(random.nextDouble() * 800);
    //tiles.barChartTile.getBarChartItems().get(2).setValue(5000)


  }


  def updatePersonalStats(controller: Controller, tiles: GuiTiles) = {
    // we want to pad the strings to have leading spaces and a total of n chars
    val padding = 10

    // Values
    val dps = controller.getCurrentCombat().getPlayerInCombatActor().getDamagePerSecond
    val damage = controller.getCurrentCombat().getPlayerInCombatActor().getDamageDone()
    val hps = controller.getCurrentCombat().getPlayerInCombatActor().getHealingDonePerSecond()
    val healing = controller.getCurrentCombat().getPlayerInCombatActor().getHealingDone()
    val damageTaken = controller.getCurrentCombat().getPlayerInCombatActor().getDamageTaken()
    val dtps = controller.getCurrentCombat().getPlayerInCombatActor().getDamageTakenPerSecond()
    val time = controller.getCurrentCombat().combatTimeSeconds
    val htps = controller.getCurrentCombat().getPlayerInCombatActor().getHealingTakenPerSecond()
    val healingTaken = controller.getCurrentCombat().getPlayerInCombatActor().getHealingTaken()
    val threat = controller.getCurrentCombat().getPlayerInCombatActor().getThreatDone()
    val tps = controller.getCurrentCombat().getPlayerInCombatActor().getThreatDonePerSecond()
    val crit = controller.getCurrentCombat().getPlayerInCombatActor().getCritDamagePercent() * 100
    val apm = controller.getCurrentCombat().getPlayerInCombatActor().getApm()

    def makePercentile(value: Double): String = {
      if (value.toString.length >= 4) {
        value.toString.dropRight(value.toString.length-4)
      } else {
        value.toString
      }
    }

    // TODO: All Percentile Metrics Need to be calculated somehow

    //DPS
    tiles.percentileDps.setValue(dps * 0.004)
    tiles.personalStatsDpsValue.setText(dps.toString.reverse.padTo(padding,' ').reverse)
    tiles.personalStatsTotalDamageValue.setText(damage.toString.reverse.padTo(padding,' ').reverse)

    //HPS
    tiles.percentileHps.setValue(hps * 0.003)
    tiles.personalStatsHpsValue.setText(hps.toString.reverse.padTo(padding,' ').reverse)
    tiles.personalStatsTotalHealingValue.setText(healing.toString.reverse.padTo(padding,' ').reverse)

    //DTPS
    tiles.percentileDtps.setValue(dtps * 0.005)
    tiles.personalStatsDtpsValue.setText(dtps.toString.reverse.padTo(padding,' ').reverse)
    tiles.personalStatsTotalDamageTakenValue.setText(damageTaken.toString.reverse.padTo(padding,' ').reverse)

    //HTPS
    tiles.percentileHtps.setValue(htps * 0.005)
    tiles.personalStatsHtpsValue.setText(htps.toString.reverse.padTo(padding,' ').reverse)
    tiles.personalStatsTotalHealingTakenValue.setText(healingTaken.toString.reverse.padTo(padding,' ').reverse)

    //Threat
    tiles.percentileThreat.setValue(tps * 0.003)
    tiles.personalStatsThreatValue.setText(threat.toString.reverse.padTo(padding,' ').reverse)
    tiles.personalStatsThreatPerSecondValue.setText(tps.toString.reverse.padTo(padding,' ').reverse)

    //Crit
    tiles.percentileCrit.setValue(crit)
    tiles.personalStatsCritValue.setText(makePercentile(crit).reverse.padTo(padding,' ').reverse)

    //Apm
    tiles.percentileApm.setValue(apm * .04)
    tiles.personalStatsApmValue.setText(makePercentile(apm).reverse.padTo(padding,' ').reverse)

    //Time
    tiles.personalStatsTimeValue.setText(time.toString.reverse.padTo(padding,' ').reverse)


  }

  /**
   * Main DPS Chart
   */
  def updateMainDpsChart(controller: Controller,tiles: GuiTiles) = {
    // clear out and add all of the combat instance data to the chart
    val damageTimeSeries =  controller.getCurrentCombat().getPlayerInCombatActor().getDamageDoneTimeSeries()
    val damagePerSecondTimeSeries = controller.getCurrentCombat().getPlayerInCombatActor().getDamagePerSecondTimeSeries()
    //      println(s"Current combat has a saved time series of ${damageTimeSeries.size} elements")
    tiles.overviewLineChartSeries.getData.removeAll()
    tiles.overviewBarChartSeries.getData.removeAll()
    tiles.overviewBarChartSeries.data = damageTimeSeries.toSeq.map(x => (x._1.toString(),x._2)).map(tiles.toCatagoryChartData)
    tiles.overviewLineChartSeries.data = damagePerSecondTimeSeries.toSeq.map(x => (x._1.toString(),x._2)).map(tiles.toCatagoryChartData)
    //      println(s"Got max value of ${damageTimeSeries.valuesIterator.max}")
    try {
      tiles.overviewChartYAxis.setUpperBound(damageTimeSeries.valuesIterator.max)
    }
    catch {
      case e: java.lang.UnsupportedOperationException => Logger.warn("No damage done, unable to perform max to set axis of damageTakenChart")
      case e: Throwable => Logger.error(s"Error trying to set damageTaken chart axis: ${e}")
    }
  }

  /**
   * Update Damage Taken Line/Bar Chart
   */
  def updateDamageTakenChart(controller: Controller,tiles: GuiTiles) = {
    // clear out and add all of the combat instance data to the chart
    val damageTimeSeries =  controller.getCurrentCombat().getPlayerInCombatActor().getDamageTakenTimeSeries()
    val damagePerSecondTimeSeries = controller.getCurrentCombat().getPlayerInCombatActor().getDamageTakenPerSecondTimeSeries()
    //      println(s"Current combat has a saved time series of ${damageTimeSeries.size} elements")
    tiles.damageTakenLineChartSeries.getData.removeAll()
    tiles.damageTakenBarChartSeries.getData.removeAll()
    tiles.damageTakenBarChartSeries.data = damageTimeSeries.toSeq.map(x => (x._1.toString(),x._2)).map(tiles.toCatagoryChartData)
    tiles.damageTakenLineChartSeries.data = damagePerSecondTimeSeries.toSeq.map(x => (x._1.toString(),x._2)).map(tiles.toCatagoryChartData)
    //      println(s"Got max value of ${damageTimeSeries.valuesIterator.max}")
    try {
      tiles.damageTakenChartYAxis.setUpperBound(damageTimeSeries.valuesIterator.max)
    }
    catch {
      case e: java.lang.UnsupportedOperationException => Logger.warn("No damage taken, unable to perform max to set axis of damageTakenChart")
      case e: Throwable => Logger.error(s"Error trying to set damageTaken chart axis: ${e}")
    }
  }

  def updateDamageDoneBySource(controller: Controller,tiles: GuiTiles) = {
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



    for (types <- controller.getCurrentCombat().getPlayerInCombatActor().getDamageTypeDone()) {
      // TODO: Need to make sure you have ALL the damage types here or they wont show
      types._1 match {
        case "internal" => {
          new TreeNode(new ChartData("Internal", types._2, UICodeConfig.internalColor), tiles.damageDoneTree);
        }
        case "kinetic" => {
          new TreeNode(new ChartData("Kinetic", types._2, UICodeConfig.kineticColor), tiles.damageDoneTree);
        }
        case "energy" => {
          new TreeNode(new ChartData("Energy", types._2, UICodeConfig.energyColor), tiles.damageDoneTree);
        }
        case "elemental" => {
          new TreeNode(new ChartData("Elemental", types._2, UICodeConfig.elementalColor), tiles.damageDoneTree);
        }
        case "No Type" =>
        case x => {
          println(s"Got Unknown Damage type: ${x}")
          new TreeNode(new ChartData("Regular", types._2, UICodeConfig.regularColor), tiles.damageDoneTree);
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
            new TreeNode(new ChartData(ability._1, ability._2, UICodeConfig.internalColor), getCorrectChild("Internal","dps",tiles));
          }
        }
        case "kinetic" => {
          for (ability <- types._2) {
            new TreeNode(new ChartData(ability._1, ability._2, UICodeConfig.kineticColor), getCorrectChild("Kinetic","dps",tiles));
          }
        }
        case "energy" => {
          for (ability <- types._2) {
            new TreeNode(new ChartData(ability._1, ability._2, UICodeConfig.energyColor), getCorrectChild("Energy","dps",tiles));
          }
        }
        case "elemental" => {
          for (ability <- types._2) {
            new TreeNode(new ChartData(ability._1, ability._2, UICodeConfig.elementalColor), getCorrectChild("Elemental","dps",tiles));
          }
        }
        case "No Type" =>
        case x => {
          for (ability <- types._2) {
            new TreeNode(new ChartData(ability._1, ability._2, UICodeConfig.regularColor), getCorrectChild("Regular","dps",tiles));
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

  def updateDamageTakenBySource(controller: Controller, tiles: GuiTiles) = {
    // remove the all old data for both tiles
    tiles.overviewDtpstree.removeAllNodes()
    tiles.overviewDamageFromTypeIndicator.clearChartData()
    tiles.damageTakenDtpstree.removeAllNodes()
    tiles.damageTakenDamageFromTypeIndicator.clearChartData()


    /**
     * Overview damage taken types
     */
    for (types <- controller.getCurrentCombat().getPlayerInCombatActor().getDamageTypeTaken()) {
      // TODO: Need to make sure you have ALL the damage types here or they wont show
      types._1 match {
        case "internal" => {
          tiles.overviewDamageFromTypeIndicator.addChartData(new ChartData("Internal",types._2,UICodeConfig.internalColor))
          new TreeNode(new ChartData("Internal", types._2, UICodeConfig.internalColor), tiles.overviewDtpstree);
        }
        case "kinetic" => {
          tiles.overviewDamageFromTypeIndicator.addChartData(new ChartData("Kinetic",types._2,UICodeConfig.kineticColor))
          new TreeNode(new ChartData("Kinetic", types._2, UICodeConfig.kineticColor), tiles.overviewDtpstree);
        }
        case "energy" => {
          tiles.overviewDamageFromTypeIndicator.addChartData(new ChartData("Energy",types._2,UICodeConfig.energyColor))
          new TreeNode(new ChartData("Energy", types._2, UICodeConfig.energyColor), tiles.overviewDtpstree);
        }
        case "elemental" => {
          tiles.overviewDamageFromTypeIndicator.addChartData(new ChartData("Elemental",types._2,UICodeConfig.elementalColor))
          new TreeNode(new ChartData("Elemental", types._2, UICodeConfig.elementalColor), tiles.overviewDtpstree);
        }
        case "No Type" =>
        case x => {
          println(s"Got Unknown Damage type: ${x}")
          tiles.overviewDamageFromTypeIndicator.addChartData(new ChartData("Regular",types._2,UICodeConfig.regularColor))
          new TreeNode(new ChartData("Regular", types._2, UICodeConfig.regularColor), tiles.overviewDtpstree);
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
            new TreeNode(new ChartData(ability._1, ability._2, UICodeConfig.internalColor), getCorrectChild("Internal","dtps",tiles));
          }
        }
        case "kinetic" => {
          for (ability <- types._2) {
            new TreeNode(new ChartData(ability._1, ability._2, UICodeConfig.kineticColor), getCorrectChild("Kinetic","dtps",tiles));
          }
        }
        case "energy" => {
          for (ability <- types._2) {
            new TreeNode(new ChartData(ability._1, ability._2, UICodeConfig.energyColor), getCorrectChild("Energy","dtps",tiles));
          }
        }
        case "elemental" => {
          for (ability <- types._2) {
            new TreeNode(new ChartData(ability._1, ability._2, UICodeConfig.elementalColor), getCorrectChild("Elemental","dtps",tiles));
          }
        }
        case "No Type" =>
        case x => {
          for (ability <- types._2) {
            new TreeNode(new ChartData(ability._1, ability._2, UICodeConfig.regularColor), getCorrectChild("Regular","dtps",tiles));
          }
        }

      }
    }

    /**
     * Damage Taken Tab Types Wheel
     */
    for (types <- controller.getCurrentCombat().getPlayerInCombatActor().getDamageTypeTaken()) {
      // TODO: Need to make sure you have ALL the damage types here or they wont show
      types._1 match {
        case "internal" => {
          tiles.damageTakenDamageFromTypeIndicator.addChartData(new ChartData("Internal",types._2,UICodeConfig.internalColor))
          new TreeNode(new ChartData("Internal", types._2, UICodeConfig.internalColor), tiles.damageTakenDtpstree);
        }
        case "kinetic" => {
          tiles.damageTakenDamageFromTypeIndicator.addChartData(new ChartData("Kinetic",types._2,UICodeConfig.kineticColor))
          new TreeNode(new ChartData("Kinetic", types._2, UICodeConfig.kineticColor), tiles.damageTakenDtpstree);
        }
        case "energy" => {
          tiles.damageTakenDamageFromTypeIndicator.addChartData(new ChartData("Energy",types._2,UICodeConfig.energyColor))
          new TreeNode(new ChartData("Energy", types._2, UICodeConfig.energyColor), tiles.damageTakenDtpstree);
        }
        case "elemental" => {
          tiles.damageTakenDamageFromTypeIndicator.addChartData(new ChartData("Elemental",types._2,UICodeConfig.elementalColor))
          new TreeNode(new ChartData("Elemental", types._2, UICodeConfig.elementalColor), tiles.damageTakenDtpstree);
        }
        case "No Type" =>
        case x => {
          println(s"Got Unknown Damage type: ${x}")
          tiles.damageTakenDamageFromTypeIndicator.addChartData(new ChartData("Regular",types._2,UICodeConfig.regularColor))
          new TreeNode(new ChartData("Regular", types._2, UICodeConfig.regularColor), tiles.damageTakenDtpstree);
        }
      }
    }

    /**
     * Damage Taken Tab from source tile ability data
     */

    for (types <- controller.getCurrentCombat().getPlayerInCombatActor().getDamageTakenStats()) {
      types._1 match {
        case "internal" => {
          for (ability <- types._2) {
            new TreeNode(new ChartData(ability._1, ability._2, UICodeConfig.internalColor), getCorrectChild("Internal","dtps",tiles));
          }
        }
        case "kinetic" => {
          for (ability <- types._2) {
            new TreeNode(new ChartData(ability._1, ability._2, UICodeConfig.kineticColor), getCorrectChild("Kinetic","dtps",tiles));
          }
        }
        case "energy" => {
          for (ability <- types._2) {
            new TreeNode(new ChartData(ability._1, ability._2, UICodeConfig.energyColor), getCorrectChild("Energy","dtps",tiles));
          }
        }
        case "elemental" => {
          for (ability <- types._2) {
            new TreeNode(new ChartData(ability._1, ability._2, UICodeConfig.elementalColor), getCorrectChild("Elemental","dtps",tiles));
          }
        }
        case "No Type" =>
        case x => {
          for (ability <- types._2) {
            new TreeNode(new ChartData(ability._1, ability._2, UICodeConfig.regularColor), getCorrectChild("Regular","dtps",tiles));
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
  def getCorrectChild(name : String, from : String,tiles: GuiTiles): TreeNode[ChartData] = {
    if (from == "dtps") {
      val root : java.util.List[TreeNode[ChartData]] = tiles.overviewDtpstree.getAll
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
    tiles.overviewDtpstree
  }

  def updateLeaderBoard(controller: Controller, tiles: GuiTiles) = {
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
      tiles.leaderBoardItems.get(index).setValue(combatInstanceActor.getDamagePerSecond())
      tiles.leaderBoardItems.get(index).setName(combatInstanceActor.getActor().getName())
      tiles.leaderBoardItems.get(index).getChartData.setName(combatInstanceActor.getActor().getName())
      tiles.leaderBoardItems.get(index).setVisible(true)
    }
  }


  def updateOverlays(controller: Controller, tiles: GuiTiles): Unit = {

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

    Overlays.personalDamageOverlay.setTitle(s"Dps: ${controller.getCurrentCombat().getPlayerInCombatActor().getDamagePerSecond()}")
    for (damageTypeDone <- controller.getCurrentCombat().getPlayerInCombatActor().getDamageDone1DStats()) {
      for (damageSource <- damageTypeDone._2.keys) {
        val value = controller.getCurrentCombat().getPlayerInCombatActor().getDamageDone1DStats().get("").get(damageSource)
        Overlays.personalDamageOverlay.addChartData(new ChartData(damageSource,value,UICodeConfig.randomColor()))
      }
    }
//    Overlays.personalDamageOverlay.clearChartData()
//    Overlays.personalDamageOverlay.setTitle(s"DPS: ${controller.getCurrentCombat().getPlayerInCombatActor().getDamagePerSecond()}")
//    for (damageTypeDone <- controller.getCurrentCombat().getPlayerInCombatActor().getDamageTypeDone()) {
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

    Overlays.personalHealingOverlay.setTitle(s"Hps: ${controller.getCurrentCombat().getPlayerInCombatActor().getHealingDonePerSecond()}")
    for (healingTypeDone <- controller.getCurrentCombat().getPlayerInCombatActor().getHealingDoneStats()) {
      for (healSource <- healingTypeDone._2.keys) {
        val healValue = controller.getCurrentCombat().getPlayerInCombatActor().getHealingDoneStats().get("").get(healSource)
        Overlays.personalHealingOverlay.addChartData(new ChartData(healSource,healValue,UICodeConfig.randomColor()))
      }
    }


    /**
     * Update Overlay Your Damage Taken
     */

    Overlays.personalDamageTakenOverlay.setTitle(s"Dtps: ${controller.getCurrentCombat().getPlayerInCombatActor().getDamageTakenPerSecond()}")
    for (damageTypeTaken <- controller.getCurrentCombat().getPlayerInCombatActor().getDamageTaken1DStats()) {
      for (damageSource <- damageTypeTaken._2.keys) {
        val value = controller.getCurrentCombat().getPlayerInCombatActor().getDamageTaken1DStats().get("").get(damageSource)
        Overlays.personalDamageTakenOverlay.addChartData(new ChartData(damageSource,value,UICodeConfig.randomColor()))
      }
    }


    /**
     * Update Overlay Group Damage Done
     */

    // what actor has done the most damage this tick?
    var maxDamage = 1
    var totalDamage = 1
    for (actor <- controller.getCurrentCombat().getCombatActors()) {
      totalDamage = totalDamage + actor.getDamageDone()
      if (actor.getDamageDone() > maxDamage) maxDamage = actor.getDamageDone()
    }
    if (totalDamage > 1) totalDamage = totalDamage - 1

    val sortedByDamageDone = controller.getCurrentCombat().getCombatActors().sortWith(_.getDamageDone() > _.getDamageDone()).filter(_.getDamageDone() > 0)

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
    for (actor <- controller.getCurrentCombat().getCombatActors()) {
      totalHealing = totalHealing + actor.getHealingDone()
      if (actor.getHealingDone() > maxHealing) maxHealing = actor.getHealingDone()
    }
    if(totalHealing > 1) totalHealing = totalHealing - 1

    val sortedByHealingDone = controller.getCurrentCombat().getCombatActors().sortWith(_.getHealingDone() > _.getHealingDone()).filter(_.getHealingDone() > 0)

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

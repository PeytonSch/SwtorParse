package UI

import Controller.Controller
import com.typesafe.config.ConfigFactory
import eu.hansolo.tilesfx.chart.ChartData
import eu.hansolo.tilesfx.tools.TreeNode
import parsing.Actors.Player
import scalafx.event.ActionEvent
import javafx.scene.paint.Color

/**
 * Element loader is for loading data into the UI charts and graphs etc.
 * It will be called on refreshes and loading new combats
 */
class ElementLoader {

  val uiCodeConfig = new UICodeConfig

  // This can be used to generate random numbers
  val random = scala.util.Random

  /**
   * This function is called from the menu item when a new combat instance is selected.
   * It may need to change when we move changing combat instances out of the menu bar.
   */
  def combatInstanceChangeMenuAction(controller: Controller, tiles: GuiTiles): ActionEvent => Unit = (event: ActionEvent) => {
    //println(s"You clicked ${event.getTarget.asInstanceOf[javafx.scene.control.MenuItem].getText}")

    // set the current combat instance
    controller
      .setCurrentCombatInstance(controller.
        getCombatInstanceById(event.getTarget.asInstanceOf[javafx.scene.control.MenuItem].getText))

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

  }

  /**
   * This function is for updating the UI during live parsing,
   * it is called in the timer loop
   */
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



  /**
   * Main DPS Chart
   */
  def updateMainDpsChart(controller: Controller,tiles: GuiTiles) = {
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
          new TreeNode(new ChartData("Internal", types._2, uiCodeConfig.internalColor), tiles.damageDoneTree);
        }
        case "kinetic" => {
          new TreeNode(new ChartData("Kinetic", types._2, uiCodeConfig.kineticColor), tiles.damageDoneTree);
        }
        case "energy" => {
          new TreeNode(new ChartData("Energy", types._2, uiCodeConfig.energyColor), tiles.damageDoneTree);
        }
        case "elemental" => {
          new TreeNode(new ChartData("Elemental", types._2, uiCodeConfig.elementalColor), tiles.damageDoneTree);
        }
        case "No Type" =>
        case x => {
          println(s"Got Unknown Damage type: ${x}")
          new TreeNode(new ChartData("Regular", types._2, uiCodeConfig.regularColor), tiles.damageDoneTree);
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
            new TreeNode(new ChartData(ability._1, ability._2, uiCodeConfig.internalColor), getCorrectChild("Internal","dps",tiles));
          }
        }
        case "kinetic" => {
          for (ability <- types._2) {
            new TreeNode(new ChartData(ability._1, ability._2, uiCodeConfig.kineticColor), getCorrectChild("Kinetic","dps",tiles));
          }
        }
        case "energy" => {
          for (ability <- types._2) {
            new TreeNode(new ChartData(ability._1, ability._2, uiCodeConfig.energyColor), getCorrectChild("Energy","dps",tiles));
          }
        }
        case "elemental" => {
          for (ability <- types._2) {
            new TreeNode(new ChartData(ability._1, ability._2, uiCodeConfig.elementalColor), getCorrectChild("Elemental","dps",tiles));
          }
        }
        case "No Type" =>
        case x => {
          for (ability <- types._2) {
            new TreeNode(new ChartData(ability._1, ability._2, uiCodeConfig.regularColor), getCorrectChild("Regular","dps",tiles));
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
    tiles.dtpstree.removeAllNodes()
    tiles.damageFromTypeIndicator.clearChartData()

    for (types <- controller.getCurrentCombat().getPlayerInCombatActor().getDamageTypeTaken()) {
      // TODO: Need to make sure you have ALL the damage types here or they wont show
      types._1 match {
        case "internal" => {
          tiles.damageFromTypeIndicator.addChartData(new ChartData("Internal",types._2,uiCodeConfig.internalColor))
          new TreeNode(new ChartData("Internal", types._2, uiCodeConfig.internalColor), tiles.dtpstree);
        }
        case "kinetic" => {
          tiles.damageFromTypeIndicator.addChartData(new ChartData("Kinetic",types._2,uiCodeConfig.kineticColor))
          new TreeNode(new ChartData("Kinetic", types._2, uiCodeConfig.kineticColor), tiles.dtpstree);
        }
        case "energy" => {
          tiles.damageFromTypeIndicator.addChartData(new ChartData("Energy",types._2,uiCodeConfig.energyColor))
          new TreeNode(new ChartData("Energy", types._2, uiCodeConfig.energyColor), tiles.dtpstree);
        }
        case "elemental" => {
          tiles.damageFromTypeIndicator.addChartData(new ChartData("Elemental",types._2,uiCodeConfig.elementalColor))
          new TreeNode(new ChartData("Elemental", types._2, uiCodeConfig.elementalColor), tiles.dtpstree);
        }
        case "No Type" =>
        case x => {
          println(s"Got Unknown Damage type: ${x}")
          tiles.damageFromTypeIndicator.addChartData(new ChartData("Regular",types._2,uiCodeConfig.regularColor))
          new TreeNode(new ChartData("Regular", types._2, uiCodeConfig.regularColor), tiles.dtpstree);
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
            new TreeNode(new ChartData(ability._1, ability._2, uiCodeConfig.internalColor), getCorrectChild("Internal","dtps",tiles));
          }
        }
        case "kinetic" => {
          for (ability <- types._2) {
            new TreeNode(new ChartData(ability._1, ability._2, uiCodeConfig.kineticColor), getCorrectChild("Kinetic","dtps",tiles));
          }
        }
        case "energy" => {
          for (ability <- types._2) {
            new TreeNode(new ChartData(ability._1, ability._2, uiCodeConfig.energyColor), getCorrectChild("Energy","dtps",tiles));
          }
        }
        case "elemental" => {
          for (ability <- types._2) {
            new TreeNode(new ChartData(ability._1, ability._2, uiCodeConfig.elementalColor), getCorrectChild("Elemental","dtps",tiles));
          }
        }
        case "No Type" =>
        case x => {
          for (ability <- types._2) {
            new TreeNode(new ChartData(ability._1, ability._2, uiCodeConfig.regularColor), getCorrectChild("Regular","dtps",tiles));
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
  def getCorrectChild(name : String, from : String,tiles: GuiTiles): TreeNode[ChartData] = {
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
      tiles.leaderBoardItems.get(index).setValue(combatInstanceActor.getDamageDone())
      tiles.leaderBoardItems.get(index).setName(combatInstanceActor.getActor().getName())
      tiles.leaderBoardItems.get(index).getChartData.setName(combatInstanceActor.getActor().getName())
      tiles.leaderBoardItems.get(index).setVisible(true)
    }
  }



}

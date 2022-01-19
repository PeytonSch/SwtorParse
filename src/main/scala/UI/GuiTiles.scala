package UI

import eu.hansolo.tilesfx.Tile.SkinType
import eu.hansolo.tilesfx.addons.Indicator
import eu.hansolo.tilesfx.chart.SunburstChart.TextOrientation
import eu.hansolo.tilesfx.chart.{ChartData, RadarChartMode}
import eu.hansolo.tilesfx.skins.{BarChartItem, LeaderBoardItem}
import eu.hansolo.tilesfx.tools.{Helper, TreeNode}
import eu.hansolo.tilesfx.{Tile, TileBuilder}
import javafx.scene.paint.Stop
import scalafx.collections.ObservableBuffer
import scalafx.scene.chart._
import scalafx.scene.layout.StackPane
import scalafx.scene.paint.Color

import scala.util.Random

/**
 * This UI.GuiTiles Class handles most of the elements in the GUI.
 * Right now it has some random data in it. We will have to figure
 * out how to go about updating this etc.
 */

class GuiTiles {
  val     TILE_WIDTH : Double  = 250
  val     TILE_HEIGHT : Double = 350
  val     menuTileSize : Double = .30

  val random = new Random()

  /** These indicators are for the status tile */
  val leftGraphics : Indicator = new Indicator(Tile.RED);
  leftGraphics.setOn(true)

  val middleGraphics : Indicator = new Indicator(Tile.YELLOW);
  middleGraphics.setOn(true)

  val rightGraphics : Indicator = new Indicator(Tile.GREEN);
  rightGraphics.setOn(true)

  /** These values are for the leaderboard */
  var leaderBoardItems : java.util.List[LeaderBoardItem] = new java.util.ArrayList[LeaderBoardItem]
  val leaderBoardItem1 = new LeaderBoardItem("Xan", 47)
  val leaderBoardItem2 = new LeaderBoardItem("Isaac", 43)
  val leaderBoardItem3 = new LeaderBoardItem("Tsou", 12)
  val leaderBoardItem4 = new LeaderBoardItem("Chatoz", 8)

  for (i <- 0 to 23) {
    leaderBoardItems.add(new LeaderBoardItem("Start",0))
    leaderBoardItems.get(i).setVisible(false)
  }


  /** Chart Data for the percentiles polygon radar chart
   * This will show what percentile you performed in compared
   * to other players
   * */
  val percentileDps = new ChartData("DPS", 24.0, Tile.GREEN);
  val percentileHps = new ChartData("HPS", 10.0, Tile.BLUE);
  val percentileDtps = new ChartData("DTPS", 12.0, Tile.RED);
  val percentileHtps = new ChartData("HTPS", 13.0, Tile.YELLOW_ORANGE);
  val percentileThreat = new ChartData("THREAT", 13.0, Tile.BLUE);
  val percentileCrit = new ChartData("CRIT", 13.0, Tile.BLUE);
  val percentileApm = new ChartData("APM", 13.0, Tile.BLUE);
  val percentileTime = new ChartData("TIME", 13.0, Tile.BLUE);

  /** Chart Data for the bar chart stats
   * This will be updated to show your personal stats
   * It needs to stop moving though
   * */
  val personalStatsDps = new BarChartItem("DPS", 47, Tile.RED);
  val personalStatsHps = new BarChartItem("HPS", 43, Tile.GREEN);
  val personalStatsThreat = new BarChartItem("THREAT", 12, Tile.YELLOW);
  val personalStatsDtps = new BarChartItem("DTPS", 8, Tile.RED);
  val personalStatsHtps = new BarChartItem("HTPS", 47, Tile.GREEN);
  val personalStatsApm = new BarChartItem("APM", 43, Tile.YELLOW);
  val personalStatsCrit = new BarChartItem("CRIT", 12, Tile.ORANGE);
  val personalStatsTime = new BarChartItem("TIME", 8, Tile.ORANGE);

  /** Sunburst Tile (Fancy Pie Charts 1 and 2) Data
   * This will be updated to represent damage taken and damage done
   * by sources
   * */
  val dtpstree = new TreeNode(new ChartData("ROOT"));
  val  dtpsTreeStart1  = new TreeNode(new ChartData("Damage Taken 1", 1, Tile.BLUE), dtpstree);
  val  dtpsTreeStart2  = new TreeNode(new ChartData("Damage Taken 2", 1, Tile.RED), dtpstree);
  val  dtpsTreeOuter1 = new TreeNode(new ChartData("Damaging Ability", 1), dtpsTreeStart1);
  val  dtpsTreeOuter2 = new TreeNode(new ChartData("Damaging Ability 2", 1), dtpsTreeStart2);

  // Sunburst Tile 2
  val  damageDoneTree   = new TreeNode(new ChartData("ROOT"));
  val  dpsTreeStart1  = new TreeNode(new ChartData("Damage Done 1", 1, Tile.BLUE), damageDoneTree);
  val  dpsTreeStart2  = new TreeNode(new ChartData("Damage Done 2", 1, Tile.RED), damageDoneTree);
  val  dpsTreeOuter1 = new TreeNode(new ChartData("Damaging Ability", 1), dpsTreeStart1);
  val  dpsTreeOuter2 = new TreeNode(new ChartData("Damaging Ability 2", 1), dpsTreeStart2);


  val statusTile = TileBuilder.create()
    .skinType(SkinType.STATUS)
    .prefSize(TILE_WIDTH*menuTileSize, TILE_HEIGHT*menuTileSize)
    .title("Status Tile")
    .description("parser.Parser Status")
    .leftText("Cannot Find Log")
    .middleText("Parsing, No Raid Connected")
    .rightText("Log Loaded, Raid Team Connected")
    .leftGraphics(leftGraphics)
    .middleGraphics(middleGraphics)
    .rightGraphics(rightGraphics)
    .text("Log file loaded: /User/Documents/swtor/logfile")
    .build()

  val switchTile = TileBuilder.create()
    .skinType(SkinType.SWITCH)
    .prefSize(TILE_WIDTH*menuTileSize, TILE_HEIGHT*menuTileSize)
    .title("RAID")
    .text("Raid Team Connected: wtb-fill")
    //.description("Test")
    .build()

  var leaderBoardTile = TileBuilder.create()
    .skinType(SkinType.LEADER_BOARD)
    .prefSize(TILE_WIDTH, TILE_HEIGHT)
    .title("Raid LeaderBoard")
    .text("If you're not first you're last")
    .leaderBoardItems(leaderBoardItems)
//    .leaderBoardItems(leaderBoardItem1, leaderBoardItem2, leaderBoardItem3, leaderBoardItem4)
    .build();

//    val timelineTile = TileBuilder.create()
//    .skinType(SkinType.TIMELINE)
//    .prefSize(TILE_WIDTH * 4, TILE_HEIGHT)
//      //.maxTimePeriod(Duration.ofSeconds(10))
//      .value(0)
//    .title("Damage Per Second")
//    .unit("dps")
//    .minValue(0)
//    .maxValue(350)
//    .smoothing(false)
//    .lowerThreshold(70)
//    .lowerThresholdColor(Helper.getColorWithOpacity(Tile.RED, 0.0))
//    .threshold(240)
//    .thresholdColor(Helper.getColorWithOpacity(Tile.RED, 0.0))
//    .thresholdVisible(true)
//    .tickLabelColor(Helper.getColorWithOpacity(Tile.FOREGROUND, 0.5))
//    .highlightSections(true)
//    .sectionsVisible(true)
//    .timePeriod(java.time.Duration.ofMinutes(1))
//    .numberOfValuesForTrendCalculation(5)
//    .trendVisible(false)
//    .maxTimePeriod(java.time.Duration.ofSeconds(60))
//    .gradientStops(new Stop(0, Tile.RED),
//                    new Stop(0.15, Tile.RED),
//                    new Stop(0.2, Tile.YELLOW_ORANGE),
//                    new Stop(0.25, Tile.GREEN),
//                    new Stop(0.3, Tile.GREEN),
//                    new Stop(0.35, Tile.GREEN),
//                    new Stop(0.45, Tile.YELLOW_ORANGE),
//                    new Stop(0.5, Tile.ORANGE),
//                    new Stop(0.685, Tile.RED),
//                    new Stop(1.0, Tile.RED))
//    .averageVisible(false)
//    .timeoutMs(60000)
//    .build();


  val radarChartTile2 = TileBuilder.create().skinType(SkinType.RADAR_CHART)
    .prefSize(TILE_WIDTH, TILE_HEIGHT)
    .minValue(0)
    .maxValue(100)
    .title("Fight Metrics Percentiles")
    .unit("%")
    .radarChartMode(RadarChartMode.POLYGON)
    .gradientStops(new Stop(0.00000, Color.TRANSPARENT),
      new Stop(0.00001, Color.web("#3552a0")),
      new Stop(0.09090, Color.web("#456acf")),
      new Stop(0.27272, Color.web("#45a1cf")),
      new Stop(0.36363, Color.web("#30c8c9")),
      new Stop(0.45454, Color.web("#30c9af")),
      new Stop(0.50909, Color.web("#56d483")),
      new Stop(0.72727, Color.web("#9adb49")),
      new Stop(0.81818, Color.web("#efd750")),
      new Stop(0.90909, Color.web("#ef9850")),
      new Stop(1.00000, Color.web("#ef6050")))
    .text("")
    .chartData(percentileDps, percentileHps, percentileDtps, percentileHtps,
      percentileThreat, percentileCrit, percentileApm, percentileTime)
    .tooltipText("")
    .animated(true)
    .build();

  val personalStatsBarChart = TileBuilder.create()
    .skinType(SkinType.BAR_CHART)
    .prefSize(TILE_WIDTH, TILE_HEIGHT)
    .title("Personal Stats")
    .text("")
    .barChartItems(personalStatsDps, personalStatsHps, personalStatsThreat, personalStatsDtps, personalStatsHtps,personalStatsApm,
      personalStatsCrit,personalStatsTime)
    .decimals(0)
    .build();

  val damageTakenSourceTile = TileBuilder.create().skinType(SkinType.SUNBURST)
    .prefSize(TILE_WIDTH*2, TILE_HEIGHT)
    .title("Sources: Damage Taken")
    .textVisible(true)
    .sunburstTree(dtpstree)
    .sunburstBackgroundColor(Tile.BACKGROUND)
    .sunburstTextColor(Tile.BACKGROUND)
    .sunburstUseColorFromParent(true)
    .sunburstTextOrientation(TextOrientation.TANGENT)
    .sunburstAutoTextColor(false)
    .sunburstUseChartDataTextColor(false)
    .sunburstInteractive(true)
    .build();

  val damageDoneSourceTile = TileBuilder.create().skinType(SkinType.SUNBURST)
    .prefSize(TILE_WIDTH*2, TILE_HEIGHT)
    .title("Sources: Damage Done")
    .textVisible(true)
    .sunburstTree(damageDoneTree)
    .sunburstBackgroundColor(Tile.BACKGROUND)
    .sunburstTextColor(Tile.BACKGROUND)
    .sunburstUseColorFromParent(true)
    .sunburstTextOrientation(TextOrientation.TANGENT)
    .sunburstAutoTextColor(false)
    .sunburstUseChartDataTextColor(false)
    .sunburstInteractive(true)
    .build();

  val damageFromTypeIndicator = TileBuilder.create()
    .skinType(SkinType.DONUT_CHART)
    .prefSize(TILE_WIDTH, TILE_HEIGHT)
    .title("Fight Damage Types")
    .text("% of damage taken from different types")
    .textVisible(true)
    .sectionTextVisible(true)
    .build();

//  val xAxis = NumberAxis("Combat Time")
//  val xAxis = NumberAxis("Values for X-Axis", 0, 3, 10)
  val yAxis = NumberAxis("Damage & Damage/Sec")
  yAxis.setAutoRanging(false)
  yAxis.setTickUnit(2000)
  yAxis.setLowerBound(0)
  val xAxis : CategoryAxis = CategoryAxis("Combat Time")

  // Helper function to convert a tuple to `XYChart.Data`
  val toNumberChartData = (xy: (Int, Int)) => XYChart.Data[Number, Number](xy._1, xy._2)
  val toCatagoryChartData = (xy: (String, Int)) => XYChart.Data[String, Number](xy._1, xy._2)

  val lineChartSeries = new XYChart.Series[String, Number] {
    name = "Series 1"
    //    data = Seq(
    //      (0.0, 1.0),
    //      (1.2, 1.4),
    //      (2.2, 1.9),
    //      (2.7, 2.3),
    //      (2.9, 0.5)).map(toChartData)
    val dataSeq: Seq[(String, Int)] = for (i <- 1 to 30) yield (i.toString, random.nextInt(20))
    data = dataSeq.map(toCatagoryChartData)
  }

  val barChartSeries = new XYChart.Series[String, Number] {
    name = "Series 2"
//    data = Seq(
//      ("0", 1.6),
//      ("0.8", 0.4),
//      ("1.4", 2.9),
//      ("2.1", 1.3),
//      ("2.6", 0.9)).map(toBarChartData)
    val dataSeq : Seq[(String,Int)] = for (i <- 1 to 30) yield (i.toString,random.nextInt(20)+10)
    data = dataSeq.map(toCatagoryChartData)
  }

  val lineChart = new LineChart[String, Number](xAxis, yAxis, ObservableBuffer(lineChartSeries))
  lineChart.setAnimated(true)
  lineChart.setTitle("Damage")
  lineChart.setCreateSymbols(false)
  lineChart.setLegendVisible(false)
  lineChart.setPrefSize(750,350)
  lineChart.verticalGridLinesVisible = false
  lineChart.horizontalGridLinesVisible = false
//  lineChart.getXAxis.setStyle()

  val barChart = new BarChart[String,Number](xAxis, yAxis, ObservableBuffer(barChartSeries))
  barChart.setAnimated(false)
  barChart.setTitle("Damage")
  barChart.setLegendVisible(false)
  barChart.setPrefSize(750,350)
  barChart.verticalGridLinesVisible = false
  barChart.horizontalGridLinesVisible = false
//  barChart.getXAxis.setVisible(false)
//  barChart.getYAxis.setVisible(false)
//  barChart.getXAxis.setTickLabelsVisible(false)

  val stackedArea : StackPane = new StackPane()
  stackedArea.getChildren.addAll(barChart,lineChart)
  yAxis.setUpperBound(45)

  val stackedAreaDPSTab : StackPane = new StackPane()
  //stackedAreaDPSTab.getChildren.addAll(barChart,lineChart)









}

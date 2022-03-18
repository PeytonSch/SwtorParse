package UI

import UI.overlays.Overlays.{background, createMovableTopWithToggles}
import eu.hansolo.tilesfx.Tile.SkinType
import eu.hansolo.tilesfx.addons.Indicator
import eu.hansolo.tilesfx.chart.SunburstChart.TextOrientation
import eu.hansolo.tilesfx.chart.{ChartData, RadarChartMode}
import eu.hansolo.tilesfx.skins.{BarChartItem, LeaderBoardItem}
import eu.hansolo.tilesfx.tools.{Helper, TreeNode}
import eu.hansolo.tilesfx.{Tile, TileBuilder}
import javafx.scene.paint.Stop
import scalafx.collections.ObservableBuffer
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.chart._
import scalafx.scene.control.ScrollPane.ScrollBarPolicy
import scalafx.scene.control.{Label, Menu, MenuBar, MenuItem, ScrollPane}
import scalafx.scene.layout.GridPane.getColumnIndex
import scalafx.scene.layout.{Background, BackgroundFill, CornerRadii, GridPane, StackPane, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.Text
import scalafx.stage.{Stage, StageStyle}

import scala.util.Random

/**
 * This UI.GuiTiles Class handles most of the elements in the GUI.
 * Right now it has some random data in it. We will have to figure
 * out how to go about updating this etc.
 */

object Tiles {
  val     TILE_WIDTH : Double  = 350
  val     TILE_HEIGHT : Double = 450
  val     menuTileSize : Double = .30

  val random = new Random()

  // Things should be in dark-mode always
  val backgroundFill = new BackgroundFill(Color.web("#2a2a2a"), CornerRadii.Empty, Insets.Empty)
  val backgroundFillArray = Array(backgroundFill)
  val background = new Background(backgroundFillArray)

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
//  val personalStatsDps = new BarChartItem("DPS", 47, Tile.RED);
//  val personalStatsHps = new BarChartItem("HPS", 43, Tile.GREEN);
//  val personalStatsThreat = new BarChartItem("THREAT", 12, Tile.YELLOW);
//  val personalStatsDtps = new BarChartItem("DTPS", 8, Tile.RED);
//  val personalStatsHtps = new BarChartItem("HTPS", 47, Tile.GREEN);
//  val personalStatsApm = new BarChartItem("APM", 43, Tile.YELLOW);
//  val personalStatsCrit = new BarChartItem("CRIT", 12, Tile.ORANGE);
//  val personalStatsTime = new BarChartItem("TIME", 8, Tile.ORANGE);

  /** Sunburst Tile (Fancy Pie Charts 1 and 2) Data
   * This will be updated to represent damage taken and damage done
   * by sources
   * */
  val overviewDtpstree = new TreeNode(new ChartData("ROOT"));
  val  overviewDtpsTreeStart1  = new TreeNode(new ChartData("Damage Taken 1", 1, Tile.BLUE), overviewDtpstree);
  val  overviewDtpsTreeStart2  = new TreeNode(new ChartData("Damage Taken 2", 1, Tile.RED), overviewDtpstree);
  val  overviewDtpsTreeOuter1 = new TreeNode(new ChartData("Damaging Ability", 1), overviewDtpsTreeStart1);
  val  overviewDtpsTreeOuter2 = new TreeNode(new ChartData("Damaging Ability 2", 1), overviewDtpsTreeStart2);

  // Overview Damage Done Tile
  val  damageDoneTree   = new TreeNode(new ChartData("ROOT"));
  val  dpsTreeStart1  = new TreeNode(new ChartData("Damage Done 1", 1, Tile.BLUE), damageDoneTree);
  val  dpsTreeStart2  = new TreeNode(new ChartData("Damage Done 2", 1, Tile.RED), damageDoneTree);
  val  dpsTreeOuter1 = new TreeNode(new ChartData("Damaging Ability", 1), dpsTreeStart1);
  val  dpsTreeOuter2 = new TreeNode(new ChartData("Damaging Ability 2", 1), dpsTreeStart2);

  // Damage Taken Tab By Source
  val damageTakenDtpstree = new TreeNode(new ChartData("ROOT"));
  val  damageTakenDtpsTreeStart1  = new TreeNode(new ChartData("Damage Taken 1", 1, Tile.BLUE), damageTakenDtpstree);
  val  damageTakenDtpsTreeStart2  = new TreeNode(new ChartData("Damage Taken 2", 1, Tile.RED), damageTakenDtpstree);
  val  damageTakenDtpsTreeOuter1 = new TreeNode(new ChartData("Damaging Ability", 1), damageTakenDtpsTreeStart1);
  val  damageTakenDtpsTreeOuter2 = new TreeNode(new ChartData("Damaging Ability 2", 1), damageTakenDtpsTreeStart2);


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

  //This particular RAID switch is not currently being used in the GUI. Keeping this
  //code here as a reference in case we want to use a switch down the road.
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

//  val personalStatsBarChart = TileBuilder.create()
//    .skinType(SkinType.BAR_CHART)
//    .prefSize(TILE_WIDTH, TILE_HEIGHT)
//    .title("Personal Stats")
//    .text("")
//    .barChartItems(personalStatsDps, personalStatsHps, personalStatsThreat, personalStatsDtps, personalStatsHtps,personalStatsApm,
//      personalStatsCrit,personalStatsTime)
//    .decimals(0)
//    .build();

  val overviewDamageTakenSourceTile = TileBuilder.create().skinType(SkinType.SUNBURST)
    .prefSize(TILE_WIDTH*2, TILE_HEIGHT)
    .title("Sources: Damage Taken")
    .textVisible(true)
    .sunburstTree(overviewDtpstree)
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

  val overviewDamageFromTypeIndicator = TileBuilder.create()
    .skinType(SkinType.DONUT_CHART)
    .prefSize(TILE_WIDTH, TILE_HEIGHT)
    .title("Fight Damage Types")
    .text("% of damage taken from different types")
    .textVisible(true)
    .sectionTextVisible(true)
    .build();

//  val xAxis = NumberAxis("Combat Time")
//  val xAxis = NumberAxis("Values for X-Axis", 0, 3, 10)
  val overviewChartYAxis = NumberAxis("Damage & Damage/Sec")
  overviewChartYAxis.setAutoRanging(false)
  overviewChartYAxis.setTickUnit(2000)
  overviewChartYAxis.setLowerBound(0)
  val overviewChartXAxis : CategoryAxis = CategoryAxis("Combat Time")

  // Helper function to convert a tuple to `XYChart.Data`
  val toNumberChartData = (xy: (Int, Int)) => XYChart.Data[Number, Number](xy._1, xy._2)
  val toCatagoryChartData = (xy: (String, Int)) => XYChart.Data[String, Number](xy._1, xy._2)

  val overviewLineChartSeries = new XYChart.Series[String, Number] {
    name = "Series 1"
    val dataSeq: Seq[(String, Int)] = for (i <- 1 to 30) yield (i.toString, random.nextInt(20))
    data = dataSeq.map(toCatagoryChartData)
  }

  val overviewBarChartSeries = new XYChart.Series[String, Number] {
    name = "Series 2"
    val dataSeq : Seq[(String,Int)] = for (i <- 1 to 30) yield (i.toString,random.nextInt(20)+10)
    data = dataSeq.map(toCatagoryChartData)
  }

  val overviewLineChart = new LineChart[String, Number](overviewChartXAxis, overviewChartYAxis, ObservableBuffer(overviewLineChartSeries))
  overviewLineChart.setAnimated(true)
  overviewLineChart.setTitle("Damage")
  overviewLineChart.setCreateSymbols(false)
  overviewLineChart.setLegendVisible(false)
  overviewLineChart.setPrefSize(750,350)
  overviewLineChart.verticalGridLinesVisible = false
  overviewLineChart.horizontalGridLinesVisible = false
//  lineChart.getXAxis.setStyle()

  val overviewBarChart = new BarChart[String,Number](overviewChartXAxis, overviewChartYAxis, ObservableBuffer(overviewBarChartSeries))
  overviewBarChart.setAnimated(false)
  overviewBarChart.setTitle("Damage")
  overviewBarChart.setLegendVisible(false)
  overviewBarChart.setPrefSize(750,350)
  overviewBarChart.verticalGridLinesVisible = false
  overviewBarChart.horizontalGridLinesVisible = false
//  barChart.getXAxis.setVisible(false)
//  barChart.getYAxis.setVisible(false)
//  barChart.getXAxis.setTickLabelsVisible(false)

  val overviewStackedArea : StackPane = new StackPane()
  overviewStackedArea.getChildren.addAll(overviewBarChart,overviewLineChart)
  overviewChartYAxis.setUpperBound(45)

//  val stackedAreaDPSTab : StackPane = new StackPane()
  //stackedAreaDPSTab.getChildren.addAll(barChart,lineChart)


  val personalStatsScrollPane = new ScrollPane()
  val personalStatsGridPane = new GridPane()
  personalStatsScrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);


  /**
   * Label Data for Personal Stats. The labels can be put in a sequence to make
   * them much simpler and easier to set properties with
   * */
    val personalStatLabels: Seq[Label] = Seq(
      new Label(" DPS"),
      new Label(" Damage"),
      new Label(" HPS"),
      new Label(" Healing"),
      new Label(" Threat"),
      new Label(" Threat/sec"),
      new Label(" DTPS"),
      new Label(" TDT"),
      new Label(" HTPS"),
      new Label(" THT"),
      new Label(" APM"),
      new Label(" CRIT"),
      new Label(" TIME")
    )


  /**
   * Adding all the value labels individually lets
   * us access their names in the element loader. Even if it would
   * be nice to just throw them in a sequence
   */
  val paddedStartText : String = "0".reverse.padTo(10, ' ').reverse
  val personalStatsDpsValue = new Label(paddedStartText)
  val personalStatsTotalDamageValue = new Label(paddedStartText)
  val personalStatsHpsValue = new Label(paddedStartText)
  val personalStatsTotalHealingValue = new Label(paddedStartText)
  val personalStatsThreatValue = new Label(paddedStartText)
  val personalStatsThreatPerSecondValue = new Label(paddedStartText)
  val personalStatsDtpsValue = new Label(paddedStartText)
  val personalStatsTotalDamageTakenValue = new Label(paddedStartText)
  val personalStatsHtpsValue = new Label(paddedStartText)
  val personalStatsTotalHealingTakenValue = new Label(paddedStartText)
  val personalStatsApmValue = new Label(paddedStartText)
  val personalStatsCritValue = new Label(paddedStartText)
  val personalStatsTimeValue = new Label(paddedStartText)

  for (l <- 0 until personalStatLabels.length) {
    personalStatLabels(l).setId("personalStatslabel")
    personalStatsGridPane.add(personalStatLabels(l),0,l)

  }


  /**
   * Set IDs for css styling
   */
  personalStatsDpsValue.setId("personalStatsValueLabel")
  personalStatsTotalDamageValue.setId("personalStatsValueLabel")
  personalStatsTotalHealingValue.setId("personalStatsValueLabel")
  personalStatsHpsValue.setId("personalStatsValueLabel")
  personalStatsThreatValue.setId("personalStatsValueLabel")
  personalStatsThreatPerSecondValue.setId("personalStatsValueLabel")
  personalStatsDtpsValue.setId("personalStatsValueLabel")
  personalStatsTotalDamageTakenValue.setId("personalStatsValueLabel")
  personalStatsHtpsValue.setId("personalStatsValueLabel")
  personalStatsTotalHealingTakenValue.setId("personalStatsValueLabel")
  personalStatsApmValue.setId("personalStatsValueLabel")
  personalStatsCritValue.setId("personalStatsValueLabel")
  personalStatsTimeValue.setId("personalStatsValueLabel")


  /**
   * Add to grid
   */
  personalStatsGridPane.add(personalStatsDpsValue,1,0)
  personalStatsGridPane.add(personalStatsTotalDamageValue,1,1)
  personalStatsGridPane.add(personalStatsHpsValue,1,2)
  personalStatsGridPane.add(personalStatsTotalHealingValue,1,3)
  personalStatsGridPane.add(personalStatsThreatValue,1,4)
  personalStatsGridPane.add(personalStatsThreatPerSecondValue,1,5)
  personalStatsGridPane.add(personalStatsDtpsValue,1,6)
  personalStatsGridPane.add(personalStatsTotalDamageTakenValue,1,7)
  personalStatsGridPane.add(personalStatsHtpsValue,1,8)
  personalStatsGridPane.add(personalStatsTotalHealingTakenValue,1,9)
  personalStatsGridPane.add(personalStatsApmValue,1,10)
  personalStatsGridPane.add(personalStatsCritValue,1,11)
  personalStatsGridPane.add(personalStatsTimeValue,1,12)


  /**
   * Some Container Settings
   */
  personalStatsScrollPane.setContent(personalStatsGridPane)
  personalStatsScrollPane.setBackground(background)
  personalStatsGridPane.setBackground(background)
  personalStatsScrollPane.setFitToWidth(true)
//  personalStatsScrollPane.setFitToHeight(true)
  personalStatsScrollPane.setVbarPolicy(ScrollBarPolicy.Never)
  personalStatsGridPane.setPrefWidth(200)
  personalStatsGridPane.gridLinesVisible = true
  personalStatsGridPane.setPrefWidth(200)


  /**
   * Damage Taken Tab
   */

  /**
   * To Start, we want a damage taken per second graph and bar chart, damage taken by type wheel,
   * and damage taken by ability wheel
   */

  val damageTakenGridPane = new GridPane()

  /**
   * Line and Bar Chart For Damage Taken
   */

  // Axis
  val damageTakenChartYAxis = NumberAxis("Damage Taken & DTPS")
  damageTakenChartYAxis.setAutoRanging(false)
  damageTakenChartYAxis.setTickUnit(2000)
  damageTakenChartYAxis.setLowerBound(0)
  val damageTakenChartXAxis : CategoryAxis = CategoryAxis("Combat Time")

  // Series
  val damageTakenLineChartSeries = new XYChart.Series[String, Number] {
    name = "Series 1"
    val dataSeq: Seq[(String, Int)] = for (i <- 1 to 30) yield (i.toString, random.nextInt(20))
    data = dataSeq.map(toCatagoryChartData)
  }

  val damageTakenBarChartSeries = new XYChart.Series[String, Number] {
    name = "Series 2"
    val dataSeq : Seq[(String,Int)] = for (i <- 1 to 30) yield (i.toString,random.nextInt(20)+10)
    data = dataSeq.map(toCatagoryChartData)
  }
  val damageTakenLineChart = new LineChart[String, Number](damageTakenChartXAxis, damageTakenChartYAxis, ObservableBuffer(damageTakenLineChartSeries))
  damageTakenLineChart.setAnimated(true)
  damageTakenLineChart.setTitle("Damage Taken")
  damageTakenLineChart.setCreateSymbols(false)
  damageTakenLineChart.setLegendVisible(false)
  damageTakenLineChart.setPrefSize(750,350)
  damageTakenLineChart.verticalGridLinesVisible = false
  damageTakenLineChart.horizontalGridLinesVisible = false
  damageTakenLineChart.getStyleClass.add("damageTakenClass")

  val damageTakenBarChart = new BarChart[String,Number](damageTakenChartXAxis, damageTakenChartYAxis, ObservableBuffer(damageTakenBarChartSeries))
  damageTakenBarChart.setAnimated(false)
  damageTakenBarChart.setTitle("Damage Taken")
  damageTakenBarChart.setLegendVisible(false)
  damageTakenBarChart.setPrefSize(750,350)
  damageTakenBarChart.verticalGridLinesVisible = false
  damageTakenBarChart.horizontalGridLinesVisible = false


  val damageTakenStackedArea : StackPane = new StackPane()
  damageTakenStackedArea.getChildren.addAll(damageTakenBarChart,damageTakenLineChart)
  damageTakenChartYAxis.setUpperBound(45)

  damageTakenStackedArea.setBackground(background)


  val damageTakenDamageFromTypeIndicator = TileBuilder.create()
    .skinType(SkinType.DONUT_CHART)
    .prefSize(TILE_WIDTH, TILE_HEIGHT)
    .title("Fight Damage Types")
    .text("% of damage taken from different types")
    .textVisible(true)
    .sectionTextVisible(true)
    .build();


  val damageTakenDamageTakenSourceTile = TileBuilder.create().skinType(SkinType.SUNBURST)
    .prefSize(TILE_WIDTH*2, TILE_HEIGHT)
    .title("Sources: Damage Taken")
    .textVisible(true)
    .sunburstTree(damageTakenDtpstree)
    .sunburstBackgroundColor(Tile.BACKGROUND)
    .sunburstTextColor(Tile.BACKGROUND)
    .sunburstUseColorFromParent(true)
    .sunburstTextOrientation(TextOrientation.TANGENT)
    .sunburstAutoTextColor(false)
    .sunburstUseChartDataTextColor(false)
    .sunburstInteractive(true)
    .build();


  /**
   * Add To Grid Pane
   */

  damageTakenGridPane.add(damageTakenStackedArea,0,0,2,1)
  damageTakenGridPane.add(damageTakenDamageTakenSourceTile,0,1,1,1)
  damageTakenGridPane.add(damageTakenDamageFromTypeIndicator,1,1,1,1)


  /**
   * Custom Leaderboard is a VBOX with stacked damage and heal leaderboards
   */


  val dpsLeaderboardOuter = new VBox()
  dpsLeaderboardOuter.setBackground(Tiles.background)
  val dpsLeaderBoardPane = new VBox()
  val dpsLeaderboardScrollPane = new ScrollPane()

  dpsLeaderBoardPane.setBackground(Tiles.background)

  dpsLeaderboardScrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
  dpsLeaderboardScrollPane.setBackground(Tiles.background)

  dpsLeaderboardOuter.getChildren.addAll(dpsLeaderboardScrollPane)

//  // this populates some test data on start
//    for (i <- Range(0,16)) {
//      val stacked = new StackPane()
//      val text = new Text()
//      text.setText("This is my text!!")
//      val rect = Rectangle((i-8)*(-25),30)
//      val backgroundRect = Rectangle(400, 30)
//      backgroundRect.setStyle("-fx-fill: #FF908D; -fx-stroke: black; -fx-stroke-width: 2;")
//      rect.setStyle("-fx-fill: #FF3633; -fx-stroke: black; -fx-stroke-width: 2;");
//      stacked.getChildren.addAll(backgroundRect,rect,text)
//      stacked.setAlignment(Pos.CenterLeft)
//      dpsLeaderBoardPane.getChildren.add(stacked)
//    }

  dpsLeaderBoardPane.setBackground(background)
  dpsLeaderboardScrollPane.setContent(dpsLeaderBoardPane)


  val hpsLeaderboardOuter = new VBox()
  hpsLeaderboardOuter.setBackground(Tiles.background)
  val hpsLeaderBoardPane = new VBox()
  val hpsLeaderboardScrollPane = new ScrollPane()

  hpsLeaderBoardPane.setBackground(Tiles.background)

  hpsLeaderboardScrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
  hpsLeaderboardScrollPane.setBackground(Tiles.background)

  hpsLeaderboardOuter.getChildren.addAll(hpsLeaderboardScrollPane)

  // this populates some test data on start
//  for (i <- Range(0,16)) {
//    val stacked = new StackPane()
//    val text = new Text()
//    text.setText("This is my text!!")
//    val rect = Rectangle((i-8)*(-25),30)
//    val backgroundRect = Rectangle(400, 30)
//    backgroundRect.setStyle("-fx-fill: #48FF80; -fx-stroke: black; -fx-stroke-width: 2;")
//    rect.setStyle("-fx-fill: #5CFF47; -fx-stroke: black; -fx-stroke-width: 2;");
//    stacked.getChildren.addAll(backgroundRect,rect,text)
//    stacked.setAlignment(Pos.CenterLeft)
//    hpsLeaderBoardPane.getChildren.add(stacked)
//  }

  hpsLeaderBoardPane.setBackground(background)
  hpsLeaderboardScrollPane.setContent(hpsLeaderBoardPane)

  val leaderBoardStacked = new VBox()
  leaderBoardStacked.setBackground(Tiles.background)
  val damageLabel = new Label("Group Damage Leader Board")
  val healingLabel = new Label("Group Healing Leader Board")
  leaderBoardStacked.getChildren.addAll(damageLabel,dpsLeaderboardScrollPane,healingLabel,hpsLeaderboardScrollPane)


  /**
   * Perspective Drop Down Menu
   */


  // recent dir menus
  var actorMenuItems: List[MenuItem] = List(
    new MenuItem("No Combat Selected")
  )

  //Make all the menus
  val actorMenu = new Menu("Select Perspective")
  actorMenu.items = actorMenuItems

  //Create blank menubar
  val actorMenuBar = new MenuBar()

  //add the menus to the menubar
  actorMenuBar.getMenus().addAll(actorMenu)








}

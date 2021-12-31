import eu.hansolo.tilesfx.Tile.SkinType
import eu.hansolo.tilesfx.{Tile, TileBuilder}
import eu.hansolo.tilesfx.addons.Indicator
import eu.hansolo.tilesfx.chart.SunburstChart.TextOrientation
import eu.hansolo.tilesfx.chart.{ChartData, RadarChartMode}
import eu.hansolo.tilesfx.skins.{BarChartItem, LeaderBoardItem}
import eu.hansolo.tilesfx.tools.TreeNode
import scalafx.geometry.Pos
import javafx.scene.paint.Stop
import scalafx.scene.paint.Color

/**
 * This GuiTiles Class handles most of the elements in the GUI.
 * Right now it has some random data in it. We will have to figure
 * out how to go about updating this etc.
 */

class GuiTiles {
  val     TILE_WIDTH : Double  = 250
  val     TILE_HEIGHT : Double = 350
  val     menuTileSize : Double = .30

  /** These indicators are for the status tile */
  val leftGraphics : Indicator = new Indicator(Tile.RED);
  leftGraphics.setOn(true)

  val middleGraphics : Indicator = new Indicator(Tile.YELLOW);
  middleGraphics.setOn(true)

  val rightGraphics : Indicator = new Indicator(Tile.GREEN);
  rightGraphics.setOn(true)

  /** These values are for the leaderboard */
  val leaderBoardItem1 = new LeaderBoardItem("Xan", 47)
  val leaderBoardItem2 = new LeaderBoardItem("Isaac", 43)
  val leaderBoardItem3 = new LeaderBoardItem("Tsou", 12)
  val leaderBoardItem4 = new LeaderBoardItem("Chatoz", 8)

  /** Chart Data for the percentiles polygon radar chart
   * This will show what percentile you performed in compared
   * to other players
   * */
  val chartData1 = new ChartData("DPS", 24.0, Tile.GREEN);
  val chartData2 = new ChartData("HPS", 10.0, Tile.BLUE);
  val chartData3 = new ChartData("Threat", 12.0, Tile.RED);
  val chartData4 = new ChartData("DTPS", 13.0, Tile.YELLOW_ORANGE);
  val chartData5 = new ChartData("HTPS", 13.0, Tile.BLUE);
  val chartData6 = new ChartData("APM", 13.0, Tile.BLUE);
  val chartData7 = new ChartData("CRIT", 13.0, Tile.BLUE);
  val chartData8 = new ChartData("TIME", 13.0, Tile.BLUE);

  /** Chart Data for the bar chart stats
   * This will be updated to show your personal stats
   * It needs to stop moving though
   * */
  val barChartItem1 = new BarChartItem("DPS", 47, Tile.RED);
  val barChartItem2 = new BarChartItem("HPS", 43, Tile.GREEN);
  val barChartItem3 = new BarChartItem("THREAT", 12, Tile.YELLOW);
  val barChartItem4 = new BarChartItem("DTPS", 8, Tile.RED);
  val barChartItem5 = new BarChartItem("HTPS", 47, Tile.GREEN);
  val barChartItem6 = new BarChartItem("APM", 43, Tile.YELLOW);
  val barChartItem7 = new BarChartItem("CRIT", 12, Tile.ORANGE);
  val barChartItem8 = new BarChartItem("TIME", 8, Tile.ORANGE);
  
  /** Sunburst Tile (Fancy Pie Charts 1 and 2) Data
   * This will be updated to represent damage taken and damage done
   * by sources
   * */
  val dtpstree = new TreeNode(new ChartData("ROOT"));
  val  dtpsfirst  = new TreeNode(new ChartData("Physical", 8.3, Tile.BLUE), dtpstree);
  val  dtpssecond = new TreeNode(new ChartData("Force", 2.2, Tile.ORANGE), dtpstree);
  val  dtpsthird  = new TreeNode(new ChartData("Tech", 1.4, Tile.PINK), dtpstree);
  val  dtpsfourth = new TreeNode(new ChartData("True", 1.2, Tile.LIGHT_GREEN), dtpstree);

  val  dtpsjan = new TreeNode(new ChartData("Saber Strike", 3.5), dtpsfirst);
  val  dtpsfeb = new TreeNode(new ChartData("Brontes Beat Down", 3.1), dtpsfirst);
  val  dtpsmar = new TreeNode(new ChartData("Styrak Super Slap", 1.7), dtpsfirst);
  val  dtpsapr = new TreeNode(new ChartData("Thundering Blast", 1.1), dtpssecond);
  val  dtpsmay = new TreeNode(new ChartData("Chain Lightning", 0.8), dtpssecond);
  val  dtpsjun = new TreeNode(new ChartData("Shock", 0.3), dtpssecond);
  val  dtpsjul = new TreeNode(new ChartData("Backstab", 0.7), dtpsthird);
  val  dtpsaug = new TreeNode(new ChartData("Explosive Probe", 0.6), dtpsthird);
  val  dtpssep = new TreeNode(new ChartData("Mine", 0.1), dtpsthird);
  val  dtpsoct = new TreeNode(new ChartData("Fall Damage", 0.5), dtpsfourth);
  val  dtpsnov = new TreeNode(new ChartData("Death Mark", 0.4), dtpsfourth);
  val  dtpsdec = new TreeNode(new ChartData("Roasted by Chatoz", 0.3), dtpsfourth);

  // Sunburst Tile 2
  val  tree   = new TreeNode(new ChartData("ROOT"));
  val  first  = new TreeNode(new ChartData("Virulence", 8.3, Tile.BLUE), tree);
  val  second = new TreeNode(new ChartData("Sniper Base Class", 2.2, Tile.ORANGE), tree);
  val  third  = new TreeNode(new ChartData("Reflected", 1.4, Tile.PINK), tree);
  val  fourth = new TreeNode(new ChartData("Misc Buff Damage", 1.2, Tile.LIGHT_GREEN), tree);

  val  jan = new TreeNode(new ChartData("Cull", 3.5), first);
  val  feb = new TreeNode(new ChartData("Weakening Blast", 3.1), first);
  val  mar = new TreeNode(new ChartData("Lethal Shot", 1.7), first);
  val  apr = new TreeNode(new ChartData("Takedown", 1.1), second);
  val  may = new TreeNode(new ChartData("Corrosive Dart", 0.8), second);
  val  jun = new TreeNode(new ChartData("Orbital Strike", 0.3), second);
  val  jul = new TreeNode(new ChartData("Saber Strike", 0.7), third);
  val  aug = new TreeNode(new ChartData("Spike", 0.6), third);
  val  sep = new TreeNode(new ChartData("Shock", 0.1), third);
  val  oct = new TreeNode(new ChartData("Bloodthirst", 0.5), fourth);
  val  nov = new TreeNode(new ChartData("Crit Adrenal", 0.4), fourth);
  val  dec = new TreeNode(new ChartData("Kephess laying down like a bitch", 0.3), fourth);

  val statusTile = TileBuilder.create()
    .skinType(SkinType.STATUS)
    .prefSize(TILE_WIDTH*menuTileSize, TILE_HEIGHT*menuTileSize)
    .title("Status Tile")
    .description("Parser Status")
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

  val leaderBoardTile = TileBuilder.create()
    .skinType(SkinType.LEADER_BOARD)
    .prefSize(TILE_WIDTH, TILE_HEIGHT)
    .title("Raid LeaderBoard")
    .text("If you're not first you're last")
    .leaderBoardItems(leaderBoardItem1, leaderBoardItem2, leaderBoardItem3, leaderBoardItem4)
    .build();

  val sparkLineTile = TileBuilder.create()
    .skinType(SkinType.SPARK_LINE)
    .prefSize(TILE_WIDTH*4, TILE_HEIGHT)
    .title("Damage Per Second")
    .unit("dps")
    .gradientStops(new Stop(0, Tile.RED),
      new Stop(8000, Tile.YELLOW),
      new Stop(12000, Tile.GREEN))
    .strokeWithGradient(true)
    //.smoothing(true)
    .build();


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
    .chartData(chartData1, chartData2, chartData3, chartData4,
      chartData5, chartData6, chartData7, chartData8)
    .tooltipText("")
    .animated(true)
    .build();

  val barChartTile = TileBuilder.create()
    .skinType(SkinType.BAR_CHART)
    .prefSize(TILE_WIDTH, TILE_HEIGHT)
    .title("Personal Stats")
    .text("")
    .barChartItems(barChartItem1, barChartItem2, barChartItem3, barChartItem4, barChartItem5,barChartItem6,
      barChartItem7,barChartItem8)
    .decimals(0)
    .build();

  val sunburstTile = TileBuilder.create().skinType(SkinType.SUNBURST)
    .prefSize(TILE_WIDTH*2, TILE_HEIGHT)
    .title("Sources: Damage Taken")
    .textVisible(false)
    .sunburstTree(dtpstree)
    .sunburstBackgroundColor(Tile.BACKGROUND)
    .sunburstTextColor(Tile.BACKGROUND)
    .sunburstUseColorFromParent(true)
    .sunburstTextOrientation(TextOrientation.TANGENT)
    .sunburstAutoTextColor(true)
    .sunburstUseChartDataTextColor(true)
    .sunburstInteractive(true)
    .build();

  val sunburstTile2 = TileBuilder.create().skinType(SkinType.SUNBURST)
    .prefSize(TILE_WIDTH*2, TILE_HEIGHT)
    .title("Sources: Damage Done")
    .textVisible(false)
    .sunburstTree(tree)
    .sunburstBackgroundColor(Tile.BACKGROUND)
    .sunburstTextColor(Tile.BACKGROUND)
    .sunburstUseColorFromParent(true)
    .sunburstTextOrientation(TextOrientation.TANGENT)
    .sunburstAutoTextColor(true)
    .sunburstUseChartDataTextColor(true)
    .sunburstInteractive(true)
    .build();

  val donutChartTile = TileBuilder.create()
    .skinType(SkinType.DONUT_CHART)
    .prefSize(TILE_WIDTH, TILE_HEIGHT)
    .title("Fight Damage Types")
    .text("% of damage taken from different types")
    .textVisible(true)
    .chartData(chartData1, chartData2, chartData3, chartData4)
    .build();

}

package UI.tabs

import UI.GraphicFactory.{LineBarChartFactory, SpreadSheetFactory}
import UI.Tiles.{random, toCatagoryChartData}
import UI.tabs.DamageDone.{mainChart, mainChartColSpan, mainChartRow, pane}
import Utils.Timer
import scalafx.collections.ObservableBuffer
import scalafx.scene.chart.{BarChart, CategoryAxis, LineChart, NumberAxis, XYChart}
import scalafx.scene.layout.{GridPane, StackPane, VBox}

object HealingDone extends UITab {


  // This can be though of as like a layout
  val pane = new GridPane()

  //Some Setting variables
  val mainChartWidth = 1600
  val mainChartHeight = 600

  override def addToUI(): GridPane = pane


  // These variables are to make adjusting the grid easier
  val topRow = 0
  val mainChartRow = topRow+ 1
  val typesRow = mainChartRow + 1
//  val mainRowSpan = 2
//  val mainRow2 = mainRow1 + mainRowSpan



  /**
   * Healing Bar Chart
   */

  val mainChartAxisLabel = "Healing & Healing/Sec"
  val barStyle = "-fx-bar-fill: #48FF80;"
  val lineStyle = "-fx-stroke: #FFE00D; -fx-stroke-width: 4px;"

  val mainChart = LineBarChartFactory.create(mainChartAxisLabel,mainChartWidth,mainChartHeight, barStyle,lineStyle)



  /**
   * Spreadsheet
   */
  val spreadSheet = SpreadSheetFactory.create("Healing")

  /**
   * Add elements to Grid Pane
   */
  val mainBox = new VBox()
  mainBox.getChildren.addAll(mainChart.getStackedArea,spreadSheet.getParent)

  pane.add(mainBox,1,mainChartRow,mainChartColSpan,10)



}

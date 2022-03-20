package UI.GraphicFactory

import UI.Tiles.{random, toCatagoryChartData}
import logger.Logger
import scalafx.collections.ObservableBuffer
import scalafx.scene.Node
import scalafx.scene.chart.{BarChart, CategoryAxis, LineChart, NumberAxis, XYChart}
import scalafx.scene.layout.{GridPane, StackPane}

import scala.collection.mutable

object LineBarChartFactory {

  // Helper function to convert a tuple to `XYChart.Data`
  val toNumberChartData = (xy: (Int, Int)) => XYChart.Data[Number, Number](xy._1, xy._2)
  val toCatagoryChartData = (xy: (String, Int)) => XYChart.Data[String, Number](xy._1, xy._2)

  def create(
              yAxisLabel: String,
              prefWidth: Double,
              prefHeight: Double,
              barStyle: String,
              lineStyle: String
            ): LineBarChart = {

    val chartYAxis = NumberAxis()
    chartYAxis.setAutoRanging(false)
    chartYAxis.setTickUnit(2000)
    chartYAxis.setLowerBound(0)

    val chartXAxis: CategoryAxis = CategoryAxis("Combat Time")


    val chartLineSeries = new XYChart.Series[String, Number] {
      name = "Series 1"
      val dataSeq: Seq[(String, Int)] = for (i <- 1 to 30) yield (i.toString, random.nextInt(20))
      data = dataSeq.map(toCatagoryChartData)
    }

    val chartBarSeries = new XYChart.Series[String, Number] {
      name = "Series 2"
      val dataSeq: Seq[(String, Int)] = for (i <- 1 to 30) yield (i.toString, random.nextInt(20) + 10)
      data = dataSeq.map(toCatagoryChartData)
    }

    chartYAxis.setUpperBound(45)

    val lineChart = new LineChart[String, Number](chartXAxis, chartYAxis, ObservableBuffer(chartLineSeries))
    val barChart = new BarChart[String, Number](chartXAxis, chartYAxis, ObservableBuffer(chartBarSeries))

    lineChart.setAnimated(true)
    lineChart.setTitle("Healing")
    lineChart.setCreateSymbols(false)
    lineChart.setLegendVisible(false)
    lineChart.verticalGridLinesVisible = false
    lineChart.horizontalGridLinesVisible = false

    barChart.setAnimated(false)
    barChart.setTitle("Healing")
    barChart.setLegendVisible(false)
    barChart.verticalGridLinesVisible = false
    barChart.horizontalGridLinesVisible = false





    chartYAxis.setLabel(yAxisLabel)
    lineChart.setPrefSize(prefWidth, prefHeight)
    barChart.setPrefSize(prefWidth, prefHeight)

    for (n <- lineChart.lookupAll(".default-color0.chart-series-line")) {
      n.setStyle(lineStyle)
    }
    for (n <- barChart.lookupAll(".default-color0.chart-bar")) {
      n.setStyle(barStyle);

    }

    val stackedArea: StackPane = new StackPane()
    stackedArea.getChildren.addAll(barChart, lineChart)

    new LineBarChart(yAxisLabel,stackedArea,barChart,lineChart,chartBarSeries,chartLineSeries,chartYAxis,barStyle,lineStyle)

  }

}

class LineBarChart(
                  yAxisLabel: String,
                  stackedArea: StackPane,
                  barChart: BarChart[String, Number],
                  lineChart: LineChart[String, Number],
                  barChartData: XYChart.Series[String, Number],
                  lineChartData: XYChart.Series[String, Number],
                  yAxis: NumberAxis,
                  barStyle: String,
                  lineStyle: String
                  ) {

  def getStackedArea = stackedArea

  def resetData(): Unit = {
    barChartData.getData.removeAll()
    lineChartData.getData.removeAll()
  }

  def applyStyling(): Unit = {
    for (n <- lineChart.lookupAll(".default-color0.chart-series-line")) {
      n.setStyle(lineStyle)
    }
    for (n <- barChart.lookupAll(".default-color0.chart-bar")) {
      n.setStyle(barStyle);

    }
  }

  def updateData(newBarChartTimeSeries: mutable.Map[Int,Int], newLineChartTimeSeries: mutable.Map[Int,Int])= {
    // set the data
    barChartData.data = newBarChartTimeSeries.toSeq.map(x => (x._1.toString(),x._2)).map(toCatagoryChartData)
    lineChartData.data = newLineChartTimeSeries.toSeq.map(x => (x._1.toString(),x._2)).map(toCatagoryChartData)

    // try to resize axis
    try {
      yAxis.setUpperBound(newBarChartTimeSeries.valuesIterator.max)
    }
    catch {
      case e: java.lang.UnsupportedOperationException => Logger.debug(s"No ${yAxisLabel} done, unable to perform max to set axis of chart")
      case e: Throwable => Logger.error(s"Error trying to set ${yAxisLabel} chart axis: ${e}")
    }

    applyStyling()

  }

}

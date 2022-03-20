package UI.tabs

import UI.Tiles
import UI.objects.ProgressBar.progressBar
import Utils.Config
import scalafx.scene.layout.{GridPane, Priority}

object Overview extends UITab {

  // This can be though of as like a layout
  val pane = new GridPane()

  override def addToUI(): GridPane = pane


  // These variables are to make adjusting the grid easier
  val mainMenuRow = 0
  val menuRow = mainMenuRow + 1
  val mainRow1 = menuRow + 1
  val mainRowSpan = 2
  val mainRow2 = mainRow1 + mainRowSpan




  /**
   * Here is where we add all the main tiles from the tiles manager class
   * */

  pane.add(Tiles.statusTile, 2, menuRow, 5, 1)
  pane.add(Tiles.actorMenuBar,6,menuRow,1,1)

  //Main Row 1
  pane.add(Tiles.leaderBoardStacked, 7, mainRow1, 1, mainRowSpan)
  pane.add(Tiles.overviewStackedArea, 1, mainRow1, 5, mainRowSpan)
  pane.add(Tiles.radarChartTile2, 6, mainRow1, 1, mainRowSpan)


  pane.add(Tiles.personalStatsScrollPane, 0, menuRow, 1, mainRowSpan + 1)
  //pane.add(Tiles.personalStatsBarChart, 7, mainRow1, 1, mainRowSpan + 1)

  //    //Main Row 2
  pane.add(Tiles.damageDoneSourceTile, 1, mainRow2, 3, 1)
  pane.add(Tiles.overviewDamageTakenSourceTile, 4, mainRow2, 3, 1)
  pane.add(Tiles.overviewDamageFromTypeIndicator, 7, mainRow2, 1, 1)

  // Progress bar
  pane.add(progressBar,0,mainRow2+1,7,1)

  //    dpsTab.onSelectionChanged = (v:Event) => {
  //      dpsTab.setContent(Tiles.stackedArea)
  //    }
  //    overViewTab.onSelectionChanged = (v:Event) => {
  //      overViewTab.setContent(pane)
  //    }

  pane.setHgap(5)
  pane.setVgap(5)

  // Set the preferred size of the window
  pane.setPrefSize(
    Config.config.getInt("UI.General.prefWidth"),
    Config.config.getInt("UI.General.prefHeight")
  )
  pane.setBackground(Tiles.background)
  Tiles.overviewStackedArea.setBackground(Tiles.background)

  GridPane.setHgrow(pane, Priority.ALWAYS);
  GridPane.setVgrow(pane, Priority.ALWAYS);

}

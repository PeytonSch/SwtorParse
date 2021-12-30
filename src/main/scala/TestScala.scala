import eu.hansolo.tilesfx.Tile.SkinType
import eu.hansolo.tilesfx.TileBuilder
import scalafx.animation.AnimationTimer
import scalafx.application.{AppHelper3, JFXApp3}
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.geometry.Insets
import scalafx.scene.{PerspectiveCamera, Scene}
import scalafx.scene.control.{Button, CheckBox}
import scalafx.scene.effect.DropShadow
import scalafx.scene.layout.{Background, BackgroundFill, CornerRadii, GridPane, HBox}
import scalafx.scene.paint.Color._
import scalafx.scene.paint._
import scalafx.scene.text.Text
import scalafx.stage.Stage
import scalafx.geometry.Pos

object ScalaFXHelloWorld extends JFXApp3 {




  override def start(): Unit = {

    val tiles : GuiTiles = new GuiTiles()
    var lastTimerCall = System.nanoTime()
    val random = scala.util.Random
    val parser : Parser = new Parser

    /** Everything in here is ran on the timer interval */
    val timer : AnimationTimer = AnimationTimer(t => {
      val now = System.nanoTime()
      if (now > lastTimerCall + 1_000_000_000L) {
        lastTimerCall = now

        /** Parser Items */
        val result = parser.getNextLine()
        result.getResult() match {
          case "ApplyEffect" => result.getResultType() match {
            case "Heal" => // add heal value to graph
          }
          case _ =>
        }



        /** Timer Ran Code */
        if (tiles.statusTile.getLeftValue() > 1000) { tiles.statusTile.setLeftValue(0); }
        if (tiles.statusTile.getMiddleValue() > 1000) { tiles.statusTile.setMiddleValue(0); }
        if (tiles.statusTile.getRightValue() > 1000) { tiles.statusTile.setRightValue(0); }
        tiles.statusTile.setLeftValue(tiles.statusTile.getLeftValue() + random.nextInt(4));
        tiles.statusTile.setMiddleValue(tiles.statusTile.getMiddleValue() + random.nextInt(3));
        tiles.statusTile.setRightValue(tiles.statusTile.getRightValue() + random.nextInt(3));

        tiles.leaderBoardTile.getLeaderBoardItems().get(random.nextInt(3)).setValue(random.nextDouble() * 80)

        tiles.sparkLineTile.setValue(random.nextDouble() * tiles.sparkLineTile.getRange() * 150 + tiles.sparkLineTile.getMinValue());

        /** Radar Percentiles Chart */
        tiles.chartData1.setValue(random.nextDouble() * 50);
        tiles.chartData2.setValue(random.nextDouble() * 50);
        tiles.chartData3.setValue(random.nextDouble() * 50);
        tiles.chartData4.setValue(random.nextDouble() * 50);
        tiles.chartData5.setValue(random.nextDouble() * 50);
        tiles.chartData6.setValue(random.nextDouble() * 50);
        tiles.chartData7.setValue(random.nextDouble() * 50);
        tiles.chartData8.setValue(random.nextDouble() * 50);

        /** Rightside bar chart for personal stats*/
        //tiles.barChartTile.getBarChartItems().get(random.nextInt(8)).setValue(random.nextDouble() * 800);
        //tiles.barChartTile.getBarChartItems().get(2).setValue(5000)


      }
    })



    val stage = new PrimaryStage()

    val pane = new GridPane()

    val backgroundFill = new BackgroundFill(Color.web("#2a2a2a"), CornerRadii.Empty, Insets.Empty)
    val backgroundFillArray = Array(backgroundFill)
    val background = new Background(backgroundFillArray)

    val menuRow = 0
    val mainRow1 = menuRow + 1
    val mainRowSpan = 2
    val mainRow2 = mainRow1 + mainRowSpan

    val buttonPane = new GridPane()
    buttonPane.setBackground(background)
    val button3 = new Button("Open Log  ")
    button3.setStyle("-fx-font-size: 1.5em; -fx-background-color: #6b6b6b; -fx-text-fill: white")
    val button4 = new Button(" Team 1   ")
    button4.setStyle("-fx-font-size: 1.5em; -fx-background-color: #6b6b6b; -fx-text-fill: white")
    val button5 = new Button("Settings     ")
    button5.setStyle("-fx-font-size: 1.5em; -fx-background-color: #6b6b6b; -fx-text-fill: white")
    val button6 = new Button(" Team 2   ")
    button6.setStyle("-fx-font-size: 1.5em; -fx-background-color: #6b6b6b; -fx-text-fill: white")
    val button7 = new Button("Add Team ")
    button7.setStyle("-fx-font-size: 1.5em; -fx-background-color: #6b6b6b; -fx-text-fill: white")
    val button8 = new Button("All Teams")
    button8.setStyle("-fx-font-size: 1.5em; -fx-background-color: #6b6b6b; -fx-text-fill: white")


    buttonPane.add(button3, 0, 0, 1, 1)
    buttonPane.add(button4, 1, 0, 1, 1)
    buttonPane.add(button5, 0, 1, 1, 1)
    buttonPane.add(button6, 1, 1, 1, 1)
    buttonPane.add(button7, 0, 2, 1, 1)
    buttonPane.add(button8, 1, 2, 1, 1)
    buttonPane.setHgap(5)
    buttonPane.setVgap(5)

    pane.add(buttonPane, 0, menuRow, 1, 1)

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


    pane.add(tiles.statusTile, 3, menuRow, 4, 1)
    pane.add(tiles.switchTile, 7, menuRow, 1, 1)

//    //Main Row 1
    pane.add(tiles.leaderBoardTile, 0, mainRow1, 1, mainRowSpan)
    pane.add(tiles.sparkLineTile, 1, mainRow1, 5, mainRowSpan)
    pane.add(tiles.radarChartTile2, 6, mainRow1, 1, mainRowSpan)
    pane.add(tiles.barChartTile, 7, mainRow1, 1, mainRowSpan + 1)
//
//    //Main Row 2
    pane.add(tiles.sunburstTile, 0, mainRow2, 3, 1)
    pane.add(tiles.sunburstTile2, 3, mainRow2, 3, 1)
    pane.add(tiles.donutChartTile, 6, mainRow2, 1, 1)

    pane.setHgap(5)
    pane.setVgap(5)

    pane.setPrefSize(1500, 800)
    pane.setBackground(background)

    val camera = new PerspectiveCamera()
    camera.setFieldOfView(10)

    val scene = new Scene(pane)
    scene.setCamera(camera)


    stage.setTitle("Test Title")
    stage.setScene(scene)
    stage.show()

    timer.start()
    System.out.println("Timer Started")

  }

  override def stopApp(): Unit = {
    println("Stopping App")
    //timer.stop()
  }
}
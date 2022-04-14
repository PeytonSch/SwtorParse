package UI.tabs

import UI.GraphicFactory.{TimerCategoryFactory, TimerRowFactory}
import UI.{Tiles, UICodeConfig, UIStyle}
import UI.objects.TimerSuggestionsTable
import Utils.Config.settings
import logger.Logger
import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.scene.control.ScrollPane.ScrollBarPolicy
import scalafx.scene.control._
import scalafx.scene.layout.{GridPane, HBox, VBox}
import scalafx.stage.Stage

import scala.collection.immutable.Range

object Timers extends UITab {

  // This can be though of as like a layout
  val pane = new GridPane()

  pane.setGridLinesVisible(true)

  val parent = new HBox()

  val boarderStyle = "-fx-border-color: #C4BFAE;\n" +
                      "-fx-border-insets: 5;\n" +
                      "-fx-border-width: 3;\n" +
                      "-fx-border-style: solid;\n"

  val left = new VBox()
  left.setStyle(boarderStyle)
  left.setPrefSize(UICodeConfig.existingTimerWidthHeight._1,UICodeConfig.existingTimerWidthHeight._2)
  val right = new VBox()
  right.setStyle(boarderStyle)
  right.setPrefSize(UICodeConfig.existingTimerWidthHeight._1,UICodeConfig.existingTimerWidthHeight._2)

  val rightTop = new VBox()
  rightTop.setStyle(boarderStyle)
  rightTop.setPrefSize(750,490)

  val rightBottom = new VBox()
  rightBottom.setStyle(boarderStyle)
  rightBottom.setPrefSize(750,490)

  right.getChildren.addAll(rightTop,rightBottom)

  parent.getChildren.addAll(left,right)

  pane.add(parent,0,0)

  val timerVbox = new VBox()

  val timerScrollPane = new ScrollPane{
    content = timerVbox
    hbarPolicy = ScrollBarPolicy.Never
    style = UIStyle.mainBackgroundObject
  }

  override def addToUI(): GridPane = pane


  val existingTimersLabel = new Label("Existing Timers")
  existingTimersLabel.setStyle("-fx-font-size: 20;")
  val createTimersLabel = new Label("Create Timers")
  createTimersLabel.setStyle("-fx-font-size: 20;")

  left.getChildren.add(existingTimersLabel)
  left.getChildren.addAll(timerScrollPane)


  /**
   * Create timers
   */

    val labelStyle = "-fx-font-size: 20;"

  // labels
  val nameLabel = new Label {
    text = "Name"
    prefWidth = 350
    style = labelStyle
  }
  val sourceActorLabel = new Label {
    text ="Source Actor"
    prefWidth = 350
    style = labelStyle
  }

  val sourceAbilityLabel = new Label {
    text ="Ability"
    prefWidth = 350
    style = labelStyle
  }

  val triggerOnLabel = new Label{
    text = "Trigger On"
    prefWidth = 350
    style = labelStyle
  }
  val durationLabel = new Label{
    text = "Cool Down"
    prefWidth = 350
    style = labelStyle
  }
  val repeatLabel = new Label{
    text ="Repeat"
    prefWidth = 350
    style = labelStyle
  }
  val cancelOnLabel = new Label{
    text = "Cancel On"
    prefWidth = 350
    style = labelStyle
  }
  val areaLabel = new Label {
    text ="Area"
    prefWidth = 350
    style = labelStyle
  }

  val nameText = new TextField{
    prefWidth = 450
  }
  val sourceActorText = new TextField{
    prefWidth = 450
  }
  val sourceAbilityText = new TextField{
    prefWidth = 450
  }
  val areaText = new TextField{
    prefWidth = 450
  }
//  val triggerOnText = new TextField{
//    prefWidth = 450
//  }
  val triggerOn = new ComboBox(
  Seq[String](
  "Ability Activate","Combat Start", "Actor Enter Combat"
))
  triggerOn.setPrefWidth(450)
  triggerOn.value="Ability Activate"

  val durationText = new TextField{
    prefWidth = 450
  }
  val repeatText = new TextField{
    prefWidth = 450
    text = "999"
  }
//  val cancelOnText = new TextField{
//    prefWidth = 450
//  }
val cancelOn = new ComboBox(Seq[String](
  "Ability Activate","Combat End", "Actor Death"
))
  cancelOn.setPrefWidth(450)
  cancelOn.value = "Combat End"


  val nameBox = new HBox()
  val sourceActorBox = new HBox()
  val sourceAbilityBox = new HBox()
  val triggerOnBox = new HBox()
  val durationBox = new HBox()
  val repeatBox = new HBox()
  val cancelOnBox = new HBox()
  val areaBox = new HBox()

  nameBox.getChildren.addAll(nameLabel,nameText)
  sourceActorBox.getChildren.addAll(sourceActorLabel,sourceActorText)
  sourceAbilityBox.getChildren.addAll(sourceAbilityLabel,sourceAbilityText)
  triggerOnBox.getChildren.addAll(triggerOnLabel,triggerOn)
  durationBox.getChildren.addAll(durationLabel,durationText)
  repeatBox.getChildren.addAll(repeatLabel,repeatText)
  cancelOnBox.getChildren.addAll(cancelOnLabel,cancelOn)
  areaBox.getChildren.addAll(areaLabel,areaText)

  /**
   * Save with apply button
   */

  val applyButton = new Button("Add Timer")

  applyButton.onAction = (event: ActionEvent) => {
    val name = nameText.getText
    val source = sourceActorText.getText
    val area = areaText.getText
    val ability = sourceAbilityText.getText
    val cooldown = durationText.getText.toDouble

    timerVbox.getChildren.add(
      TimerRowFactory.createRow(name,source,area,ability,cooldown).addToUI
    )

    Logger.highlight(s"Saved Timer: ${name},${source},${area},${ability},${cooldown}")
  }




  rightTop.getChildren.addAll(
    nameBox,sourceActorBox,sourceAbilityBox,triggerOnBox,
    durationBox,repeatBox,cancelOnBox,areaBox,
    applyButton
  )

  // Old suggestions, removed in favor of spreadsheet

//  val suggestions = new VBox()
//  val rightBottomScrollPane = new ScrollPane{
//    content = suggestions
//    background = Tiles.background
//  }

  val timerSuggestions = TimerSuggestionsTable.create()

  rightBottom.getChildren.addAll(timerSuggestions.getParent)


  /**
   * Left Side Timer Display
   */

  // Create Test Timers
  val testTimers = for (i <- 0 to 5) yield {
    TimerRowFactory.createRow(
      "Test Timer","Brontes","Dread Fortress","Kephess",5.0
    )
  }
  val testTimer = TimerRowFactory.createRow(
    "Test Timer","Predation Spam","Dread Fortress","Unnatural Might", 15
  )

  val testCategories = Seq(TimerCategoryFactory.create("> Dread Palace"),TimerCategoryFactory.create("> Dread Fortress"),
  TimerCategoryFactory.create("> Explosive Conflict"),TimerCategoryFactory.create("> Terror From Beyond"),
  TimerCategoryFactory.create("> Temple of Sacrifice"))

  testCategories.foreach(timer => timerVbox.getChildren.add(timer.addToUI))

  def getCategories = testCategories

//  testTimers.foreach(timer => timerVbox.getChildren.add(timer.addToUI))
//  timerVbox.getChildren.add(testTimer.addToUI)





}

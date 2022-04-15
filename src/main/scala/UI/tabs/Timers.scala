package UI.tabs

import UI.GraphicFactory.{TimerCategoryFactory, TimerRowFactory}
import UI.{Tiles, UICodeConfig, UIStyle}
import UI.objects.TimerSuggestionsTable
import Utils.Config.settings
import logger.Logger
import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.geometry.Pos
import scalafx.scene.control.ScrollPane.ScrollBarPolicy
import scalafx.scene.control._
import scalafx.scene.layout.{GridPane, HBox, VBox}
import scalafx.scene.shape.Rectangle
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

  val labelStyle = UIStyle.largeLightLabel
  val textFieldStyle = UIStyle.textFieldStyle

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
    style = textFieldStyle
  }
  val sourceActorText = new TextField{
    prefWidth = 450
    style = textFieldStyle
  }
  val sourceAbilityText = new TextField{
    prefWidth = 450
    style = textFieldStyle
  }
  val areaText = new TextField{
    prefWidth = 450
    style = textFieldStyle
  }
//  val triggerOnText = new TextField{
//    prefWidth = 450
//  }
  val triggerOn = new ComboBox(
  Seq[String](
  "Ability Activate","Combat Start - Not yet implemented", "Actor Enter Combat - Not yet implemented"
))
  triggerOn.setPrefWidth(450)
  triggerOn.value="Ability Activate"
  triggerOn.setStyle(textFieldStyle)

  val durationText = new TextField{
    prefWidth = 450
    style = textFieldStyle
  }
  val repeatText = new TextField{
    prefWidth = 450
    style = textFieldStyle
    text = "999"
  }
//  val cancelOnText = new TextField{
//    prefWidth = 450
//  }
val cancelOn = new ComboBox(Seq[String](
  "Ability Activate","Combat End - Not yet implemented", "Actor Death - Not yet implemented"
))
  cancelOn.setPrefWidth(450)
  cancelOn.value = "Combat End"
  cancelOn.setStyle(textFieldStyle)

  val colorLabel = new Label{
    text = "Color: "
    style = labelStyle

  }
  val colorPreview = new Rectangle{
    width = 50
    height = 50
    style = UIStyle.rectangleStyle("#FB4A38")
  }

  val colorOptions = new ComboBox(Seq[String](
      "Red","Orange", "Yellow", "Green", "Blue", "Purple", "Pink"
    ))
  colorOptions.value = "Red"
  colorOptions.setStyle(textFieldStyle)
  colorOptions.setPrefWidth(435)

  colorOptions.setOnAction( event =>{
    colorOptions.getValue match {
      case "Red" => colorPreview.setStyle(UIStyle.rectangleStyle("#FB4A38"))
      case "Orange" => colorPreview.setStyle(UIStyle.rectangleStyle("#EE8525"))
      case "Yellow" => colorPreview.setStyle(UIStyle.rectangleStyle("#FBE762"))
      case "Green" => colorPreview.setStyle(UIStyle.rectangleStyle("#55FB55"))
      case "Blue" => colorPreview.setStyle(UIStyle.rectangleStyle("#3F81EE"))
      case "Purple" => colorPreview.setStyle(UIStyle.rectangleStyle("#A723FF"))
      case "Pink" => colorPreview.setStyle(UIStyle.rectangleStyle("#FB55EC"))
      case _ =>
    }
  })





  val nameBox = new HBox()
  val sourceActorBox = new HBox()
  val sourceAbilityBox = new HBox()
  val triggerOnBox = new HBox()
  val durationBox = new HBox()
  val repeatBox = new HBox()
  val cancelOnBox = new HBox()
  val areaBox = new HBox()
  val color = new HBox()

  nameBox.getChildren.addAll(nameLabel,nameText)
  sourceActorBox.getChildren.addAll(sourceActorLabel,sourceActorText)
  sourceAbilityBox.getChildren.addAll(sourceAbilityLabel,sourceAbilityText)
  triggerOnBox.getChildren.addAll(triggerOnLabel,triggerOn)
  durationBox.getChildren.addAll(durationLabel,durationText)
  repeatBox.getChildren.addAll(repeatLabel,repeatText)
  cancelOnBox.getChildren.addAll(cancelOnLabel,cancelOn)
  areaBox.getChildren.addAll(areaLabel,areaText)
  color.getChildren.addAll(colorLabel,colorPreview, UIStyle.createSpacer(),colorOptions)

  /**
   * Save with apply button
   */
  val yourTimers = TimerCategoryFactory.create("> My Timers")

  val applyButton = new Button("Add Timer")

  applyButton.setStyle(UIStyle.uiButtonStyle)
  UIStyle.setHoverable(applyButton,UIStyle.uiButtonHoverStyle)

  val applyHbox = new HBox{
    children = Seq(UIStyle.createSpacer(),applyButton)
  }

  applyButton.onAction = (event: ActionEvent) => {
    val name = nameText.getText
    val source = sourceActorText.getText
    val area = areaText.getText
    val ability = sourceAbilityText.getText
    val cooldown = durationText.getText.toDouble
    val color = colorOptions.getValue



    yourTimers.addTimer(
      TimerRowFactory.createRow(name,source,area,ability,cooldown, color)
    )

    Logger.highlight(s"Saved Timer: ${name},${source},${area},${ability},${cooldown}")
  }




  rightTop.getChildren.addAll(
    nameBox,sourceActorBox,sourceAbilityBox,triggerOnBox,
    durationBox,repeatBox,cancelOnBox,areaBox,color,
    applyHbox
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

  val categories = Seq(TimerCategoryFactory.create("> Dread Palace"),TimerCategoryFactory.create("> Dread Fortress"),
  TimerCategoryFactory.create("> Explosive Conflict"),TimerCategoryFactory.create("> Terror From Beyond"),
  TimerCategoryFactory.create("> Temple of Sacrifice"),yourTimers)

  categories.foreach(timer => timerVbox.getChildren.add(timer.addToUI))

  def getCategories = categories

//  testTimers.foreach(timer => timerVbox.getChildren.add(timer.addToUI))
//  timerVbox.getChildren.add(testTimer.addToUI)





}

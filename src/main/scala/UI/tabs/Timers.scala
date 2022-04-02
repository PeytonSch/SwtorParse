package UI.tabs

import UI.Tiles
import UI.overlays.Overlays
import Utils.Config.settings
import logger.Logger
import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.scene.control._
import scalafx.scene.layout.{GridPane, HBox, VBox}
import scalafx.stage.Stage

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
  left.setPrefSize(800,1000)
  val right = new VBox()
  right.setStyle(boarderStyle)
  right.setPrefSize(800,1000)

  val rightTop = new VBox()
  rightTop.setStyle(boarderStyle)
  rightTop.setPrefSize(750,490)

  val rightBottom = new VBox()
  rightBottom.setStyle(boarderStyle)
  rightBottom.setPrefSize(750,490)

  right.getChildren.addAll(rightTop,rightBottom)

  parent.getChildren.addAll(left,right)

  pane.add(parent,0,0)


  override def addToUI(): GridPane = pane


  val existingTimersLabel = new Label("Existing Timers")
  existingTimersLabel.setStyle("-fx-font-size: 20;")
  val createTimersLabel = new Label("Create Timers")
  createTimersLabel.setStyle("-fx-font-size: 20;")

  left.getChildren.add(existingTimersLabel)


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
  val sourceLabel = new Label {
    text ="Source"
    prefWidth = 350
    style = labelStyle
  }

  val triggerOnLabel = new Label{
    text = "Trigger On"
    prefWidth = 350
    style = labelStyle
  }
  val durationLabel = new Label{
    text = "Duration"
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
  
  val nameText = new TextField{
    prefWidth = 450
  }
  val sourceText = new TextField{
    prefWidth = 450
  }
  val triggerOnText = new TextField{
    prefWidth = 450
  }
  val durationText = new TextField{
    prefWidth = 450
  }
  val repeatText = new TextField{
    prefWidth = 450
  }
  val cancelOnText = new TextField{
    prefWidth = 450
  }

  val nameBox = new HBox()
  val sourceBox = new HBox()
  val triggerOnBox = new HBox()
  val durationBox = new HBox()
  val repeatBox = new HBox()
  val cancelOnBox = new HBox()
  
  nameBox.getChildren.addAll(nameLabel,nameText)
  sourceBox.getChildren.addAll(sourceLabel,sourceText)
  triggerOnBox.getChildren.addAll(triggerOnLabel,triggerOnText)
  durationBox.getChildren.addAll(durationLabel,durationText)
  repeatBox.getChildren.addAll(repeatLabel,repeatText)
  cancelOnBox.getChildren.addAll(cancelOnLabel,cancelOnText)

  rightTop.getChildren.addAll(
    nameBox,sourceBox,triggerOnBox,
    durationBox,repeatBox,cancelOnBox
  )

  val suggestions = new VBox()
  val rightBottomScrollPane = new ScrollPane{
    content = suggestions
    background = Tiles.background
  }

  rightBottom.getChildren.addAll(rightBottomScrollPane)






}

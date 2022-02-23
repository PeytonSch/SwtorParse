package UI.objects

import scalafx.scene.layout.VBox
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.Text

object ProgressBar {

  val progressBar = new VBox()
  val progressBarRect = Rectangle(0,10)
  val progressBarText = new Text()
  progressBarText.setText("Progress: ")
  progressBarRect.setStyle("-fx-fill: #3474FF; -fx-stroke: black; -fx-stroke-width: 1;")
  progressBar.getChildren.addAll(progressBarText,progressBarRect)

}

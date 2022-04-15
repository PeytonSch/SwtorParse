package UI

import logger.Logger
import scalafx.application.JFXApp3.PrimaryStage

// I needed to create this to get access to the window position
object MainStage {

  var mainStage: PrimaryStage = null

  def getCenterOfStage(): (Double,Double) = {
    (mainStage.getX + (mainStage.getWidth / 2), mainStage.getY + (mainStage.getHeight / 2))
  }

}

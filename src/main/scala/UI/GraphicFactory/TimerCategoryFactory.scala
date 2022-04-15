package UI.GraphicFactory

import UI.overlays.OverlayUtils.anchors
import UI.tabs.{Settings, Timers}
import UI.timers.Timer
import UI.{UICodeConfig, UIStyle}
import javafx.event.EventHandler
import logger.Logger
import scalafx.geometry.{Point2D, Pos}
import scalafx.scene.control.Label
import scalafx.scene.layout.{HBox, Priority}
import scalafx.Includes._

import scala.collection.mutable

object TimerCategoryFactory {

  def create(name: String): TimerCategory = {
    val base = new HBox()
    base.setStyle(UIStyle.mainBackgroundObject)
    base.setStyle(UIStyle.smallBottomBoarder)
    base.setPrefWidth(UICodeConfig.existingTimerWidthHeight._1)
//    base.setId("hoverable")
//    UIStyle.setHoverable(base,UIStyle.mainBackgroundObjectHover + UIStyle.smallBottomBoarderHover)
    val cat = new HBox()
    cat.setStyle(UIStyle.transparentObject)
    cat.setAlignment(Pos.BaselineLeft)
    val spacer = new HBox()
    spacer.setStyle(UIStyle.transparentObject)
    spacer.hgrow = Priority.Sometimes
    val info = new HBox()
    info.setStyle(UIStyle.transparentObject)
    info.setAlignment(Pos.BaselineRight)

    val categoryName = new Label{
      text = name
      style = UIStyle.largeLightLabel
    }
    val numberOfTimersInCategory = new Label{
      text = "0/0 Active Timers"
      style = UIStyle.largeFaintBlueLabel
    }



    cat.getChildren.addAll(categoryName)
    info.getChildren.addAll(numberOfTimersInCategory)
    base.getChildren.addAll(cat,spacer,info)

    // Some Styling
    base.onMouseEntered = event => {
      base.setStyle(UIStyle.mainBackgroundObjectHover)
      base.setStyle(UIStyle.smallBottomBoarderHover)
      categoryName.setStyle(UIStyle.largeLightLabel + UIStyle.textHoverLightBlue)
    }
    base.onMouseExited = event => {
      base.setStyle(UIStyle.mainBackgroundObject)
      base.setStyle(UIStyle.smallBottomBoarder)
      categoryName.setStyle(UIStyle.largeLightLabel)
    }

    new TimerCategory(base,categoryName,numberOfTimersInCategory)
  }

}

class TimerCategory(
                   base: HBox,
                   nameLabel: Label,
                   numberOfTimersLabel: Label
                   ) {
  def addToUI = base
  def getName = nameLabel.getText

  var selected: Boolean = false

  def select() = {
    if (selected) {
      selected = false
      nameLabel.setText( '>' + nameLabel.getText.substring(1))
    } else {
      selected = true
      nameLabel.setText( 'V' + nameLabel.getText.substring(1))
    }
  }

  var timers: mutable.Seq[Timer] = mutable.Seq()

  def addTimer(t: Timer) = {
    timers = timers :+ t
    // refresh timer menu
    Timers.timerVbox.getChildren.clear()
    // populate selected categories with timers
    for (category <- Timers.getCategories) {
      Timers.timerVbox.getChildren.add(category.addToUI)
      if (category.selected) {
        category.getTimers.foreach(timer => Timers.timerVbox.getChildren.add(timer.addToUI))
      }
    }
  }

  // Create Test Timers
//  val testTimers = for (i <- 0 to 5) yield {
//    TimerRowFactory.createRow(
//      "Test Timer","Brontes","Dread Fortress","Kephess",5.0, "Red"
//    )
//  }

  base.setOnMouseClicked(new EventHandler[javafx.scene.input.MouseEvent] {
    override def handle(event: javafx.scene.input.MouseEvent): Unit = {
      select()
      Timers.timerVbox.getChildren.clear()
      // populate selected categories with timers
      for (category <- Timers.getCategories) {
        Timers.timerVbox.getChildren.add(category.addToUI)
        if (category.selected) {
          category.getTimers.foreach(timer => Timers.timerVbox.getChildren.add(timer.addToUI))
        }
      }

    }
  })

  def getTimers = timers

}

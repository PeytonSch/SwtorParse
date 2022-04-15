package UI.tabs

import UI.GraphicFactory.{LineBarChartFactory, SpreadSheetFactory}
import UI.UIStyle
import logger.Logger
import scalafx.scene.control.Label
import scalafx.scene.layout.{GridPane, HBox, Priority, VBox}
import scalafx.Includes._

object CustomTabs {

  var selectedTab: String = "Overview" // always start with overview tab

  def loadTabContent() = {
    selectedTab match {
      case "Overview" => contentBox.getChildren.clear(); contentBox.getChildren.add(Overview.addToUI())
      case "Damage Done" => contentBox.getChildren.clear(); contentBox.getChildren.add(DamageDone.addToUI())
      case "Healing Done" => contentBox.getChildren.clear(); contentBox.getChildren.add(HealingDone.addToUI())
      case "Damage Taken" => contentBox.getChildren.clear(); contentBox.getChildren.add(DamageTaken.addToUI())
      case "Healing Taken" => contentBox.getChildren.clear(); contentBox.getChildren.add(HealingTaken.addToUI())
      case "Settings" => contentBox.getChildren.clear(); contentBox.getChildren.add(Settings.addToUI())
      case "Timers" => contentBox.getChildren.clear(); contentBox.getChildren.add(Timers.addToUI())
    }
  }

  def refreshTabs() = {
    for (tab <- tabs) {
      val tabName = tab.getChildren.get(0).asInstanceOf[javafx.scene.control.Label].getText
      if (tabName == selectedTab) {
        tab.setStyle(UIStyle.customTabSelected)
        tab.getChildren.get(0).asInstanceOf[javafx.scene.control.Label].setStyle(UIStyle.tabLabelStyleSelected)
      } else {
        tab.setStyle(UIStyle.customTabUnselected)
        tab.getChildren.get(0).asInstanceOf[javafx.scene.control.Label].setStyle(UIStyle.tabLabelStyle)
      }

      tab.onMouseEntered = event => {
        if (tabName != selectedTab) {
          tab.setStyle(UIStyle.customTabHover)
          tab.getChildren.get(0).asInstanceOf[javafx.scene.control.Label].setStyle(UIStyle.tabLabelStyleSelected)
        }
      }
      tab.onMouseExited = event => {
        if (tabName != selectedTab) {
          tab.setStyle(UIStyle.customTabUnselected)
          tab.getChildren.get(0).asInstanceOf[javafx.scene.control.Label].setStyle(UIStyle.tabLabelStyle)
        }
      }
    }
  }


  def createTab(tabName: String): HBox = {
    val box = new HBox()
    box.setStyle(UIStyle.customTabUnselected)
    val label = new Label(tabName)
    label.setStyle(UIStyle.tabLabelStyle)
    val spacer = new HBox()
//    spacer.setStyle(UIStyle.transparentObject)
    spacer.hgrow = Priority.Always
    box.hgrow = Priority.Always

    box.getChildren.addAll(label,spacer)

    box.onMouseClicked = (event => {
      if (tabName != selectedTab) {
        selectedTab = tabName
        refreshTabs()
        loadTabContent()
      }
    })

    box.onMouseEntered = event => {
      if (tabName != selectedTab) {
        box.setStyle(UIStyle.customTabHover)
        box.getChildren.get(0).asInstanceOf[javafx.scene.control.Label].setStyle(UIStyle.tabLabelStyleSelected)
      }
    }
    box.onMouseExited = event => {
      if (tabName != selectedTab) {
        box.setStyle(UIStyle.customTabUnselected)
        box.getChildren.get(0).asInstanceOf[javafx.scene.control.Label].setStyle(UIStyle.tabLabelStyle)
      }
    }

    box
  }

  val parent = new VBox()
  parent.setStyle(UIStyle.mainBackgroundObject)
  parent.vgrow = Priority.Always
  parent.hgrow = Priority.Always

  val tabBox = new HBox()
  tabBox.setStyle(UIStyle.mainBackgroundObject)
  tabBox.hgrow = Priority.Always

  val contentBox = new VBox()
  contentBox.setStyle(UIStyle.mainBackgroundObject)
  contentBox.vgrow = Priority.Always
  contentBox.hgrow = Priority.Always

  val tabs = Seq(
    createTab("Overview"),
    createTab("Damage Done"),
    createTab("Healing Done"),
    createTab("Damage Taken"),
    createTab("Healing Taken"),
    createTab("Settings"),
    createTab("Timers"),
  )
  
  tabs.foreach(tab => tabBox.getChildren.add(tab))

  parent.getChildren.addAll(tabBox,contentBox)

  def addToUI = parent

  loadTabContent()
  refreshTabs()


}

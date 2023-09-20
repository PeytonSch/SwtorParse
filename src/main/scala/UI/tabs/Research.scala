package UI.tabs

import UI.UIStyle
import scalafx.scene.control.Label
import scalafx.scene.layout.{GridPane, HBox, Priority, VBox}
import scalafx.scene.web.WebView

object Research extends UITab {

  val browserBox = new VBox()


  // create browser
  val browser = new WebView()
  browser.vgrow = Priority.Always
//  browser.hgrow = Priority.Always
  val webEngine = browser.getEngine

  webEngine.load("https://github.com/PeytonSch/SwtorParse")


  var selectedTab: String = "Elite Raid Parser" // always start with erp tab

  def loadTabContent() = {
    selectedTab match {
      case "Elite Raid Parser" => webEngine.load("https://github.com/PeytonSch/SwtorParse")
      case "Google" => webEngine.load("https://google.com")
      case "Parsely" => webEngine.load("https://parsely.io/")
      case "Jedipedia" => webEngine.load("https://swtor.jedipedia.net/en")
      case "SWTOR" => webEngine.load("https://swtor.com")
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
  contentBox.getChildren.add(browser)

  def getContentSize = {
    (contentBox.getWidth,contentBox.getHeight)
  }

  val tabs = Seq(
    createTab("Elite Raid Parser"),
    createTab("Google"),
    createTab("Parsely"),
    createTab("Jedipedia"),
    createTab("SWTOR")
  )

  tabs.foreach(tab => tabBox.getChildren.add(tab))

  parent.getChildren.addAll(tabBox,contentBox)

  loadTabContent()
  refreshTabs()

//
//  browserBox.getChildren.add(browser)
//  browserBox.setPrefSize(CustomTabs.getContentSize._1,CustomTabs.getContentSize._2)

  def addToUI = parent


}

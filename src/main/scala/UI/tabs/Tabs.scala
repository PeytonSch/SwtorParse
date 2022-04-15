//package UI.tabs
//
//import UI.{Tiles, UIStyle}
//import UI.tabs.Overview
//import scalafx.scene.control.{Tab, TabPane}
//import scalafx.scene.layout.VBox
//
//object Tabs {
//
//  val tabbedPane = new TabPane()
//  tabbedPane.setStyle(UIStyle.mainBackgroundObject)
//  val overViewTab = new Tab
//  overViewTab.setClosable(false)
//  overViewTab.setText("Overview")
//  overViewTab.setStyle(UIStyle.tabStyle)
//  overViewTab.setId("hoverable")
//  val dpsTab = new Tab
//  dpsTab.setClosable(false)
//  dpsTab.setText("DAMAGE DONE")
//  dpsTab.setStyle(UIStyle.tabStyle)
//  val hpsTab = new Tab
//  hpsTab.setClosable(false)
//  hpsTab.setText("HEALING DONE")
//  hpsTab.setStyle(UIStyle.tabStyle)
//  val dtpsTab = new Tab
//  dtpsTab.setClosable(false)
//  dtpsTab.setText("DAMAGE TAKEN")
//  dtpsTab.setStyle(UIStyle.tabStyle)
//  val htpsTab = new Tab
//  htpsTab.setClosable(false)
//  htpsTab.setText("HEALING TAKEN")
//  htpsTab.setStyle(UIStyle.tabStyle)
//  val settingsTab = new Tab
//  settingsTab.setClosable(false)
//  settingsTab.setText("SETTINGS")
//  settingsTab.setStyle(UIStyle.tabStyle)
//  val timersTab = new Tab
//  timersTab.setClosable(false)
//  timersTab.setText("TIMERS")
//  timersTab.setStyle(UIStyle.tabStyle)
//
//  val testCustomTabs = new Tab
//  testCustomTabs.setClosable(false)
//  testCustomTabs.setText("TEST CUSTOM TABS")
//  testCustomTabs.setStyle(UIStyle.tabStyle)
//
//  tabbedPane.tabs = List(overViewTab,dpsTab,hpsTab,dtpsTab,htpsTab,settingsTab,timersTab,testCustomTabs)
//
//
//  // TABS - these add objects that extend from the UITab Trait
////  overViewTab.content = Overview.addToUI()
////  dtpsTab.content = DamageTaken.addToUI()
////  hpsTab.content = HealingDone.addToUI()
////  dpsTab.content = DamageDone.addToUI()
////  htpsTab.content = HealingTaken.addToUI()
////  settingsTab.content = Settings.addToUI()
////  timersTab.content = Timers.addToUI()
//  testCustomTabs.content = TestCustomTabs.addToUI
//
//}

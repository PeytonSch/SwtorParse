package UI.tabs

import UI.Tiles
import UI.tabs.Overview
import scalafx.scene.control.{Tab, TabPane}
import scalafx.scene.layout.VBox

object Tabs {

  val tabbedPane = new TabPane()
  tabbedPane.setId("tabbedPane")
  val overViewTab = new Tab
  overViewTab.setClosable(false)
  overViewTab.setText("Overview")
  val dpsTab = new Tab
  dpsTab.setClosable(false)
  dpsTab.setText("DAMAGE DONE")
  val hpsTab = new Tab
  hpsTab.setClosable(false)
  hpsTab.setText("HEALING DONE")
  val dtpsTab = new Tab
  dtpsTab.setClosable(false)
  dtpsTab.setText("DAMAGE TAKEN")
  val htpsTab = new Tab
  htpsTab.setClosable(false)
  htpsTab.setText("HEALING TAKEN")
  tabbedPane.tabs = List(overViewTab,dpsTab,hpsTab,dtpsTab,htpsTab)


  // TABS - these add objects that extend from the UITab Trait
  overViewTab.content = Overview.addToUI()
  dtpsTab.content = DamageTaken.addToUI()
  hpsTab.content = HealingDone.addToUI()
  dpsTab.content = DamageDone.addToUI()
  htpsTab.content = HealingTaken.addToUI()

}

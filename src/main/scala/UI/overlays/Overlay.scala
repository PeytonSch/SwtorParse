package UI.overlays

import UI.UIElement
import scalafx.scene.control.CheckBox
import scalafx.stage.Stage

/**
 * Overlays
 */
trait Overlay extends UIElement{

  def getOverlay(): Stage

  def show(): Unit = getOverlay().show()

  def hide(): Unit = getOverlay().hide()

  def createSettingsCheckbox(): CheckBox


}

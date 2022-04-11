package UI

/**
 * Everything that is visual in the UI should extend this trait
 */
trait UIElement {

  // need to refresh with new data
  def refresh()

  // need to clear old data
  def clear()

}

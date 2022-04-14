package UI

object UIStyle {

  // TODO: Font isn't being recognized
  private val mainFont = "HelveticaNeue-Light, Helvetica Neue Light, Helvetica Neue, Helvetica, Arial, Lucida Grande, sans-serif";

  private val mainBackgroundColor = "#404040"
  private val darkBackgroundColor = "#2B2B2B"
//  private val lightBackgroundColor = ""
  private val mouseOverHighlightColor = ""

  private val faintBlueTextColor = "#60737F"
  private val faintGreyTextColor = "#6B6B6B"
  private val lightTextColor = "#D9D9D9"

  private val baseTextSize = 18

  private val extraSmallSize = (baseTextSize -4).toString
  private val smallSize = (baseTextSize - 2).toString
  private val mediumSize = (baseTextSize).toString
  private val mediumLargeSize = (baseTextSize + 2).toString
  private val largeSize = (baseTextSize + 4).toString
  private val extraLargeSize = (baseTextSize + 6).toString

  private def createLabelStyle(textColor: String, textSize: String): String = {
    s"""
       |-fx-background-color: transparent;
       |-fx-text-fill: ${textColor};
       |-fx-font-size: ${textSize};
       |-fx-font-family: ${mainFont};
       |-fx-font-weight: 300;
       |-fx-padding: 10;
       |""".stripMargin
  }

  val mainBackgroundObject: String =
    s"""
    |-fx-background-color: ${mainBackgroundColor};
    |""".stripMargin

  val insetBackgroundObject: String =
    s"""
    |-fx-background-color: ${darkBackgroundColor};
    |""".stripMargin

  val transparentObject: String =
    s"""
       |-fx-background-color: transparent;
       |""".stripMargin

  val smallBottomBoarder: String =
    s"""
       |-fx-padding: 10;
       |-fx-border-style: solid inside;
       |-fx-border-width: 0 0 2 0;
       |-fx-border-insets: 5;
       |-fx-border-color: ${lightTextColor};
       |""".stripMargin

  val smallLightLabel: String = createLabelStyle(lightTextColor,smallSize)

  val smallFaintGreyLabel: String = createLabelStyle(faintGreyTextColor,smallSize)

  val smallFaintBlueLabel: String = createLabelStyle(faintBlueTextColor,smallSize)

  val largeLightLabel: String = createLabelStyle(lightTextColor,largeSize)

  val largeFaintGreyLabel: String = createLabelStyle(faintGreyTextColor,largeSize)

  val largeFaintBlueLabel: String = createLabelStyle(faintBlueTextColor,largeSize)

}

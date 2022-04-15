package UI

import scalafx.geometry.Insets
import scalafx.scene.layout.{Background, BackgroundFill, CornerRadii}
import scalafx.scene.paint.Color

object UIStyle {

  // TODO: Font isn't being recognized
  private val mainFont = "HelveticaNeue-Light, Helvetica Neue Light, Helvetica Neue, Helvetica, Arial, Lucida Grande, sans-serif";

  private val mainBackgroundColor = "#2B2B2B"
  private val darkBackgroundColor = "#232323"
  private val lightBackgroundColor = "#404040"
  // right now this is in the css file and needs the #hoverable id
  private val mouseOverHighlightColor = "#262626"

  private val faintBlueTextColor = "#60737F"
  private val faintGreyTextColor = "#6B6B6B"
  private val lightTextColor = "#D9D9D9"
  private val yellowTextColor = "#ecec7a"

  private val orangeBarColor = "#EE8525"

  private val baseTextSize = 18

  private val overlayFontSize = "12"
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
       |-fx-padding: ${(textSize.toInt - 8).toString};
       |""".stripMargin
  }

  private val backgroundFill = new BackgroundFill(Color.web(mainBackgroundColor), CornerRadii.Empty, Insets.Empty)
  private val backgroundFillArray = Array(backgroundFill)
  val background = new Background(backgroundFillArray)

  val tileBackground = Color.web(mainBackgroundColor)

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

  val smallBottomBoarderNoPadding: String =
    s"""
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

  val tabLabelStyle: String = createLabelStyle(faintGreyTextColor,extraLargeSize)

  val tabLabelStyleSelected: String = createLabelStyle(lightTextColor,extraLargeSize)

  val menuLabelStyle: String = createLabelStyle(faintGreyTextColor,mediumSize)

  val menuLabelStyleSelected: String = createLabelStyle(lightTextColor,mediumSize)

  val overlayButtonStyle: String =
    s"""
       |-fx-background-color: ${darkBackgroundColor};
       |-fx-text-fill: ${yellowTextColor};
       |-fx-font-size: ${overlayFontSize};
       |""".stripMargin

  val tabStyle: String =
    s"""
       |-fx-background-color: ${mainBackgroundColor};
       |-fx-border-color: ${darkBackgroundColor};
       |-fx-border-width: 0 1;
       |-fx-padding: 5 110;
       |""".stripMargin

  val customTabUnselected: String =
    s"""
       |-fx-border-style: solid inside;
       |-fx-border-width: 0 0 0 0;
       |-fx-border-insets: 1;
       |-fx-border-color: ${lightTextColor};
       |""".stripMargin

  val customTabHover: String =
    s"""
      |-fx-border-width: 0 0 1 0;
      |-fx-border-color: ${lightTextColor};
      |""".stripMargin

  val customTabSelected: String =
    s"""
       |-fx-border-width: 0 0 1 0;
       |-fx-border-color: ${orangeBarColor};
       |""".stripMargin

  val menuBarStyle: String =
    s"""
       |-fx-background-color: ${darkBackgroundColor};
       |-fx-padding: 5;
       |-fx-border-style: solid inside;
       |-fx-border-width: 0 0 3 0;
       |-fx-border-insets: 5;
       |-fx-border-color: ${faintGreyTextColor};
       |-fx-base: ${mainBackgroundColor};
       |-fx-color: ${mainBackgroundColor};
       |""".stripMargin

  val menuItemStyle: String =
    s"""
       |-fx-background-color: transparent;
       |-fx-border-color: transparent ;
       |-fx-base: ${mainBackgroundColor};
       |-fx-color: ${mainBackgroundColor};
       |""".stripMargin

  val customMenuUnselected: String =
    s"""
       |-fx-border-style: solid inside;
       |-fx-border-width: 0 0 0 0;
       |-fx-border-insets: 1;
       |-fx-border-color: ${lightTextColor};
       |""".stripMargin

  val customMenuHover: String =
    s"""
       |-fx-border-width: 0 0 1 0;
       |-fx-border-color: ${lightTextColor};
       |""".stripMargin

}

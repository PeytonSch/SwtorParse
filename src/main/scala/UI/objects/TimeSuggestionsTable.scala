package UI.objects

import Controller.Controller
import UI.tabs.Timers
import logger.Logger
import scalafx.beans.property.{ObjectProperty, StringProperty}
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.{CheckBox, Label, TableColumn, TableView}
import scalafx.scene.layout.{HBox, VBox}

import scala.collection.mutable
import scala.math.sqrt

class SpreadSheetRow(_ability: String, _source:String, _avgTimeDifference:Double,_timeStd:Double,_avgHealthDiffernce:Double,_healthStd:Double,_num:Int) {
  val ability      = new StringProperty(this, "Ability", _ability)
  val source   = new StringProperty(this, "Source", _source)
  val time         = new ObjectProperty(this, "Avg Time Diff", _avgTimeDifference)
  val timeStd         = new ObjectProperty(this, "Time Std", _timeStd)
  val health         = new ObjectProperty(this, "Avg Health Diff", _avgHealthDiffernce)
  val healthStd         = new ObjectProperty(this, "Health Std", _healthStd)
  val activations         = new ObjectProperty(this, "# of Activations", _num)

}


object TimerSuggestionsTable {

  def create(): TimerSuggestionSheet = {

    /**
     * Mock Data
     */
    val data = ObservableBuffer[SpreadSheetRow](
      //      new DamageRow("Ability Name","Target Name",50,5600,9800,18280,.26,22345,6476000,.50),
      //      new DamageRow("Ability Name","Target Name",50,5600,9800,18280,.26,22345,6476000,.50),
      //      new DamageRow("Ability Name","Target Name",50,5600,9800,18280,.26,22345,6476000,.50),
    )

    // Parent VBOX
    val parent = new VBox()

    val prefWidthValue = 150
//    val prefSmallColWidth = 75


    // create a table
    val table = new TableView[SpreadSheetRow](data) {
      columns ++= Seq(
        new TableColumn[SpreadSheetRow, String] {
          text = "Ability"
          cellValueFactory = _.value.ability
          prefWidth = prefWidthValue
        },
        new TableColumn[SpreadSheetRow, String]() {
          text = "Source"
          cellValueFactory = _.value.source
          prefWidth = prefWidthValue
          style = "-fx-alignment: CENTER"
        },
        new TableColumn[SpreadSheetRow, Double] {
          text = "Avg Time Diff"
          cellValueFactory = _.value.time
          prefWidth = prefWidthValue
          style = "-fx-alignment: CENTER"
        },
        new TableColumn[SpreadSheetRow, Double] {
          text = "Time Std"
          cellValueFactory = _.value.timeStd
          prefWidth = prefWidthValue
          style = "-fx-alignment: CENTER"
        },
        new TableColumn[SpreadSheetRow, Double] {
          text = "Avg Health Diff"
          cellValueFactory = _.value.health
          prefWidth = prefWidthValue
          style = "-fx-alignment: CENTER"
        },
        new TableColumn[SpreadSheetRow, Double] {
          text = "Health Std"
          cellValueFactory = _.value.healthStd
          prefWidth = prefWidthValue
          style = "-fx-alignment: CENTER"
        },
        new TableColumn[SpreadSheetRow, Int] {
          text = "# Activations"
          cellValueFactory = _.value.activations
          prefWidth = prefWidthValue
          style = "-fx-alignment: CENTER"
        }
      )
    }

    table.setOnMouseClicked(event => {
      setClicked()
    })

    def setClicked(): Unit = {

      try {
        // get the row clicked
        val row = table.getSelectionModel.selectedItemProperty().get()
        //      Logger.highlight(s"Clicked: ${row.ability.value}, ${row.source.value}")
        Timers.nameText.setText(s"${row.source.value}:${row.ability.value}")
        Timers.durationText.setText(s"${row.time.value}")
        Timers.sourceActorText.setText(s"${row.source.value}")
        Timers.sourceAbilityText.setText(s"${row.ability.value}")
      }
      catch {
        case e:Throwable => Logger.debug("Failed to populate timer suggestions field. Generally happens with a null value, nothing to worry about.")
      }
    }

    parent.getChildren.addAll(table)
    new TimerSuggestionSheet(parent,table)

  }

  case class TimerSuggestions(
                               abilityName: String,
                               source: String,
                               timeDiff: Double,
                               timeStd: Double,
                               healthDiff: Double,
                               healthStd: Double,
                               activations: Int,
                             )

  def loadTimerSugestions(): Iterable[TimerSuggestions] = {
//    Logger.highlight(s"Found ${Controller.getCurrentCombat().getTimerSuggestionMap.keys.size} keys for combat")
    val suggestions: Iterable[TimerSuggestions] = for (key <- Controller.getCurrentCombat().getTimerSuggestionMap.keys) yield {
      val abilityName = key._1
      val source = key._2
      val times: List[Double] = Controller.getCurrentCombat().getTimerSuggestionMap(key)._1
      val healths: List[Double] = Controller.getCurrentCombat().getTimerSuggestionMap(key)._2

      // calculate time differences
      val timeDifferences = for (index <- Range(1,times.length))yield{
        (times(index) - times(index-1))
      }

      // TODO: Should this be divided by the length of the number of differences?

      val averageTimeDifference = timeDifferences.sum / (timeDifferences.length).toDouble

      // calculate the time std
      val timeStdTop = for (elem <- timeDifferences) yield {(elem-averageTimeDifference)*(elem-averageTimeDifference)}
      val timeStdTopSum = timeStdTop.sum
      val timeStd = sqrt(timeStdTopSum / (timeDifferences.length).toDouble)

      // calculate health differences
      var healthDifferences = for (index <- Range(1,healths.length)) yield {
        (healths(index) - healths(index-1))
      }

      // TODO: Should this be divided by the length of the number of differences?

      val averageHealthDifference = healthDifferences.sum / (healthDifferences.length).toDouble

      // calculate the health std deviation
      val healthStdTop = for (elem <- healthDifferences) yield {(elem-averageHealthDifference)*(elem-averageHealthDifference)}
      val healthStdTopSum = healthStdTop.sum
      val healthStd = sqrt(healthStdTopSum / (healthDifferences.length).toDouble)

      // num activations
      val activations = times.length

      val suggest: TimerSuggestions = TimerSuggestions(abilityName,source,averageTimeDifference,timeStd,averageHealthDifference,healthStd,activations)


//      Logger.highlight(s"Suggestion for ${abilityName}: " +
//        s"Found an average time difference of ${averageTimeDifference} with " +
//        s"a standard deviation of ${timeStd} from the following sequence of differences: \n " +
//        s"${timeDifferences} \n " +
//        s"derived from the following sequence of times \n " +
//        s"${times}")

      suggest

    }

//    Logger.highlight(s"Returning ${suggestions.knownSize} suggestions")
    suggestions
  }

}



class TimerSuggestionSheet(
                     parent: VBox,
                     table: TableView[SpreadSheetRow],
                   ) {

  def getParent = parent

  def getTable = table

  def refresh(): Unit = {
//    Logger.highlight("Refreshing Timer Suggestion Sheet")
    // create an observable buffer
    val buf = ObservableBuffer[SpreadSheetRow]()
    // get timer suggestions
    val timerSuggestions = TimerSuggestionsTable.loadTimerSugestions()
    // add data to buffer
     for (entry <- timerSuggestions) {
        buf +=
          new SpreadSheetRow(
            entry.abilityName, entry.source, entry.timeDiff,entry.timeStd, entry.healthDiff,entry.healthStd, entry.activations
          )
      }

    // Set spread sheet data to buffer
    table.setItems(buf)
  }

}

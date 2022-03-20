package UI.GraphicFactory

import javafx.scene.control.cell.PropertyValueFactory
import scalafx.beans.property.{DoubleProperty, IntegerProperty, ObjectProperty, StringProperty}
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.{TableColumn, TableView}
import scalafx.scene.layout.VBox
import scalafx.scene.paint.Color
import scalafx.scene.shape.Circle


class SpreadSheetRow(_ability: String, _targetName:String, _hits: Int, _norm:Int, _crit:Int, _avg:Int, _miss:Double, _dps:Int, _total:Int, _totalPercent:Double) {
    val ability      = new StringProperty(this, "Ability", _ability)
    val targetName   = new StringProperty(this, "Target Name", _targetName)
    val hits         = new ObjectProperty(this, "Hits", _hits)
    val norm         = new ObjectProperty(this, "Nrom", _norm)
    val crit         = new ObjectProperty(this, "Crit", _crit)
    val avg          = new ObjectProperty(this, "AVG", _avg)
    val miss         = new ObjectProperty(this, "Miss", _miss)
    val dps          = new ObjectProperty(this, "DPS", _dps)
    val total        = new ObjectProperty(this, "Total", _total)
    val totalPercent = new ObjectProperty(this, "Total %", _totalPercent)

}


object SpreadSheetFactory {

  def create(spreadSheetType:String): SpreadSheet = {
    /**
     * Layout Summary:
     * Parent layout if going to be a vbox,
     * first element is the header section which whill be an hbox,
     * next element will be a vbox that will contain rows of data,
     * each row will be an hbox
     */

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

    // create a table
    val table = new TableView[SpreadSheetRow](data) {
      columns ++= Seq(
        new TableColumn[SpreadSheetRow, String] {
          text = "Ability Name"
          cellValueFactory = _.value.ability
          prefWidth = prefWidthValue
        },
        new TableColumn[SpreadSheetRow, String]() {
          text = "Target Name"
          cellValueFactory = _.value.targetName
          prefWidth = prefWidthValue
        },
        new TableColumn[SpreadSheetRow, Int] {
          text = "# of Hits"
          cellValueFactory = _.value.hits
          prefWidth = prefWidthValue
          style = "-fx-alignment: CENTER"
        },
        new TableColumn[SpreadSheetRow, Int]() {
          text = "Normal Value"
          cellValueFactory = _.value.norm
          prefWidth = prefWidthValue
          style = "-fx-alignment: CENTER"
        },
        new TableColumn[SpreadSheetRow, Int] {
          text = "Avg Crit Value"
          cellValueFactory = _.value.crit
          prefWidth = prefWidthValue
          style = "-fx-alignment: CENTER"
        },
        new TableColumn[SpreadSheetRow, Int]() {
          text = "Avg Value"
          cellValueFactory = _.value.avg
          prefWidth = prefWidthValue
          style = "-fx-alignment: CENTER"
        },
        new TableColumn[SpreadSheetRow, Double] {
          text = "Miss %"
          cellValueFactory = _.value.miss
          prefWidth = prefWidthValue
          style = "-fx-alignment: CENTER"
        },
        new TableColumn[SpreadSheetRow, Int]() {
          text = s"${spreadSheetType} / Sec"
          cellValueFactory = _.value.dps
          prefWidth = prefWidthValue
          style = "-fx-alignment: CENTER"
        },
        new TableColumn[SpreadSheetRow, Int] {
          text = s"Total ${spreadSheetType}"
          cellValueFactory = _.value.total
          prefWidth = prefWidthValue
          style = "-fx-alignment: CENTER"
        },
        new TableColumn[SpreadSheetRow, Double]() {
          text = s"Total % of ${spreadSheetType}"
          cellValueFactory = _.value.totalPercent
          prefWidth = prefWidthValue
          style = "-fx-alignment: CENTER"
        }
      )
    }


    parent.getChildren.addAll(table)

    new SpreadSheet(parent,table)


  }

}

class SpreadSheet(
                   parent: VBox,
                   table: TableView[SpreadSheetRow],
                 ) {

  def getParent = parent
  def getTable = table

}

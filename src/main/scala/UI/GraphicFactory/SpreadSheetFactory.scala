package UI.GraphicFactory

import UI.ElementLoader
import javafx.scene.control.cell.PropertyValueFactory
import logger.Logger
import scalafx.beans.property.{DoubleProperty, IntegerProperty, ObjectProperty, StringProperty}
import scalafx.collections.ObservableBuffer
import scalafx.event.ActionEvent
import scalafx.scene.control.{CheckBox, Label, TableColumn, TableView}
import scalafx.scene.layout.{HBox, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.shape.Circle
import scalafx.Includes._


class SpreadSheetRow(_ability: String, _targetName:String, _hits:Int,_normHits:Int,_critHits:Int, _norm:Int, _crit:Int, _avg:Int, _miss:Double, _dps:Int, _total:Int, _totalPercent:Double) {
    val ability      = new StringProperty(this, "Ability", _ability)
    val targetName   = new StringProperty(this, "Target Name", _targetName)
    val hits         = new ObjectProperty(this, "Hits", _hits)
    val normHits         = new ObjectProperty(this, "Norm Hits", _normHits)
    val critHits         = new ObjectProperty(this, "Crit Hits", _critHits)
    val norm         = new ObjectProperty(this, "Norm", _norm)
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
    val prefSmallColWidth = 75

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
          prefWidth = prefSmallColWidth
          style = "-fx-alignment: CENTER"
        },
        new TableColumn[SpreadSheetRow, Int] {
          text = "Norm Hits"
          cellValueFactory = _.value.normHits
          prefWidth = prefSmallColWidth
          style = "-fx-alignment: CENTER"
        },
        new TableColumn[SpreadSheetRow, Int] {
          text = "Crit Hits"
          cellValueFactory = _.value.critHits
          prefWidth = prefSmallColWidth
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

    /**
     * Create Filter toggles
     */
    val filterBox = new HBox()
    val aggregateLabel = new Label("Aggregate Data by: ")
    val abilityCheckbox = new CheckBox("Ability")
    setCheckboxAction(abilityCheckbox,spreadSheetType)
    val targetTypeCheckbox = new CheckBox("Target Type")
    setCheckboxAction(targetTypeCheckbox,spreadSheetType)
    // TODO: Target instance hasn't been set up yet!
    val targetInstanceCheckbox = new CheckBox("Target Instance")
    setCheckboxAction(targetInstanceCheckbox,spreadSheetType)

    val checkBoxStyle = "-fx-font-size: 12;"
    abilityCheckbox.setStyle(checkBoxStyle)
    targetTypeCheckbox.setStyle(checkBoxStyle)
    targetInstanceCheckbox.setStyle(checkBoxStyle)

    // By default, we show data by target instance and ability
    abilityCheckbox.selected = true
    targetTypeCheckbox.selected = true
    targetInstanceCheckbox.selected = false

    filterBox.getChildren.addAll(aggregateLabel,abilityCheckbox,targetTypeCheckbox)

    parent.getChildren.addAll(filterBox,table)

    new SpreadSheet(parent,table,abilityCheckbox,targetTypeCheckbox,targetInstanceCheckbox)


  }

  def setCheckboxAction(c:CheckBox, sheetType: String): Unit = {
    if (sheetType == "Damage"){
      c.onAction = (event: ActionEvent) => {
        ElementLoader.rollBackCombatIfNeeded()
        ElementLoader.updateDamageDoneSpreadSheet()
        ElementLoader.restorCombatIfRolledBack()
      }
    } else if (sheetType == "Damage Taken"){
      c.onAction = (event: ActionEvent) => {
        ElementLoader.rollBackCombatIfNeeded()
        ElementLoader.updateDamageTakenSpreadSheet()
        ElementLoader.restorCombatIfRolledBack()
      }
    } else if (sheetType == "Healing"){
      c.onAction = (event: ActionEvent) => {
        ElementLoader.rollBackCombatIfNeeded()
        ElementLoader.updateHealingDoneSpreadSheet()
        ElementLoader.restorCombatIfRolledBack()
      }
    } else if (sheetType == "Healing Taken"){
      c.onAction = (event: ActionEvent) => {
        ElementLoader.rollBackCombatIfNeeded()
        ElementLoader.updateHealingTakenSpreadSheet()
        ElementLoader.restorCombatIfRolledBack()
      }
    } else {
      Logger.error(s"Sheet type not recognized: ${sheetType}")
    }

  }

}

class SpreadSheet(
                   parent: VBox,
                   table: TableView[SpreadSheetRow],
                   abilityCheckbox: CheckBox,
                   targetTypeCheckbox: CheckBox,
                   targetInstanceCheckbox: CheckBox
                 ) {

  def getParent = parent
  def getTable = table

  def updateAsDamageDone() = {
    // Aggregate by ability only
    if (
        abilityCheckbox.selected.value == true &&
        targetTypeCheckbox.selected.value == false
    ) {
      val data = Controller.Controller.getCurrentCombat().getPlayerInCombatActor().getDamageDoneByAbilitySpreadSheetData()
      table.setItems(data)
    }
      // the default view, aggregate on (ability, target)
      // dont care about what targetType box is set to because target instance overrides it
    else if(
        abilityCheckbox.selected.value == true &&
        targetTypeCheckbox.selected.value == true
    ) {
      val data = Controller.Controller.getCurrentCombat().getPlayerInCombatActor().getDamageDoneSpreadSheetData()
      table.setItems(data)
    }
    // Aggregate by target type
    else if(
      abilityCheckbox.selected.value == false &&
        targetTypeCheckbox.selected.value == true
    ) {
      val data = Controller.Controller.getCurrentCombat().getPlayerInCombatActor().getDamageDoneToTargetTypeSpreadSheetData()
      table.setItems(data)
    }
  }

  def updateAsDamageTaken() = {
    // Aggregate by ability only
    if (
      abilityCheckbox.selected.value == true &&
        targetTypeCheckbox.selected.value == false
    ) {
      val data = Controller.Controller.getCurrentCombat().getPlayerInCombatActor().getDamageTakenByAbilitySpreadSheetData()
      table.setItems(data)
    }
    // the default view, aggregate on (ability, target)
    // dont care about what targetType box is set to because target instance overrides it
    else if(
      abilityCheckbox.selected.value == true &&
        targetTypeCheckbox.selected.value == true
    ) {
      val data = Controller.Controller.getCurrentCombat().getPlayerInCombatActor().getDamageTakenSpreadSheetData()
      table.setItems(data)
    }
    // Aggregate by target type
    else if(
      abilityCheckbox.selected.value == false &&
        targetTypeCheckbox.selected.value == true
    ) {
      val data = Controller.Controller.getCurrentCombat().getPlayerInCombatActor().getDamageTakenToTargetTypeSpreadSheetData()
      table.setItems(data)
    }
  }


  def updateAsHealingDone() = {
    // Aggregate by ability only
    if (
      abilityCheckbox.selected.value == true &&
        targetTypeCheckbox.selected.value == false
    ) {
      val data = Controller.Controller.getCurrentCombat().getPlayerInCombatActor().getHealingDoneByAbilitySpreadSheetData()
      table.setItems(data)
    }
    // the default view, aggregate on (ability, target)
    // dont care about what targetType box is set to because target instance overrides it
    else if(
      abilityCheckbox.selected.value == true &&
        targetTypeCheckbox.selected.value == true
    ) {
      val data = Controller.Controller.getCurrentCombat().getPlayerInCombatActor().getHealingDoneSpreadSheetData()
      table.setItems(data)
    }
    // Aggregate by target type
    else if(
      abilityCheckbox.selected.value == false &&
        targetTypeCheckbox.selected.value == true
    ) {
      val data = Controller.Controller.getCurrentCombat().getPlayerInCombatActor().getHealingDoneToTargetTypeSpreadSheetData()
      table.setItems(data)
    }
  }

  def updateAsHealingTaken() = {
    // Aggregate by ability only
    if (
      abilityCheckbox.selected.value == true &&
        targetTypeCheckbox.selected.value == false
    ) {
      val data = Controller.Controller.getCurrentCombat().getPlayerInCombatActor().getHealingTakenByAbilitySpreadSheetData()
      table.setItems(data)
    }
    // the default view, aggregate on (ability, target)
    // dont care about what targetType box is set to because target instance overrides it
    else if(
      abilityCheckbox.selected.value == true &&
        targetTypeCheckbox.selected.value == true
    ) {
      val data = Controller.Controller.getCurrentCombat().getPlayerInCombatActor().getHealingTakenSpreadSheetData()
      table.setItems(data)
    }
    // Aggregate by target type
    else if(
      abilityCheckbox.selected.value == false &&
        targetTypeCheckbox.selected.value == true
    ) {
      val data = Controller.Controller.getCurrentCombat().getPlayerInCombatActor().getHealingTakenToTargetTypeSpreadSheetData()
      table.setItems(data)
    }
  }


}

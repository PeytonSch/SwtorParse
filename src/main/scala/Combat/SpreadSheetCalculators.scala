package Combat

import UI.GraphicFactory.SpreadSheetRow
import scalafx.collections.ObservableBuffer

import scala.collection.mutable

/**
 * The entire idea of these functions is to allow us to only store the spread sheet data once.
 * This will hopefully give us improved performance and when the default selections are checked
 * increased performance. These functions take the spread sheet data we store and convert it to
 * whichever type of data we have selected.
 */
object SpreadSheetCalculators {

  def aggregateOnAbility(m: mutable.Map[(String,String),(Int,Int,Int,Int,Int,Int,Double,Int,Int,Double)]): mutable.Map[(String),(Int,Int,Int,Int,Int,Int,Double,Int,Int,Double)] = {
    var abilityBased: mutable.Map[(String),(Int,Int,Int,Int,Int,Int,Double,Int,Int,Double)] = mutable.Map()

    for (key <- m.keys){
      if(abilityBased.contains(key._1)){
        val hits = m(key)._1 + abilityBased(key._1)._1
        val normHits = m(key)._2 + abilityBased(key._1)._2
        val critHits = m(key)._3 + abilityBased(key._1)._3
        // (oldNorm*normHits + currentNorm*normHits) / (oldNormHits + currentNormHits))
        val norm = if((m(key)._2 + abilityBased(key._1)._2) == 0) 0 else ((m(key)._4 * m(key)._2) + (abilityBased(key._1)._4 * abilityBased(key._1)._2)) / (m(key)._2 + abilityBased(key._1)._2)
        // (oldCrit*critHits + currentCrit*critHits) / (oldCritHits + currentCritHits))
        val crit = if ((m(key)._3 + abilityBased(key._1)._3) == 0) 0 else ((m(key)._5 * m(key)._3) + (abilityBased(key._1)._5 * abilityBased(key._1)._3)) / (m(key)._3 + abilityBased(key._1)._3)
        val total = abilityBased(key._1)._9 + m(key)._9
        // total damage / hits
        val avg = total / hits
        val miss = 0
        val dps = 0
        val totalPercent = 0

        abilityBased(key._1) = (hits,normHits,critHits,norm,crit,avg,miss,dps,total,totalPercent)

      }
      else {
        val hits = m(key)._1
        val normHits = m(key)._2
        val critHits = m(key)._3
        val norm = m(key)._4
        val crit = m(key)._5
        val total = m(key)._9
        val avg = total / hits
        val miss = 0
        val dps = 0
        val totalPercent = 0

        abilityBased(key._1) = (hits,normHits,critHits,norm,crit,avg,miss,dps,total,totalPercent)
      }

    }

    abilityBased
  }


  def aggregateOnTargetType(m: mutable.Map[(String,String),(Int,Int,Int,Int,Int,Int,Double,Int,Int,Double)]): mutable.Map[(String),(Int,Int,Int,Int,Int,Int,Double,Int,Int,Double)] = {
    var targetTypeBased: mutable.Map[(String),(Int,Int,Int,Int,Int,Int,Double,Int,Int,Double)] = mutable.Map()

    for (key <- m.keys){
      if(targetTypeBased.contains(key._2)){
        val hits = m(key)._1 + targetTypeBased(key._2)._1
        val normHits = m(key)._2 + targetTypeBased(key._2)._2
        val critHits = m(key)._3 + targetTypeBased(key._2)._3
        // (oldNorm*normHits + currentNorm*normHits) / (oldNormHits + currentNormHits))
        val norm = if((m(key)._2 + targetTypeBased(key._2)._2) == 0) 0 else ((m(key)._4 * m(key)._2) + (targetTypeBased(key._2)._4 * targetTypeBased(key._2)._2)) / (m(key)._2 + targetTypeBased(key._2)._2)
        // (oldCrit*critHits + currentCrit*critHits) / (oldCritHits + currentCritHits))
        val crit = if ((m(key)._3 + targetTypeBased(key._2)._3) == 0) 0 else ((m(key)._5 * m(key)._3) + (targetTypeBased(key._2)._5 * targetTypeBased(key._2)._3)) / (m(key)._3 + targetTypeBased(key._2)._3)
        val total = targetTypeBased(key._2)._9 + m(key)._9
        // total damage / hits
        val avg = total / hits
        val miss = 0
        val dps = 0
        val totalPercent = 0

        targetTypeBased(key._2) = (hits,normHits,critHits,norm,crit,avg,miss,dps,total,totalPercent)

      }
      else {
        val hits = m(key)._1
        val normHits = m(key)._2
        val critHits = m(key)._3
        val norm = m(key)._4
        val crit = m(key)._5
        val total = m(key)._9
        val avg = total / hits
        val miss = 0
        val dps = 0
        val totalPercent = 0

        targetTypeBased(key._2) = (hits,normHits,critHits,norm,crit,avg,miss,dps,total,totalPercent)
      }

    }

    targetTypeBased
  }

  /**
   * These get spreadsheet data functions generate an ObservableBuffer[SpreadSheetRow] that the UI needs
   * to display the data. We have a couple of different types of them to support the different types of data inputs
   */


  def getSpreadSheetData(dataSheetMap: mutable.Map[(String,String),(Int,Int,Int,Int,Int,Int,Double,Int,Int,Double)] ):ObservableBuffer[SpreadSheetRow] = {
    // create an observable buffer
    val buf = ObservableBuffer[SpreadSheetRow]()
    // add data to buffer
    for (entry <- dataSheetMap) {
      buf +=
        new SpreadSheetRow(
          entry._1._1, entry._1._2,entry._2._1,entry._2._2,entry._2._3,entry._2._4,entry._2._5,entry._2._6,entry._2._7,entry._2._8,entry._2._9,entry._2._10
        )
    }
    buf
  }


  def getSpreadSheetDataByAbility(dataSheetMap: mutable.Map[(String),(Int,Int,Int,Int,Int,Int,Double,Int,Int,Double)] ):ObservableBuffer[SpreadSheetRow] = {
    // create an observable buffer
    val buf = ObservableBuffer[SpreadSheetRow]()
    // add data to buffer
    for (entry <- dataSheetMap) {
      buf +=
        new SpreadSheetRow(
          entry._1, "All Targets",entry._2._1,entry._2._2,entry._2._3,entry._2._4,entry._2._5,entry._2._6,entry._2._7,entry._2._8,entry._2._9,entry._2._10
        )
    }
    buf
  }

  def getSpreadSheetDataToTarget(dataSheetMap: mutable.Map[(String),(Int,Int,Int,Int,Int,Int,Double,Int,Int,Double)] ):ObservableBuffer[SpreadSheetRow] = {
    // create an observable buffer
    val buf = ObservableBuffer[SpreadSheetRow]()
    // add data to buffer
    for (entry <- dataSheetMap) {
      buf +=
        new SpreadSheetRow(
          "All Abilities",entry._1,entry._2._1,entry._2._2,entry._2._3,entry._2._4,entry._2._5,entry._2._6,entry._2._7,entry._2._8,entry._2._9,entry._2._10
        )
    }
    buf
  }

}

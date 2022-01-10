package patterns

import parsing.Actions.DefaultAction
import parsing.Actors.Actor
import parsing.Result.Result
import parsing.Threat.ThreatValue
import parsing.Values.Value
import parsing.subTypes.LogTimestamp
import patterns.Actions.Action

class LogInformation (
                       time : LogTimestamp,
                       performer : Actor,
                       target : Actor,
                       action : Action,
                       result : Result,
                       resultValue : Value,
                       threatValue : ThreatValue
                     ){

  def getTime(): LogTimestamp = time
  def getPerformer(): Actor = performer
  def getTarget(): Actor = target
  def getAction(): Action = action
  def getResult(): Result = result
  def getResulValue(): Value = resultValue
  def getThreatValue(): ThreatValue = threatValue

  override def toString: String = s"${time} ${performer} ${target} ${action} ${result} ${resultValue} ${threatValue}"


}

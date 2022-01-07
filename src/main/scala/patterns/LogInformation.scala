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


}

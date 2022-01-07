package parsing.Actions

import patterns.Actions.Action

class DefaultAction(
               name: String,
               id: String
             ) extends Action{

  override def toString: String = s"[ Name: ${name} Id: ${id} ]"

}

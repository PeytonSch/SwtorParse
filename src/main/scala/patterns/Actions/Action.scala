package patterns.Actions

class Action (
               name: String,
               id: String
             ){

  override def toString: String = s"[ Name: ${name} Id: ${id} ]"

}

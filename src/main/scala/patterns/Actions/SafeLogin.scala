package patterns.Actions

class SafeLogin(
               name: String,
               id: String
             ) extends Action{

  override def toString: String = s"[ Name: ${name} Id: ${id} ]"

  override def getName(): String = name

}

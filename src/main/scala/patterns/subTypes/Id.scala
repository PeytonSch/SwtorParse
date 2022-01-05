package patterns.subTypes

class Id (
         typeId : Int,
         instanceId : Int
         ){

  override def toString: String = "[ Type ID: " + typeId + " Instance ID: " + instanceId + " ]"

}

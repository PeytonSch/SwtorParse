package patterns.subTypes

class Id ( // These IDs are too Long to be Ints or Longs, they need to be strings
         typeId : String,
         instanceId : String
         ){

  override def toString: String = "[ Type ID: " + typeId + " Instance ID: " + instanceId + " ]"

}

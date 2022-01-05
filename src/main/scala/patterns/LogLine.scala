package patterns

case class Entity(
                 name: String,
                 x_dir : Double,
                 y_dir : Double,
                 z_dir : Double,
                 angle : Double,
                 currentHealth : Int,
                 maxHealth : Int
                 )

class LogLine() {
  private var timestamp : String = "" // TODO: Make timestamp type
  private var lineType : String = ""
//  private var performer : Entity = Entity("",None,None,None,None,None,None)
//  private var target : Entity = ""


}

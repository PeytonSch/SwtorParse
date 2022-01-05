package patterns.subTypes

class Position(
              x_dir : Double,
              y_dir : Double,
              z_dir : Double,
              facing : Double
              ) {

  override def toString: String = "[ x_dir: " + x_dir + " y_dir: " + y_dir + " z_dir: " + z_dir + " facing: " + facing + " ]"

}

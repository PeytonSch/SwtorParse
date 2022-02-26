package Utils

import com.typesafe.config.ConfigFactory

object Config {

  // make as var so we can reload it after updates
  var config = ConfigFactory.load()

}

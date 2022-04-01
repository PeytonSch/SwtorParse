package Utils

import com.typesafe.config.ConfigFactory

import java.util.prefs.Preferences

object Config {

  // make as var so we can reload it after updates
  var config = ConfigFactory.load()

  //Initialize Java Preferences object
  val settings: Preferences = Preferences.userNodeForPackage(this.getClass())


  //Example code for working with Java Preferences API (assuming prefs is the instance of the Preference class)
  //Set a preference value: prefs.put("key", "value")
  //Get a preference value: prefs.get("key", "default value")
  //Print all the valid keys in this node: prefs.keys().foreach(println)
  //Remove a key: prefs.remove("key")
  //Force changes to be updated in the preferences storage: prefs.flush()
  //MJP

  //Example code for getting directory from preferences instead. ("./SampleLogs") is a default value if key: "PARSE_LOG_DIR" is not found
  //val files = Utils.FileHelper.getListOfFiles(prefs.get("PARSE_LOG_DIR", "./SampleLogs"))

}

package Utils

import UI.ElementLoader
import logger.Logger

import java.io.{File, FileWriter}
import scala.collection.mutable
import scala.io.Source

object FileHelper{
    def getListOfFiles(dir: String):List[File] = {
      Logger.highlight(s"Getting list of files from dir ${dir}")
      val d = new File(dir)
      if (d.exists && d.isDirectory) {
        d.listFiles.filter(_.isFile).toList
      } else {
        List[File]()
      }
    }

  def writeTimerToFile(timer: String): Unit = {
      val fw = new FileWriter("src/main/resources/timers.txt", false)
      try {
        fw.write(timer)
        fw.write("\n")
      }
      finally fw.close()
    }

  def getSavedTimers() = {
    Source.fromFile("src/main/resources/timers.txt").getLines.toList
  }
}

object PathLoader {

  val paths: mutable.Set[String] = mutable.Set()

  def getPaths(): List[String] = {
    val loaded = Source.fromFile("src/main/resources/paths.txt").getLines.toList
    loaded.foreach(s => paths.add(s))
    loaded
  }

  def addPath(recentDir: String): Unit = {
    if (!paths.contains(recentDir)) {
      paths.add(recentDir)
      val fw = new FileWriter("src/main/resources/paths.txt", true)
      try {
        fw.write(recentDir)
        fw.write("\n")
      }
      finally fw.close()
      ElementLoader.loadRecentDirectoryMenu()
    }
  }

}
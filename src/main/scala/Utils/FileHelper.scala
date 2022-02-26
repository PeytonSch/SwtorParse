package Utils

import UI.ElementLoader

import java.io.{File, FileWriter}
import scala.collection.mutable
import scala.io.Source

object FileHelper{
    def getListOfFiles(dir: String):List[File] = {
      val d = new File(dir)
      if (d.exists && d.isDirectory) {
        d.listFiles.filter(_.isFile).toList
      } else {
        List[File]()
      }
    }
}

object PathLoader {

  val paths: mutable.Set[String] = mutable.Set()

  def getPaths(): List[String] = {
    // TODO: What path should this be in when running from exe?
    Source.fromFile("src/main/resources/paths.txt").getLines.toList
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
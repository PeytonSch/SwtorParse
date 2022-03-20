import sbt.Keys.libraryDependencies

name := "SwtorParseScala"

version := "alpha.1.0.3"

scalaVersion := "2.13.7"

lazy val root = (project in file("."))
  .settings(
    // https://mvnrepository.com/artifact/eu.hansolo/tilesfx
    libraryDependencies ++= Seq(
       "eu.hansolo" % "tilesfx" % "16.0.3",
       "org.scalafx" %% "scalafx" % "17.0.1-R26",
       "org.scalatest" %% "scalatest" % "3.1.1" % "test", // test library,
       "com.typesafe" % "config" % "1.4.1"
    )//ConfigFactory,

  )


assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}






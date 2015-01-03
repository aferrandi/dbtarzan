name := "dbtarzan"

version := "1.0"

scalaVersion := "2.11.4"

mainClass in Compile := Some("dbtarzan.gui.Main")

scalaSource in Compile := baseDirectory.value / "src"

scalaSource in Test := baseDirectory.value / "test"

libraryDependencies ++= Seq(
  "io.spray" %%  "spray-json" % "1.3.1",
  "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test",
  "org.scalafx" %% "scalafx" % "8.0.20-R6",
  "com.typesafe.akka" %% "akka-actor" % "2.3.7",
  "org.apache.derby" % "derby" % "10.11.1.1"
)

unmanagedJars in Compile += Attributed.blank(
    file(scala.util.Properties.javaHome) / "lib" / "jfxrt.jar")

fork := true
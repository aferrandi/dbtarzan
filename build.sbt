import com.github.retronym.SbtOneJar._

oneJarSettings

enablePlugins(DebianPlugin,JavaAppPackaging)

name := "dbtarzan"

version := "1.03"

maintainer := "Max Smith <max.smith@yourcompany.io>"

scalaVersion := "2.11.4"

mainClass in Compile := Some("dbtarzan.gui.Main")

mainClass in oneJar := Some("dbtarzan.gui.Main")

scalaSource in Compile := baseDirectory.value / "src"

scalaSource in Test := baseDirectory.value / "test"

libraryDependencies ++= Seq(
  "io.spray" %%  "spray-json" % "1.3.1",
  "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test",
  "org.scalafx" %% "scalafx" % "8.+",
  "com.typesafe.akka" %% "akka-actor" % "2.3.7"
)

unmanagedJars in Compile += Attributed.blank(
    file(scala.util.Properties.javaHome) / "lib" / "jfxrt.jar")

fork := true

/* debian package */
packageSummary := "DBTarzan Debian Package"
packageDescription := "DBTarzan, the database browser"
debianPackageDependencies in Debian ++= Seq("openjdk-8-jre")
bashScriptExtraDefines += """addApp "--configPath=$HOME/.config/dbtarzan""""

addCommandAlias("packageAll", 
	"; one-jar " + 
	"; debian:package-bin"
)
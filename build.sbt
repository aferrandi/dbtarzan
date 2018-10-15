import scala.sys.process._

enablePlugins(DebianPlugin,JavaAppPackaging)

// scalacOptions += "-Ylog-classpath"

name := "dbtarzan"

version := "1.15"

maintainer := "Andrea Ferrandi"

scalaVersion := "2.12.4"

mainClass in Compile := Some("dbtarzan.gui.Main")

scalaSource in Compile := baseDirectory.value / "src"

scalaSource in Test := baseDirectory.value / "test"

scalacOptions in Compile ++= Seq("-Ywarn-unused:imports")
scalacOptions in Compile --= Seq("-Xfatal-warnings")
libraryDependencies ++= Seq(
  "io.spray" %%  "spray-json" % "1.3.4",
  "org.scalatest" % "scalatest_2.12" % "3.0.5" % "test",
  "org.scalafx" %% "scalafx" % "8.+",
  "com.typesafe.akka" %% "akka-actor" % "2.5.11"
)

unmanagedJars in Compile += Attributed.blank(
    file(scala.util.Properties.javaHome) / "lib" / "ext" / "jfxrt.jar")

fork := true

/* debian package */
packageSummary := "DBTarzan Debian Package"
packageDescription := "DBTarzan, the database browser"
debianPackageDependencies in Debian ++= Seq("openjdk-8-jre")
bashScriptExtraDefines += """addApp "--configPath=$HOME/.config/dbtarzan""""


lazy val packageMacOS = taskKey[Unit]("Packages MacOS app")
packageMacOS := {
  val macOsDir = baseDirectory.value / "macosx"
  "macosx/package.sh "+macOsDir+" "+version.value !
}

lazy val packageWin = taskKey[Unit]("Packages Windows app")
packageWin := {
  val rootDir = baseDirectory.value
  "mkwin/packageexe.sh "+rootDir+" "+version.value !
}


addCommandAlias("packageAll", 
	"; assembly " + 
	"; debian:packageBin" +
  "; packageWin" +
  "; packageMacOS"
)

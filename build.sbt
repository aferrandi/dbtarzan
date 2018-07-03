enablePlugins(DebianPlugin,JavaAppPackaging)

// scalacOptions += "-Ylog-classpath"

name := "dbtarzan"

version := "1.12"

maintainer := "Andrea Ferrandi"

scalaVersion := "2.12.4"

mainClass in Compile := Some("dbtarzan.gui.Main")

scalaSource in Compile := baseDirectory.value / "src"

scalaSource in Test := baseDirectory.value / "test"

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

addCommandAlias("packageAll", 
	"; assembly " + 
	"; debian:packageBin"
)

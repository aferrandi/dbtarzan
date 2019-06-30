import scala.sys.process._

fork := true

lazy val standardLibraries = Seq(
  "io.spray" %%  "spray-json" % "1.3.4",
  "org.scalatest" % "scalatest_2.12" % "3.0.5" % "test",
  "org.scalafx" %% "scalafx" % "11-R16",
  "com.typesafe.akka" %% "akka-actor" % "2.5.11",
  "com.h2database" % "h2" % "1.4.197"
)


lazy val commonConfiguration = Seq(
  name := "dbtarzan",

  maintainer := "Andrea Ferrandi",

  version := "1.20",

  scalaVersion := "2.12.8",

  mainClass in Compile := Some("dbtarzan.gui.Main"),

  scalaSource in Compile := baseDirectory.value / ".." / "src",

  scalaSource in Test := baseDirectory.value / ".." / "test",

  resourceDirectory in Compile := baseDirectory.value / ".." / "src" / "main" / "resources",

  resourceDirectory in Test := baseDirectory.value / ".." / "src" / "test" / "resources",

  scalacOptions in Compile ++= Seq("-Ywarn-unused:imports"),
  scalacOptions in Compile --= Seq("-Xfatal-warnings"),
  assemblyMergeStrategy in assembly := {
    case "module-info.class" => MergeStrategy.discard
    case x => {
      val oldStrategy = (assemblyMergeStrategy in assembly).value
      oldStrategy(x)
    }
  },
// scalacOptions += "-Ylog-classpath"
  maintainer := "Andrea Ferrandi <ferrandi.andrea@gmail.com>",
  packageSummary := "DBTarzan Package",
  packageDescription := "DBTarzan, the database browser"

) 

def buildProject(name: String) = {
  val javaFXModules = Seq("base", "controls", "graphics", "media")
  Project(name, file(s"prj${name}"))
    .settings(commonConfiguration)
    .settings(
      libraryDependencies ++= standardLibraries ++ javaFXModules.map( module => 
        "org.openjfx" % s"javafx-$module" % "11" classifier name
      )
    )
}


lazy val linux = buildProject("linux")
    .settings(Seq(
      debianPackageDependencies in Debian ++= Seq("openjdk-11-jre"),
      bashScriptExtraDefines += """addApp "--configPath=$HOME/.config/dbtarzan""""
    ))
    .enablePlugins(DebianPlugin, JavaAppPackaging)
    
lazy val win = buildProject("win")

lazy val mac = buildProject("mac")

lazy val packageWin = taskKey[Unit]("Packages Windows app")
packageWin := {
  val rootDir = baseDirectory.value
  (win/assembly).value // dependency
  "mkwin/packageexe.sh "+rootDir+" "+version.value !
}

lazy val packageMacOS = taskKey[Unit]("Packages MacOS app")
packageMacOS := {
  val macOsDir = baseDirectory.value / "macosx"
  (mac/assembly).value // dependency
  "macosx/package.sh "+macOsDir+" "+version.value !
}

lazy val packageSnap = taskKey[Unit]("Packages Snap")
packageSnap := {
  val rootDir = baseDirectory.value
  (linux/assembly).value // dependency
  "mksnap/create.sh "+rootDir+" "+version.value !
}

addCommandAlias("packageAll", 
	"; debian:packageBin" +
  "; packageWin" +
  "; packageMacOS" +
  "; packageSnap"
)

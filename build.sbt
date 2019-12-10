import scala.sys.process._

fork := true

val versionNymber = "1.21"
version := versionNymber

lazy val standardLibraries = Seq(
  "io.spray" %%  "spray-json" % "1.3.5",
  "org.scalatest" % "scalatest_2.13" % "3.1.0" % "test",
  "org.scalafx" % "scalafx_2.13" % "12.0.2-R18",
  "com.typesafe.akka" %% "akka-actor" % "2.6.0",
  "com.h2database" % "h2" % "1.4.197"
)


lazy val commonConfiguration = Seq(
  name := "dbtarzan",

  maintainer := "Andrea Ferrandi",

  version := versionNymber,

  scalaVersion := "2.13.1",

  mainClass in Compile := Some("dbtarzan.gui.Main"),

  scalaSource in Compile := baseDirectory.value / ".." / "src" / "main" / "scala",

  scalaSource in Test := baseDirectory.value / ".." / "src" / "test" / "scala",

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
        "org.openjfx" % s"javafx-$module" % "12" classifier name
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
  "mksnap/create.sh "+rootDir+" "+ version.value !
}

addCommandAlias("packageAll", 
	"; debian:packageBin" +
  "; packageWin" +
  "; packageMacOS" +
  "; packageSnap"
)

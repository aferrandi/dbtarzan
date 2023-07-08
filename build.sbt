import scala.sys.process._

fork := true

val versionNumber = "1.27"
version := versionNumber

lazy val standardLibraries = Seq(
  "io.spray" %%  "spray-json" % "1.3.6",
  "org.scalatest" % "scalatest_2.13" % "3.1.0" % "test",
  "org.scalafx" % "scalafx_2.13" % "15.0.1-R21",
  "com.typesafe.akka" %% "akka-actor" % "2.6.4",
  "com.h2database" % "h2" % "1.4.200"
)

lazy val commonConfiguration = Seq(
  name := "dbtarzan",

  version := versionNumber,

  scalaVersion := "2.13.6",

  Compile / mainClass := Some("dbtarzan.gui.Main"),

  Compile / scalaSource := baseDirectory.value / ".." / "src" / "main" / "scala",

  Test / scalaSource := baseDirectory.value / ".." / "src" / "test" / "scala",

  Compile / resourceDirectory := baseDirectory.value / ".." / "src" / "main" / "resources",

  Test / resourceDirectory := baseDirectory.value / ".." / "src" / "test" / "resources",

  Compile / scalacOptions ++= Seq("-Ywarn-unused:imports"),
  Compile / scalacOptions --= Seq("-Xfatal-warnings"),
  buildStrategy()
// scalacOptions += "-Ylog-classpath"
)

def buildStrategy() = {
  assembly / assemblyMergeStrategy := {
    case "module-info.class" => MergeStrategy.discard
    case x => {
      val oldStrategy = (assembly / assemblyMergeStrategy).value
      oldStrategy(x)
    }
  }
}

def buildProject(name: String) = {
  val javaFXModules = Seq("base", "controls", "graphics", "media")
  val javaFXLibraries = javaFXModules.map(module =>
    "org.openjfx" % s"javafx-$module" % "15" classifier name
  )
  Project(name, file(s"prj${name}"))
    .settings( commonConfiguration)
    .settings(
      libraryDependencies ++= standardLibraries ++ javaFXLibraries
    )
    .settings(
      excludeDependenciesOfOtherOses(name)
    )
}

def excludeDependenciesOfOtherOses(name: String) = {
  assembly / assemblyExcludedJars ++= {
    val osnamesBut = Seq("win", "mac", "linux").filter(n => n != name)
    val cp = (assembly / fullClasspath).value
    cp filter { f => osnamesBut.exists(osName => f.data.getName.contains(osName)) }
  }
}

lazy val linux = buildProject("linux")
    .settings(Seq(
      Debian / debianPackageDependencies ++= Seq("openjdk-11-jre"),
      bashScriptExtraDefines += """addApp "--configPath=$HOME/.config/dbtarzan"""",
      maintainer := "Andrea Ferrandi <ferrandi.andrea@gmail.com>",
      packageSummary := "DBTarzan Package",
      packageDescription := "DBTarzan, the database browser"
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

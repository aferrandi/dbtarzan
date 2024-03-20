import sbt.ExclusionRule

import scala.collection.immutable.Seq
import scala.sys.process.*

fork := true

val versionNumber = "1.33"
val scala3Version = "3.3.1"
val javaFxVersion = "20"
val scalaFxVersion = "20.0.0-R31"
version := versionNumber
scalaVersion := scala3Version


lazy val commonConfiguration = Seq(
  name := "dbtarzan",

  version := versionNumber,

  scalaVersion :=  scala3Version,

  Compile / scalaVersion := scala3Version,

  Compile / mainClass := Some("dbtarzan.gui.Main"),

  Compile / scalaSource := baseDirectory.value / ".." / "src" / "main" / "scala",

  Test / scalaSource := baseDirectory.value / ".." / "src" / "test" / "scala",

  Compile / resourceDirectory := baseDirectory.value / ".." / "src" / "main" / "resources",

  Test / resourceDirectory := baseDirectory.value / ".." / "src" / "test" / "resources",

  Compile / scalacOptions ++= Seq("-Xfatal-warnings", "-Ykind-projector", "-deprecation", "-feature", "-language:implicitConversions"),

  buildStrategy()

      //  Compile / scalacOptions ++= Seq("-Ywarn-unused:imports"),
//  Compile / scalacOptions --= Seq("-Xfatal-warnings"),
// scalacOptions += "-Ylog-classpath"
)

lazy val standardLibraries = Seq (
  "com.github.losizm" %% "grapple" % "13.0.0",
  "org.apache.pekko" %% "pekko-actor" % "1.0.2",
  "com.h2database" % "h2" % "2.2.220" % Test,
  "org.scalatest" %% "scalatest" % "3.2.16" % Test,
  ("org.scalafx" %% "scalafx" % scalaFxVersion).excludeAll(
    // you cannot use the ibraries requested by scalafx because they are only the ones in the OS of this PC
    ExclusionRule(organization="org.openjfx")
  )
)

def buildStrategy() = {
  assembly / assemblyMergeStrategy := {
    case "module-info.class" => MergeStrategy.discard
    case PathList("META-INF", _*) => MergeStrategy.discard
    case "application.conf" => MergeStrategy.concat
    case "javafx-web" => MergeStrategy.discard
    case x => {
      val oldStrategy = (assembly / assemblyMergeStrategy).value
      oldStrategy(x)
    }
  }
}

def buildProject(name: String) = {
  // we need to add web and swing to avoid compile errors, but we remove them later
  val javaFXModules = Seq("base", "controls", "graphics", "media", "web", "swing")
  val javaFXLibraries = javaFXModules.map(module =>
    "org.openjfx" % s"javafx-$module" % javaFxVersion classifier name
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

def onlyFilesIncludingTextInName(cp: Classpath, toIncludes: Seq[String]) = {
  cp.filter(f => toIncludes.exists(toInclude => f.data.getName.contains(toInclude)))
}
def excludeDependenciesOfOtherOses(name: String) = {
  val osnamesBut = Seq("win", "mac", "linux").filter(n => n != name)
  val modulesBut = Seq("javafx-web", "javafx-swing")
  assembly / assemblyExcludedJars ++= {
    val cp = (assembly / fullClasspath).value
    val toExclude = onlyFilesIncludingTextInName(cp, osnamesBut) ++ onlyFilesIncludingTextInName(cp, modulesBut)
    toExclude
  }
}

lazy val linux = buildProject("linux")
    .settings(Seq(
      Debian / debianPackageDependencies ++= Seq("openjdk-17-jre"),
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
  "mkwin/packageexe.sh "+rootDir+" "+version.value +" " + scalaVersion.value !
}

lazy val packageMacOS = taskKey[Unit]("Packages MacOS app")
packageMacOS := {
  val macOsDir = baseDirectory.value / "mkmacosx"
  (mac/assembly).value // dependency
  "mkmacosx/package.sh "+macOsDir+" "+version.value +" " + scalaVersion.value !
}

lazy val packageSnap = taskKey[Unit]("Packages Snap")
packageSnap := {
  val rootDir = baseDirectory.value
  (linux/assembly).value // dependency
  "mksnap/create.sh "+rootDir+" "+ version.value +" " + scalaVersion.value !
}

addCommandAlias("packageAll", 
	"; debian:packageBin" +
  "; packageWin" +
  "; packageMacOS" +
  "; packageSnap"
)

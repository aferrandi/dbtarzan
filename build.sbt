import sbt.ExclusionRule

import scala.sys.process.*

fork := true

val versionNumber = "1.28"
val scala3Version = "3.1.3"
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

  Compile / scalacOptions ++= Seq("-Xfatal-warnings", "-Ykind-projector", "-deprecation"),

  buildStrategy()

      //  Compile / scalacOptions ++= Seq("-Ywarn-unused:imports"),
//  Compile / scalacOptions --= Seq("-Xfatal-warnings"),
// scalacOptions += "-Ylog-classpath"
)

lazy val standardLibraries = Seq (
  ("io.spray" %% "spray-json" % "1.3.6").cross(CrossVersion.for3Use2_13),
  ("org.apache.pekko" %% "pekko-actor" % "1.0.1").cross(CrossVersion.for3Use2_13),
  "com.h2database" % "h2" % "2.2.220" % Test,
  "org.scalatest" %% "scalatest" % "3.2.16" % Test,
  ("org.scalafx" %% "scalafx" % "20.0.0-R31").excludeAll(
    ExclusionRule(organization="org.openjfx", name="javafx-web"),
    ExclusionRule(organization="org.openjfx", name="javafx-swing"),
    ExclusionRule(organization="org.openjfx", name="javafx-fxml"),
    ExclusionRule(organization="org.openjfx", name="javafx-swt")
  )
)



def buildStrategy() = {
  assembly / assemblyMergeStrategy := {
    case "module-info.class" => MergeStrategy.discard
    case PathList("META-INF", _*) => MergeStrategy.discard
    case "application.conf" => MergeStrategy.concat
    case x => {
      val oldStrategy = (assembly / assemblyMergeStrategy).value
      oldStrategy(x)
    }
  }
}

def buildProject(name: String) = {
  Project(name, file(s"prj${name}"))
    .settings(commonConfiguration)
    .settings(
      libraryDependencies ++= standardLibraries
    )
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

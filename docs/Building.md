---

layout: default
title: Build the application
resource: true
categories: [Code]

---

## Build the application

DBTarzan uses now **Java JVM 11** and **Scala 2.13**.

**JavaFX** uses different libraries for each OS. Therefore DBTarzan gets built for the following OSes:
* Windows
* Linux
* MacOS

Being written in scala, the project uses [sbt](https://www.scala-sbt.org/) to build the application.
To **execute** the application:

* Om the project root directory, run **sbt** (which uses the [build.sbt file](https://github.com/aferrandi/dbtarzan/blob/master/build.sbt)).
* Once inside the sbt shell, select the OS you are working with, writing one of the following:
  * **project win** 
  * **project linux**
  * **project mac**
* The command **run** executes the application.


To build an installable version of the application, in sbt, type the following:
* **packageWin** to build a Windows package. It uses [launch4j](http://launch4j.sourceforge.net/) and [NSIS](https://sourceforge.net/projects/nsis/) as described in the [mkwin](https://github.com/aferrandi/dbtarzan/tree/master/mkwin) directory.
* **packageSnap** to build a [snap](https://snapcraft.io/about) container for Linux, as described in the [mksnap](https://github.com/aferrandi/dbtarzan/tree/master/mksnap) directory.
* **packageMacOS** to build a MacOS package, built as a MacOS app zip file, using the [universal java application stub](https://github.com/tofi86/universalJavaApplicationStub) to execute the JVM application, as described in the [macosx](https://github.com/aferrandi/dbtarzan/tree/master/macosx) directory.
* **debian:packageBin** to build a Linux deb package.

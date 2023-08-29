DbTarzan installation
=====================

dbtarzan_XXX.exe : executable for Windows. Just copy it to a directory and run it. dbtarzan needs a [connections.config file](https://aferrandi.github.io/dbtarzan/The-database-connections-configuration-file) in the same directory. 

dbtarzan_XXX_assembly_YYY.jar : (where YYY is linux, win or mac) executable jar for Linux, MacOSX and other OSes. Just copy it to a directory and run it. dbtarzan needs a [connections.config file](https://aferrandi.github.io/dbtarzan/The-database-connections-configuration-file) in the same directory. 

dbtarzan_XXX_all.deb : Debian pacakge for Linux. Download it and install it as a debian package, normally it is enough to double-click on the .deb file from the file manager. It is kind of experimental, because it can't be executed from the system menu, but it starts by typing "dbtarzan" in the shell; and because the debian installer sees it as "non free software", even if it is open source.     


DBTarzan changelog
=================

- Release 1.29

Replaced the (not more maintained) JSON Spray library with the Scala 3 compatible Grapple library.

Uses only Scala 3, without Scala 2.13 compatibility layer

In the vertical row view new text box to filter the displayed fields

- Release 1.28

Updated to Scala 3, Java 17, JavaFX 20, ScalaFX 20.

Use Pekko instead of Akka (which is not more open source), for the actors.

Reads the values in the databases using the types INT, FLOAT, STRING, which should improve the performance and allow to sort the columns in the GUI table correctly.







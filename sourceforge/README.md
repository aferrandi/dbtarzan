DbTarzan installation
=====================

dbtarzan_XXX.exe : executable for Windows. Just copy it to a directory and run it. dbtarzan needs a [connections.config file](https://aferrandi.github.io/dbtarzan/The-database-connections-configuration-file) in the same directory. 

dbtarzan_XXX_assembly_YYY.jar : (where YYY is linux, win or mac) executable jar for Linux, MacOSX and other OSes. Just copy it to a directory and run it. dbtarzan needs a [connections.config file](https://aferrandi.github.io/dbtarzan/The-database-connections-configuration-file) in the same directory. 

dbtarzan_XXX_all.deb : Debian pacakge for Linux. Download it and install it as a debian package, normally it is enough to double-click on the .deb file from the file manager. It is kind of experimental, because it can't be executed from the system menu, but it starts by typing "dbtarzan" in the shell; and because the debian installer sees it as "non free software", even if it is open source.     


DBTarzan changelog
=================
- Release 1.34

Possibility to filter the foreign keys with a text, useful when having many foreign keys in the table.

Possibility to filter the databases names with a text, useful when having many databases. In cases of composites matches also the simple databases contained in the composites.

Uses JavaFX 21 and the related ScalaFX library.

Fixed the filtering of tables so that also the fields are matched against the pattern.

- Release 1.33

Shows the number of rows in the table pressing the button Rows number under the query text o the bottom right panel.

New log actor that writes all log in the dbactor.log file and sends the INFO, WARNING, ERROR logs to the GUI so that they can be displayed in the log view as they did before.











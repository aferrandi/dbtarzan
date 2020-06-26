DbTarzan installation
=====================

dbtarzan_XXX.exe : executable for Windows. Just copy it to a directory and run it. dbtarzan needs a [connections.config file](https://aferrandi.github.io/dbtarzan/The-database-connections-configuration-file) in the same directory. 

dbtarzan_XXX_assembly_YYY.jar : (where YYY is linux, win or mac) executable jar for Linux, MacOSX and other OSes. Just copy it to a directory and run it. dbtarzan needs a [connections.config file](https://aferrandi.github.io/dbtarzan/The-database-connections-configuration-file) in the same directory. 

dbtarzan_XXX_all.deb : Debian pacakge for Linux. Download it and install it as a debian package, normally it is enough to double-click on the .deb file from the file manager. It is kind of experimental, because it can't be executed from the system menu, but it starts by typing "dbtarzan" in the shell; and because the debian installer sees it as "non free software", even if it is open source.     


DBTarzan changelog
=================

- Release 1.23

Upgraded to JavaFX 14

Adding a connection to the database now you can select the schema from the list of the available ones

Possibility to copy the column names of the table from the "columns description table"

Have always a row selected in an open table, so that it is possible to show a vertical row view.


- Release 1.22

Fields/columns suggestions in the where clause text box

Ctrl-Del closes the current tab

DBTarzan as snap now opens the browser to show the help/documentation


